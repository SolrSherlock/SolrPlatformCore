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
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.schema.TrieDateField;
import org.apache.solr.client.solrj.request.UpdateRequest;

import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.common.ResultPojo;
import org.topicquests.solr.api.ISolrClient;
import org.topicquests.util.LoggingPlatform;


/**
 * @author park
 * For SolrCloud use
 */
public class SolrCloudClient implements ISolrClient {
	private LoggingPlatform log = LoggingPlatform.getLiveInstance();
	private CloudSolrServer server;
	private LBHttpSolrServer serverServer;
	private CloudSolrServer updateServer;
	private LBHttpSolrServer updateServerServer;
	private CloudSolrServer harvestServer;
	private LBHttpSolrServer harvestServerServer;
	//TODO make that a config value
	private boolean shouldCommit = false;
	
	/////////////////////////////////////////////////
	// API

	/**
	 * @param solrURL TODO: fix to 
	 * zkHost The client endpoint of the zookeeper quorum containing the cloud state,
	 */
	@Override
	public void init(List<String> solrURLs, List<String> zookeeperURLs) throws Exception {
		log.logDebug("SolrCloudClient- "+solrURLs+" "+zookeeperURLs);
		StringBuilder sbuf = new StringBuilder();
		int len = solrURLs.size();
		for (int i=0;i<len;i++) {
			sbuf.append(solrURLs.get(i));
			sbuf.append(",");
		}
		String solrURL = sbuf.toString();
		if (solrURL.endsWith(","))
			solrURL = solrURL.substring(0, (solrURL.length()-1));
		StringBuilder zbuf = new StringBuilder();
		len = zookeeperURLs.size();
		for (int i=0;i<len;i++) {
			zbuf.append(zookeeperURLs.get(i));
			zbuf.append(",");
		}
		String zookeeperURL = zbuf.toString();
		if (zookeeperURL.endsWith(","))
			zookeeperURL = zookeeperURL.substring(0, (zookeeperURL.length()-1));
		log.logDebug("SolrCloudClient-1 "+solrURL+" | "+zookeeperURL);
		try {
			//TODO make default collection a config property
			//NOTE: can pass in a client (String baseURL, HttpClient client)
			//to which you set params
			//We use LBHttpSolrServer so that we can setup query params below
			// there is no way to getParams() from CloudSolrServer
			serverServer = new LBHttpSolrServer(solrURL);
			System.out.println("SolrCloudClient-2 "+serverServer);
			server = new CloudSolrServer(zookeeperURL, serverServer);
	    	server.setDefaultCollection("collection1");
			System.out.println("SolrCloudClient-3 "+server);
			harvestServerServer = new LBHttpSolrServer(solrURL);
			harvestServer = new CloudSolrServer(solrURL, harvestServerServer);
	    	harvestServer.setDefaultCollection("collection1");
			updateServerServer = new LBHttpSolrServer(solrURL);
			updateServer = new CloudSolrServer(zookeeperURL, updateServerServer);
	    	updateServer.setDefaultCollection("collection1");
	System.out.println("SolrCloudClient-2 "+server);
			serverServer.getHttpClient().getParams().setParameter("update.chain", "merge");
			harvestServerServer.getHttpClient().getParams().setParameter("update.chain", "harvest");
			updateServerServer.getHttpClient().getParams().setParameter("update.chain", "partial");
		} catch (Exception e) {
			System.out.println("SHIT "+e.getMessage());
			log.logError(e.getMessage(), e);
		}
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
		log.logDebug("SolrCloudClient.runQuery- "+queryString+" "+start+" "+count);
		IResult result = new ResultPojo();
		SolrQuery parameters = new SolrQuery();
		parameters.set("q", queryString);
		parameters.setStart(start);
		if (count > -1)
			parameters.setRows(count);
		log.logDebug("SolrCloudClient.runQuery-1 "+parameters.toString());
		try {
			QueryResponse x = server.query(parameters);
			log.logDebug("SolrCloudClient.runQuery-2 "+x.getResults());
			result.setResultObject(x.getResults());
		} catch (Exception e) {
			log.logError("SolrCloudClient.runQuery "+e.getMessage()+" "+queryString, e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}
	
	/**
	 * Update has the effect of removing then replacing a document
	 * @param fields
	 * @return
	 * @deprecated
	 * NOTE: sometimes surgery overwhelms partial update
	 */
	public IResult updateData(Map<String,Object>fields) {
		return addData(fields);
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
			int status = ur.getStatus();
			//TODO not sure about this one
			server.commit();
			result.setResultObject(new Integer(status));
		} catch (Exception e) {
			result.addErrorString(e.getMessage());
		}
		return result;
		
	}
	/**
	 * Add data based on fields and values in <code>fields</code>
	 * @param fields
	 * @return <code>null</code> or status code as Integer if error with error string
	 * TODO: SolrInputDocument.putAll(Map<String,Object) might work in some cases
	 */
	public IResult addData(Map<String,Object>fields) {
		IResult result = new ResultPojo();
		if (fields.isEmpty()) {
			//result.addErrorString("SolrClient got an empty document");
			return result;
		}
		log.logDebug("SolrCloudClient.addData-1 "+fields.size());
		int status = 0;
		try {
			SolrInputDocument document = mapToDocument(fields);
			System.out.println("SolrCloudClient.addData-2 "+document);
			UpdateResponse response = null;
			if (shouldCommit) {
				response = server.add(document);
				server.commit();
			} else
				response = server.add(document, 2000);
			status = response.getStatus();
		} catch (Exception e) {
			result.addErrorString(e.getMessage());
			log.logError("SolrCloudClient.addData error-1 "+e.getMessage()+" "+fields,e);
		}
		result.setResultObject(new Integer(status));
		return result;
	}
	
	@Override
	public IResult addData(Collection<Map<String, Object>> documents) {
		IResult result = new ResultPojo();
		Iterator<Map<String,Object>>itr = documents.iterator();
		int status = 0;
		try {
			SolrInputDocument document = null;
			Map<String,Object>fields = null;
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			while (itr.hasNext()) {
				fields = itr.next();
				if (!fields.isEmpty()) {
					document = mapToDocument(fields);
					docs.add(document);
				} //else
					// result.addErrorString("SolrClient got an empty document");
			}
			UpdateResponse response = null;
			if (shouldCommit) {
				response = server.add(document);
				server.commit();
			} else
				response = server.add(document, 2000);
			status = response.getStatus();
		} catch (Exception e) {
			result.addErrorString(e.getMessage());
			log.logError("SolrCloudClient.addData error-2 "+e.getMessage()+" "+documents,e);
		}
		result.setResultObject(new Integer(status));

		return result;
	}


	/**
	 * <p>Convert a Map of fields to a {@link SolrInputDocument}</p>
	 * <p>Must deal with a variety of field types</p>
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	SolrInputDocument mapToDocument(Map<String,Object> fields) throws Exception {
		log.logDebug("SolrCloudClient.mapToDocument- "+fields);
		SolrInputDocument document = new SolrInputDocument();
		Iterator<String>keys = fields.keySet().iterator();
		String key;
		List<String>lobj;
		Object o;
		while (keys.hasNext()) {
			key = keys.next();
			o = fields.get(key);
			System.out.println("SolrCloudClient.addData-2 "+key+" | "+o);
			//here we try to catch the obvious ones
			//TODO expand the tests for Float
			if (key.startsWith(ITopicQuestsOntology.LABEL_PROPERTY) || 
				key.startsWith(ITopicQuestsOntology.DETAILS_PROPERTY)) {
				if (o instanceof String)
					document.addField(key, QueryUtil.escapeNodeData((String)o));
				else if (o instanceof List)
					document.addField(key, escapeQueryCulprits((List<String>)o));
				else
					document.addField(key, escapeMapQueryCulprits((Map<String,Object>)o));
				
					
			} else if (o instanceof String ||
				o instanceof Double ||
				o instanceof Boolean ||
				o instanceof Map || // required for partial updates only
				o instanceof Long ||
				o instanceof Float
				) {
//				System.out.println("SolrCloudClient.addData-2 "+o); 
					document.addField(key, o);
			} else if (o instanceof List) {
//				System.out.println("SolrCloudClient.addData-2list "+o); 
				lobj = (List<String>)o;
				Iterator<String>itr = lobj.iterator();
				while (itr.hasNext()) {
					//TODO if the list is just a list of single strings, e.g. mostly locators,
					//then we don't want to escape them
					//document.addField(key,  ClientUtils.escapeQueryChars(vx));
					//document.addField(key, escapeQueryCulprits(vx));
					//unit tests suggest we don't need to do any escapes
					//TODO pay attention to this issue
					document.addField(key, itr.next());
				} 
			} else if (key.equals(ITopicQuestsOntology.CREATED_DATE_PROPERTY) ||
					   key.equals(ITopicQuestsOntology.LAST_EDIT_DATE_PROPERTY)) {
				//TODO TrieDateField formatExternal is deprecated
					document.addField(key,  TrieDateField.formatExternal((Date)o));
			} else
				throw new Exception ("SolrCloudClient.addData fail: "+key+" "+o);
		}		
		return document;
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
		System.out.println("QQQ "+q);
		IResult result = runQuery(q, start, count);
		return result;		
	}

	@Override
	public IResult partialUpdateData(Map<String, Object> fields) {
		log.logDebug("SolrCloudClient.partialUpdateData "+fields);
		return addUpdateData(fields);
	}
	

	@Override
	public void shutDown() {
		server.shutdown();
		updateServer.shutdown();
		harvestServer.shutdown();
	}


	@Override
	public IResult addDataNoMerge(Map<String, Object> fields) {
		IResult result = new ResultPojo();
		if (fields.isEmpty()) {
			//result.addErrorString("SolrClient got an empty document");
			return result;
		}
		log.logDebug("SolrCloudClient.addDataNoMerge-1 "+fields.size());
		int status = 0;
		try {
			SolrInputDocument document = mapToDocument(fields);
			System.out.println("SolrCloudClient.addData-2 "+document);
			UpdateResponse response = null;
			if (shouldCommit) {
				response = harvestServer.add(document);
				server.commit();
			} else
				response = harvestServer.add(document, 2000);
			status = response.getStatus();
		} catch (Exception e) {
			result.addErrorString(e.getMessage());
			log.logError("SolrCloudClient.addDataNoMerge error-1 "+e.getMessage()+" "+fields,e);
		}
		result.setResultObject(new Integer(status));
		return result;
	}


	/////////////////////////////////////////////////
	// Support
	
	private Map<String,Object> escapeMapQueryCulprits(Map<String,Object> m) {
		Map<String,Object>result = m;
		Object obj;
		List<String>vx;
		String key;
		String vs;
		Iterator<String>itr = m.keySet().iterator();
		while (itr.hasNext()) {
			key = itr.next();
			obj = m.get(key);
			if (obj instanceof List) {
				vx = escapeQueryCulprits((List<String>)obj);
				result.put(key, vx);
			} else if (obj instanceof String) {
				vs = QueryUtil.escapeNodeData((String)obj);
				result.put(key, vs);
			}
			
		}
		return result;
	}
	
	private List<String> escapeQueryCulprits(List<String>culprits) {
		int len = culprits.size();
		for (int i=0;i<len;i++) {
			culprits.set(i, QueryUtil.escapeNodeData(culprits.get(i)));
		}
		return culprits;
	}

	///////////////////////////////////////////////
	// Partial updates use their own kind of map
	// {
	//  locator=<thelocator>, 
	//  fieldA={set=[values]}, 
	//  fieldB={set=[<values>]}, 
	//  fieldC={set=[values]}

	 IResult addUpdateData(Map<String,Object>fields) {
		IResult result = new ResultPojo();
		if (fields.isEmpty()) {
			//result.addErrorString("SolrClient got an empty document");
			return result;
		}
		log.logDebug("SolrCloudClient.addUpdateData-1 "+fields.size());
		int status = 0;
		try {
			SolrInputDocument document = mapToDocument(fields); 
			log.logDebug("SolrCloudClient.addUpdateData-2 "+document);
			UpdateRequest ur = new UpdateRequest();
			ur.add(document);
			ur.setCommitWithin(2000);
			UpdateResponse response = ur.process(updateServer);
			status = response.getStatus();
			//commitWithin means the commit will happen regardless
			if (shouldCommit)
				updateServer.commit();
		} catch (Exception e) {
			result.addErrorString(e.getMessage());
			log.logError("SolrCloudClient.addUpdateData error-1 "+e.getMessage()+" "+fields,e);
		}
		result.setResultObject(new Integer(status));

		return result;
	}
}
