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
package org.topicquests.solr.api;

import java.util.Map;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrServer;
import org.topicquests.common.api.IResult;

/**
 * @author park
 *
 */
public interface ISolrClient {

	 /**
	  * Returns the SolrJ server
	  * @return
	  */
	 SolrServer getSolrServer();
	 
	/**
	 * Run a query based on <code>queryString</code>
	 * @param queryString
	 * @param start TODO
	 * @param count TODO
	 * @return  NamedList<Object> in result or error string
	 */
	 IResult runQuery(String queryString, int start, int count);
	 
	/**
	 * Update has the effect of removing then replacing a document
	 * May have to deal with _version_ field and optimistic locking
	 * @param fields
	 * @return can return an error message
	 * @deprecated use <code>partialUpdateData</code>
	 * NOTE: there are cases where surgery is pretty major, so we still need this
	 */
	 IResult updateData(Map<String,Object>fields);
	 
	 /**
	  * <p>Map must include locator, version and any other fields that are changed</p>
	  * <p>Partial update only applies to Solr4, and requires that all fields are Stored</p>
	  * @param fields
	  * @return
	  */
	 IResult partialUpdateData(Map<String,Object>fields);
	 
	/**
	 * Removes an entire document //TODO needs testing
	 * @param locator
	 * @return an Integer for result status
	 */
	 IResult deleteByLocator(String locator);
	 
	/**
	 * Add data based on fields and values in <code>fields</code>
	 * @param fields
	 * @return can return an error message
	 */
	 IResult addData(Map<String,Object>fields);
	 
	 /**
	  * Add several documents
	  * @param documents
	  * @return
	  */
	 IResult addData(Collection<Map<String,Object>> documents);
	 
	/**
	 * Fetch by way of the node's <code>locator</code> field
	 * @param locator
	 * @param start
	 * @param count
	 * @return
	 */
	 IResult getByProxyLocator(String locator, int start, int count);
	 
	 /**
	  * Shutdown the Solr server
	  */
	 void shutDown();
}
