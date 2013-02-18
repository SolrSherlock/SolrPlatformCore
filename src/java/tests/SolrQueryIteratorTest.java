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
package tests;

import java.util.*;
import org.topicquests.common.api.IResult;
import org.topicquests.solr.SolrEnvironment;
import org.topicquests.solr.api.ISolrQueryIterator;

/**
 * @author park
 *
 */
public class SolrQueryIteratorTest {
	private SolrEnvironment environment;
	private ISolrQueryIterator itr;
	private final String query = "locator:*";
	private final int count = 10;
	/**
	 * 
	 */
	public SolrQueryIteratorTest() {
		environment = new SolrEnvironment();
		itr = environment.getQueryIterator();
		runTest();
	}
	
	void runTest() {
		Set<String>credentials = new HashSet<String>();
		credentials.add("admin");
		
		IResult result = itr.start(query, count, credentials);
		System.out.println("GOT "+result.hasError()+" "+result.getResultObject());
		result = itr.next();
		System.out.println("GOT-1 "+result.hasError()+" "+result.getResultObject());
		result = itr.next();
		System.out.println("GOT-2 "+result.hasError()+" "+result.getResultObject());
		result = itr.previous();
		System.out.println("GOT-3 "+result.hasError()+" "+result.getResultObject());
	}

}
/**
SolrDataProvider.runQuery locator:*
Solr3Client.runQuery- locator:* 0 10
Solr3Client.runQuery-1 q=locator%3A*&start=0&rows=10
ZZZZ {numFound=134,start=0,docs=[SolrDocument{locator=MergeAssertionType, smallIcon=cogwheel.png, subOf=[RelationType], details=[The TopicQuests typology merge assertion node type.], isPrivate=false, creatorId=SystemUser, label=[Merge Assertion Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:25 PST 2013, createdDate=Sun Feb 17 21:36:25 PST 2013}, SolrDocument{locator=VirtualNodeType, smallIcon=cogwheel.png, subOf=[ClassType], details=[The TopicQuests typology virtual node type.], isPrivate=false, creatorId=SystemUser, label=[Virtual Node Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:23 PST 2013, createdDate=Sun Feb 17 21:36:23 PST 2013}, SolrDocument{locator=RelationType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology relation node type.], isPrivate=false, creatorId=SystemUser, label=[Relation Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:24 PST 2013, createdDate=Sun Feb 17 21:36:24 PST 2013}, SolrDocument{locator=PropertyType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology property node type.], isPrivate=false, creatorId=SystemUser, label=[Property Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:26 PST 2013, createdDate=Sun Feb 17 21:36:26 PST 2013}, SolrDocument{locator=OntologyType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology ontology node type.], isPrivate=false, creatorId=SystemUser, label=[Ontology Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:25 PST 2013, createdDate=Sun Feb 17 21:36:25 PST 2013}, SolrDocument{locator=ClassType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology class node type.], isPrivate=false, creatorId=SystemUser, label=[Class Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:22 PST 2013, createdDate=Sun Feb 17 21:36:22 PST 2013}, SolrDocument{locator=RuleType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology rule node type.], isPrivate=false, creatorId=SystemUser, label=[Rule Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:26 PST 2013, createdDate=Sun Feb 17 21:36:26 PST 2013}, SolrDocument{locator=GraphType, smallIcon=cogwheel.png, subOf=[ClassType], details=[The TopicQuests typology graph type.], isPrivate=false, creatorId=SystemUser, label=[Graph Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:23 PST 2013, createdDate=Sun Feb 17 21:36:23 PST 2013}, SolrDocument{locator=NodeType, smallIcon=cogwheel.png, subOf=[ClassType], details=[The TopicQuests typology node type.], isPrivate=false, creatorId=SystemUser, label=[Node Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:22 PST 2013, createdDate=Sun Feb 17 21:36:22 PST 2013}, SolrDocument{locator=TypeType, smallIcon=cogwheel.png, details=[The TopicQuests typology root node type.], isPrivate=false, creatorId=SystemUser, label=[Type Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:21 PST 2013, createdDate=Sun Feb 17 21:36:21 PST 2013}]}
GOT false [org.topicquests.model.Node@979dce4, org.topicquests.model.Node@1f950198, org.topicquests.model.Node@6f9bb25a, org.topicquests.model.Node@56da6bf4, org.topicquests.model.Node@1de58cb8, org.topicquests.model.Node@4979935d, org.topicquests.model.Node@4cb9e45a, org.topicquests.model.Node@403ef810, org.topicquests.model.Node@66100363, org.topicquests.model.Node@254e8cee]
SolrDataProvider.runQuery locator:*
Solr3Client.runQuery- locator:* 10 10
Solr3Client.runQuery-1 q=locator%3A*&start=10&rows=10
ZZZZ {numFound=134,start=10,docs=[SolrDocument{locator=HarvestAgentType, smallIcon=cogwheel.png, subOf=[AgentType], details=[The TopicQuests typology harvest agent node type.], isPrivate=false, creatorId=SystemUser, label=[Harvest Agent User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:31 PST 2013, createdDate=Sun Feb 17 21:36:31 PST 2013}, SolrDocument{locator=MergeAgentType, smallIcon=cogwheel.png, subOf=[AgentType], details=[The TopicQuests typology merge agent node type.], isPrivate=false, creatorId=SystemUser, label=[Merge Agent User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:30 PST 2013, createdDate=Sun Feb 17 21:36:30 PST 2013}, SolrDocument{locator=UnknownUserType, smallIcon=cogwheel.png, subOf=[UserType], details=[The TopicQuests typology unknown user node type.], isPrivate=false, creatorId=SystemUser, label=[Unknown User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:29 PST 2013, createdDate=Sun Feb 17 21:36:29 PST 2013}, SolrDocument{locator=ForeignUserType, smallIcon=cogwheel.png, subOf=[UserType], details=[The TopicQuests typology foreign user node type.], isPrivate=false, creatorId=SystemUser, label=[Foreign User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:29 PST 2013, createdDate=Sun Feb 17 21:36:29 PST 2013}, SolrDocument{locator=MergeRuleType, smallIcon=cogwheel.png, subOf=[RuleType], details=[The TopicQuests typology merge rule node type.], isPrivate=false, creatorId=SystemUser, label=[Merge Rule Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:27 PST 2013, createdDate=Sun Feb 17 21:36:27 PST 2013}, SolrDocument{locator=AgentType, smallIcon=cogwheel.png, subOf=[UserType], details=[The TopicQuests typology agent user node type.], isPrivate=false, creatorId=SystemUser, label=[Agent User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:30 PST 2013, createdDate=Sun Feb 17 21:36:30 PST 2013}, SolrDocument{locator=RoleType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology role node type.], isPrivate=false, creatorId=SystemUser, label=[Role Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:27 PST 2013, createdDate=Sun Feb 17 21:36:27 PST 2013}, SolrDocument{locator=UserType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology user node type.], isPrivate=false, creatorId=SystemUser, label=[User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:28 PST 2013, createdDate=Sun Feb 17 21:36:28 PST 2013}, SolrDocument{locator=SystemUser, smallIcon=cogwheel.png, details=[The TopicQuests System User.], isPrivate=false, creatorId=SystemUser, instanceOf=UserType, label=[System user], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:28 PST 2013, createdDate=Sun Feb 17 21:36:28 PST 2013}, SolrDocument{locator=lastEditDate, smallIcon=snowflake.png, subOf=[PropertyType], details=[The TopicQuests typology last edit date property type.], isPrivate=false, creatorId=SystemUser, label=[Last Edit Date Property Type], largeIcon=snowflake.png, lastEditDate=Sun Feb 17 21:36:36 PST 2013, createdDate=Sun Feb 17 21:36:36 PST 2013}]}
GOT-1 false [org.topicquests.model.Node@1ebcda2d, org.topicquests.model.Node@97d01f, org.topicquests.model.Node@6ee0a386, org.topicquests.model.Node@5e0feb48, org.topicquests.model.Node@671ff436, org.topicquests.model.Node@62da3a1e, org.topicquests.model.Node@651dba45, org.topicquests.model.Node@2b03be0, org.topicquests.model.Node@2af081, org.topicquests.model.Node@313a53d]
SolrDataProvider.runQuery locator:*
Solr3Client.runQuery- locator:* 20 10
Solr3Client.runQuery-1 q=locator%3A*&start=20&rows=10
ZZZZ {numFound=134,start=20,docs=[SolrDocument{locator=MergeRuleScopeType, smallIcon=cogwheel.png, subOf=[ScopeType], details=[The TopicQuests typology merge rule scope node type.], isPrivate=false, creatorId=SystemUser, label=[Merge Rule Scope Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:34 PST 2013, createdDate=Sun Feb 17 21:36:34 PST 2013}, SolrDocument{locator=createdDate, smallIcon=snowflake.png, subOf=[PropertyType], details=[The TopicQuests typology created date property type.], isPrivate=false, creatorId=SystemUser, label=[Created Date Property Type], largeIcon=snowflake.png, lastEditDate=Sun Feb 17 21:36:35 PST 2013, createdDate=Sun Feb 17 21:36:35 PST 2013}, SolrDocument{locator=creatorId, smallIcon=snowflake.png, subOf=[PropertyType], details=[The TopicQuests typology creator id property type.], isPrivate=false, creatorId=SystemUser, label=[Creator ID Property Type], largeIcon=snowflake.png, lastEditDate=Sun Feb 17 21:36:36 PST 2013, createdDate=Sun Feb 17 21:36:36 PST 2013}, SolrDocument{locator=locator, smallIcon=snowflake.png, subOf=[PropertyType], details=[The TopicQuests typology locator property type.], isPrivate=false, creatorId=SystemUser, label=[Node Locator Property Type], largeIcon=snowflake.png, lastEditDate=Sun Feb 17 21:36:35 PST 2013, createdDate=Sun Feb 17 21:36:35 PST 2013}, SolrDocument{locator=WebResourceType, smallIcon=cogwheel.png, subOf=[ResourceType], details=[The TopicQuests typology web resource node type.], isPrivate=false, creatorId=SystemUser, label=[Web Resource Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:33 PST 2013, createdDate=Sun Feb 17 21:36:33 PST 2013}, SolrDocument{locator=ResourceType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology resource node type.], isPrivate=false, creatorId=SystemUser, label=[Resource Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:32 PST 2013, createdDate=Sun Feb 17 21:36:32 PST 2013}, SolrDocument{locator=LegendType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology legend node type.], isPrivate=false, creatorId=SystemUser, label=[Legend Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:33 PST 2013, createdDate=Sun Feb 17 21:36:33 PST 2013}, SolrDocument{locator=ScopeType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology scope node type.], isPrivate=false, creatorId=SystemUser, label=[Scope Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:34 PST 2013, createdDate=Sun Feb 17 21:36:34 PST 2013}, SolrDocument{locator=largeIcon, smallIcon=snowflake.png, subOf=[PropertyType], details=[The TopicQuests typology large image path property type.], isPrivate=false, creatorId=SystemUser, label=[Large Image Path Property Type], largeIcon=snowflake.png, lastEditDate=Sun Feb 17 21:36:41 PST 2013, createdDate=Sun Feb 17 21:36:41 PST 2013}, SolrDocument{locator=smallIcon, smallIcon=snowflake.png, subOf=[PropertyType], details=[The TopicQuests typology small image path property type.], isPrivate=false, creatorId=SystemUser, label=[Small Image Path Property Type], largeIcon=snowflake.png, lastEditDate=Sun Feb 17 21:36:41 PST 2013, createdDate=Sun Feb 17 21:36:41 PST 2013}]}
GOT-2 false [org.topicquests.model.Node@7ec5495e, org.topicquests.model.Node@4a53fb57, org.topicquests.model.Node@4f9a32e0, org.topicquests.model.Node@148238f4, org.topicquests.model.Node@716925b0, org.topicquests.model.Node@2e297ffb, org.topicquests.model.Node@26914f6a, org.topicquests.model.Node@df4cbee, org.topicquests.model.Node@42787d6a, org.topicquests.model.Node@7471dc3d]
SolrDataProvider.runQuery locator:*
Solr3Client.runQuery- locator:* 10 10
Solr3Client.runQuery-1 q=locator%3A*&start=10&rows=10
ZZZZ {numFound=134,start=10,docs=[SolrDocument{locator=HarvestAgentType, smallIcon=cogwheel.png, subOf=[AgentType], details=[The TopicQuests typology harvest agent node type.], isPrivate=false, creatorId=SystemUser, label=[Harvest Agent User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:31 PST 2013, createdDate=Sun Feb 17 21:36:31 PST 2013}, SolrDocument{locator=MergeAgentType, smallIcon=cogwheel.png, subOf=[AgentType], details=[The TopicQuests typology merge agent node type.], isPrivate=false, creatorId=SystemUser, label=[Merge Agent User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:30 PST 2013, createdDate=Sun Feb 17 21:36:30 PST 2013}, SolrDocument{locator=UnknownUserType, smallIcon=cogwheel.png, subOf=[UserType], details=[The TopicQuests typology unknown user node type.], isPrivate=false, creatorId=SystemUser, label=[Unknown User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:29 PST 2013, createdDate=Sun Feb 17 21:36:29 PST 2013}, SolrDocument{locator=ForeignUserType, smallIcon=cogwheel.png, subOf=[UserType], details=[The TopicQuests typology foreign user node type.], isPrivate=false, creatorId=SystemUser, label=[Foreign User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:29 PST 2013, createdDate=Sun Feb 17 21:36:29 PST 2013}, SolrDocument{locator=MergeRuleType, smallIcon=cogwheel.png, subOf=[RuleType], details=[The TopicQuests typology merge rule node type.], isPrivate=false, creatorId=SystemUser, label=[Merge Rule Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:27 PST 2013, createdDate=Sun Feb 17 21:36:27 PST 2013}, SolrDocument{locator=AgentType, smallIcon=cogwheel.png, subOf=[UserType], details=[The TopicQuests typology agent user node type.], isPrivate=false, creatorId=SystemUser, label=[Agent User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:30 PST 2013, createdDate=Sun Feb 17 21:36:30 PST 2013}, SolrDocument{locator=RoleType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology role node type.], isPrivate=false, creatorId=SystemUser, label=[Role Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:27 PST 2013, createdDate=Sun Feb 17 21:36:27 PST 2013}, SolrDocument{locator=UserType, smallIcon=cogwheel.png, subOf=[TypeType], details=[The TopicQuests typology user node type.], isPrivate=false, creatorId=SystemUser, label=[User Type], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:28 PST 2013, createdDate=Sun Feb 17 21:36:28 PST 2013}, SolrDocument{locator=SystemUser, smallIcon=cogwheel.png, details=[The TopicQuests System User.], isPrivate=false, creatorId=SystemUser, instanceOf=UserType, label=[System user], largeIcon=cogwheel.png, lastEditDate=Sun Feb 17 21:36:28 PST 2013, createdDate=Sun Feb 17 21:36:28 PST 2013}, SolrDocument{locator=lastEditDate, smallIcon=snowflake.png, subOf=[PropertyType], details=[The TopicQuests typology last edit date property type.], isPrivate=false, creatorId=SystemUser, label=[Last Edit Date Property Type], largeIcon=snowflake.png, lastEditDate=Sun Feb 17 21:36:36 PST 2013, createdDate=Sun Feb 17 21:36:36 PST 2013}]}
GOT-3 false [org.topicquests.model.Node@5f326484, org.topicquests.model.Node@656546ef, org.topicquests.model.Node@5c1428ea, org.topicquests.model.Node@2f8a49e0, org.topicquests.model.Node@1ff82982, org.topicquests.model.Node@5d6d2633, org.topicquests.model.Node@28e70e30, org.topicquests.model.Node@5954864a, org.topicquests.model.Node@3c3c9217, org.topicquests.model.Node@2c9b42e6]
*/
