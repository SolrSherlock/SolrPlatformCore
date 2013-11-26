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

import org.topicquests.common.api.IConsoleDisplay;
import org.topicquests.model.Environment;
import org.topicquests.solr.api.ISolrClient;
import org.topicquests.solr.api.ISolrMergeImplementation;
import org.topicquests.solr.api.ISolrQueryModel;
import org.topicquests.solr.api.ISolrQueryIterator;
import org.topicquests.util.LoggingPlatform;
import org.topicquests.util.Tracer;

/**
 * 
 * @author park
 *
 */
public class SolrEnvironment extends Environment {
	private ISolrClient solr;
	private ISolrQueryModel model;
	private IConsoleDisplay host;

	/**
	 * @param p
	 */
	public SolrEnvironment(Map<String,Object>p) {
		super(p,true);
		init();
	}
	
	public SolrEnvironment() {
		super(true);
		init();
	}
	
	void init() {
		try {
			List<List<String>>solrs = (List<List<String>>)getProperties().get("SolrURLs");
			List<List<String>>zookeeps= (List<List<String>>)getProperties().get("ZKHosts");
			log.logDebug("SolrEnvironment.init "+solrs+" "+zookeeps);
			List<String> solrservers = new ArrayList<String>();
			int len = solrs.size();
			for (int i=0;i<len;i++)
				solrservers.add(((List<String>)solrs.get(i)).get(1));
			List<String> zookeepers = new ArrayList<String>(); 
			len = zookeeps.size();
			for (int i=0;i<len;i++)
				zookeepers.add(((List<String>)zookeeps.get(i)).get(1));	
			String ccp = getStringProperty("SolrClient");
System.out.println("SolrEnvironment-1 "+ccp);
			Class o = Class.forName(ccp);
			solr = (ISolrClient)o.newInstance();
			solr.init(solrservers, zookeepers);
System.out.println("SolrEnvironment-2 "+solr);
			record("SolrClient started");
System.out.println("AAAA "+getStringProperty("MapCacheSize"));
			int cachesize = Integer.parseInt(getStringProperty("MapCacheSize"));
			database = new SolrDataProvider(this,cachesize );
			ISolrMergeImplementation merger;
			String cp = getStringProperty("MergeImplementation");
			//this installation might not deal with merge bean
			if (cp != null) {
				o = Class.forName(cp);
				merger = (ISolrMergeImplementation)o.newInstance();
				merger.init(this);
				database.setMergeBean(merger);
			}
			model = new SolrQueryModel(this);
			String bs = getStringProperty("ShouldBootstrap");
			boolean shouldBootstrap = false; // default value
			if (bs != null)
				shouldBootstrap = bs.equalsIgnoreCase("Yes");
			if (shouldBootstrap)
				bootstrap();
		} catch (Exception e) {
System.out.println("SolrEnvironment error "+e.getMessage());
			logError(e.getMessage(),e);
			e.printStackTrace();
		}
		logDebug("SolrEnvironment Started");
	}
	
	public ISolrQueryModel getSolrModel() {
		return model;
	}
	
	public ISolrClient getSolrClient() {
		return solr;
	}
	
//	public ISolrDataProvider getDataProvider() {
//		return database;
//	}
		
	public void setConsoleDisplay(IConsoleDisplay d) {
		host = d;
	}
	/**
	 * Can return <code>null</code>
	 * @return
	 */
	public IConsoleDisplay getConsoleDisplay() {
		return host;
	}

	/**
	 * Return a new {@link ISolrQueryIterator}
	 * @return
	 */
	public ISolrQueryIterator getQueryIterator() {
		return new SolrQueryIterator(this);
	}
	
	public void shutDown() {
		super.shutDown();
	}
}
