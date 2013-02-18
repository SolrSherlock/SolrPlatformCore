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

import org.nex.config.ConfigPullParser;
import org.topicquests.solr.api.ISolrClient;
import org.topicquests.solr.api.ISolrDataProvider;
import org.topicquests.solr.api.ISolrQueryIterator;
import org.topicquests.util.LoggingPlatform;
import org.topicquests.util.Tracer;

/**
 * 
 * @author park
 *
 */
public class SolrEnvironment {
	private LoggingPlatform log = LoggingPlatform.getInstance();
	private Hashtable<String,Object>props;
	private ISolrClient solr;
	private ISolrDataProvider database;

	/**
	 * @param p
	 */
	public SolrEnvironment(Hashtable<String,Object>p) {
		init(p);
	}
	
	public SolrEnvironment() {
		ConfigPullParser p = new ConfigPullParser("config-props.xml");
		init(p.getProperties());
	}
	
	void init(Hashtable<String,Object>p) {
		props = p;
		try {
			solr = new Solr3Client(getStringProperty("SolrURL")); //TODO Solr4Client for testing
			record("Solr4Client started");
			System.out.println("AAAA "+getStringProperty("MapCacheSize"));
			int cachesize = Integer.parseInt(getStringProperty("MapCacheSize"));
			database = new SolrDataProvider(solr,cachesize );
		} catch (Exception e) {
			logError(e.getMessage(),e);
			e.printStackTrace();
		}
		logDebug("Started");
	}
	
	public ISolrClient getSolrClient() {
		return solr;
	}
	
	public ISolrDataProvider getDataProvider() {
		return database;
	}
	public Map<String,Object> getProperties() {
		return props;
	}
	
	public String getStringProperty(String key) {
		return (String)props.get(key);
	}
		
	/**
	 * Return a new {@link ISolrQueryIterator}
	 * @return
	 */
	public ISolrQueryIterator getQueryIterator() {
		return new SolrQueryIterator(this);
	}
	
	
	public void shutDown() {
		//
	}
	/////////////////////////////
	// Utilities
	public void logDebug(String msg) {
		log.logDebug(msg);
	}
	
	public void logError(String msg, Exception e) {
		log.logError(msg,e);
	}
	
	public void record(String msg) {
		log.record(msg);
	}

	public Tracer getTracer(String name) {
		return log.getTracer(name);
	}
}
