/*
 * Copyright 2012, TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.topicquests.solr;
import java.util.*;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.client.solrj.util.ClientUtils;

import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.common.ResultPojo;
import org.topicquests.solr.api.ISolrClient;

/**
 * @author park
 * <p> First issue is doing updates. Also, something about partial updates
 * @see http://solr.pl/en/2012/07/09/solr-4-0-partial-documents-update/
 * for more on that
 * @see http://stackoverflow.com/questions/11791803/update-a-new-field-to-existing-document
 * "In Solr 4 they implemented feature like that, but they have a condition: all fields have to be stored, not just indexed"
 * Still looking for how to do a full node update:
 *   apparently, if some fields in a document are not stored, must stay with delete then add
 * </p>
 * <p>Version, the field _version_ is important</p>
 * <p>If we stay with delete then add, we are forced to test _version_ field and
 * generate our own OptimisticLockException.
 * That will run SLOW
 * </p>
 * NOTE: not yet completed or tested
 */
public class Solr4Client implements ISolrClient {
	private CloudSolrServer server; //TODO SolrCloudServer

	/**
	 * 
	 */
	public Solr4Client(String solrURL) throws Exception {
		server = new CloudSolrServer(solrURL);
		//server.setParser(new XMLResponseParser()); //TODO
	}

	public SolrServer getSolrServer() {
		return server;
	}
	
	/**
	 * Run a query based on <code>queryString</code>
	 * @param queryString
	 * @param start TODO
	 * @param count TODO
	 * @return  NamedList<Object> in result or error string
	 */
	public IResult runQuery(String queryString, int start, int count) {
		System.out.println("Solr4Client.runQuery- "+queryString+" "+start+" "+count);
		IResult result = new ResultPojo();
		SolrQuery parameters = new SolrQuery();
		parameters.set("q", queryString);
		parameters.setStart(start);
		if (count > -1)
			parameters.setRows(count);
		//force result as JSON
//		parameters.set("wt", "json");
		System.out.println("Solr4Client.runQuery-1 "+parameters.toString());
		try {
			QueryResponse x = server.query(parameters);
//			System.out.println("Solr3Client.runQuery "+x.getStatus());
//			System.out.println("XXXX "+x.getHeader());
//			System.out.println("YYYY "+x.getResponse());
			System.out.println("ZZZZ "+x.getResults());
			result.setResultObject(x.getResults());
		} catch (Exception e) {
			//TODO log the error
			e.printStackTrace();
			result.addErrorString(e.getMessage());
		}
		return result;
	}
	
	/**
	 * Update has the effect of removing then replacing a document
	 * May have to deal with _version_ field and optimistic locking
	 * @param fields
	 * @return can return an error message
	 */
	//TODO fix this: there is a different way to do an update
	// looks like we must stay with this.
	//STILL, must deal with _version_ field
	public IResult updateData(Map<String,Object>fields) {
		IResult result = new ResultPojo();
		IResult temp = deleteByLocator((String)fields.get(ITopicQuestsOntology.LOCATOR_PROPERTY));
		if (!temp.hasError()) {
			result = addData(fields);
		} else
			result.addErrorString(temp.getErrorString());
		return result;
	}
	
	/**
	 * Removes an entire document //TODO needs testing
	 * @param locator
	 * @return
	 */
	public IResult deleteByLocator(String locator) {
		IResult result = new ResultPojo();
		try {
			UpdateResponse ur = server.deleteById(locator);
			server.commit();
		} catch (Exception e) {
			result.addErrorString(e.getMessage());
		}
		return result;
		
	}
	/**
	 * Add data based on fields and values in <code>fields</code>
	 * @param fields
	 * @return <code>null</code> or status code as Integer if error with error string
	 */
	public IResult addData(Map<String,Object>fields) {
		IResult result = new ResultPojo();
		if (fields.isEmpty())
			return result;
		System.out.println("Solr3Client.addData "+fields.size());
		int status = 0;
		try {
			SolrInputDocument document = new SolrInputDocument();
			Iterator<String>keys = fields.keySet().iterator();
			String key;
			List<String>lobj;
			Object o;
			while (keys.hasNext()) {
				key = keys.next();
				o = fields.get(key);
				System.out.println("Solr3Client.addData-1 "+key+" "+o);
				if (o instanceof String) {
			//		if (key.equals(ITopicQuestsOntology.CREATED_DATE_PROPERTY))
						document.addField(key, (String)o);
			//		else
			//			document.addField(key, ClientUtils.escapeQueryChars((String)o));
						
				} else if (o instanceof List) {
					lobj = (List<String>)o;
					Iterator<String>itr = lobj.iterator();
					while (itr.hasNext())
						document.addField(key,  ClientUtils.escapeQueryChars(itr.next()));
					//TODO add Booleans and other things
				} else
					throw new Exception ("Solr4Client.addData fail: "+key+" "+o);
			}
			
			UpdateResponse response = server.add(document);
			status = response.getStatus();
			//TODO full commit or soft commit?
			server.commit();
		} catch (Exception e) {
			result.addErrorString(e.getMessage());
		}
		result.setResultObject(new Integer(status));
		return result;
	}
	
	/**
	 * Fetch by way of the node's <em>locator</em> field
	 * @param locator
	 * @param start
	 * @param count
	 * @return
	 */
	public IResult getByProxyLocator(String locator, int start, int count) {
		String q = ITopicQuestsOntology.LOCATOR_PROPERTY+":"+locator;
		IResult result = runQuery(q, start, count);
		return result;		
	}

	@Override
	public IResult partialUpdateData(Map<String, Object> fields) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void shutDown() {
		server.shutdown();
	}

	@Override
	public IResult addData(Collection<Map<String, Object>> documents) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult addDataNoMerge(Map<String, Object> fields) {
		// TODO Auto-generated method stub
		return null;
	}

}
