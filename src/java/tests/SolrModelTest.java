/*
 * Copyright 2013, TopicQuests
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
package tests;
import java.util.*;

import org.topicquests.model.api.INode;
import org.topicquests.model.api.INodeModel;
import org.topicquests.solr.Node;
import org.topicquests.solr.SolrEnvironment;
import org.topicquests.solr.api.ISolrDataProvider;
import org.topicquests.solr.api.ISolrQueryModel;
import org.topicquests.solr.api.ISolrQueryIterator;
import org.topicquests.common.api.IRelationsLegend;
import org.topicquests.common.api.IResult;

/**
 * @author park
 *
 */
public class SolrModelTest {
	private SolrEnvironment environment;
	private ISolrDataProvider database;
	private INodeModel nodeModel;
	private ISolrQueryModel solrModel;
	private String aLabel = "My first label";
	private String bLabel = "My second label";
	private String aDetails = "My first details";
	private String bDetails = "My second details";
	private String relationType = IRelationsLegend.CAUSES_RELATION_TYPE;

	/**
	 * 
	 */
	public SolrModelTest() {
		environment = new SolrEnvironment();
		database = (ISolrDataProvider)environment.getDataProvider();
		nodeModel = database.getNodeModel();
		solrModel = environment.getSolrModel();
		runTest();
	}

	void runTest() {
		String loc1 = "MyFirstNode"+System.currentTimeMillis();
		String loc2 = "MySecondNode"+System.currentTimeMillis();
		Date d = new Date();

		String userId = "admin";
		String lang = "en";
		Set<String>credentials = new HashSet<String>();
		credentials.add(userId);
		//build some nodes
		IResult r1 = null;
				//nodeModel.newNode(aLabel, bDetails, lang, userId, null, null, false);
		INode n1 = makeNode(loc1,"Class1Type",d,aLabel,bDetails);
				//(INode)r1.getResultObject();
		database.putNode(n1);
		System.out.println(n1.toXML());
		IResult r2 = null; //nodeModel.newNode(bLabel, aDetails, lang, userId, null, null, false);
		INode n2 = makeNode(loc2,"Class1Type",d,bLabel,aDetails);
				//(INode)r2.getResultObject();
		database.putNode(n2);
		System.out.println(n2.toXML());
		r1 = database.getNode(n1.getLocator(), credentials);
		n1 = (INode)r1.getResultObject();
		System.out.println(n1.toXML());
		r2 = database.getNode(n2.getLocator(), credentials);
		n2 = (INode)r2.getResultObject();
		System.out.println(n2.toXML());
		
		//Relate them
		IResult r3 = nodeModel.relateNodes(n1.getLocator(), n1.getLocator(), relationType, userId, null, null, false, false);
		System.out.println("#1: "+r1.hasError()+" "+r2.hasError()+" "+r3.hasError());
	}
	
	INode makeNode(String loc, String sup, Date d, String label, String details ) {
		INode result = new Node();
		result.setLocator(loc);
		result.setCreatorId("admin");
		result.setDate(d);
		result.setLastEditDate(d);
		result.setIsPrivate(false);
		result.setNodeType(sup);
		result.addDetails(details, "en", "admin", false);
		result.addLabel(label, "en", "admin", false);
		return result;
	}

}
