SolrPlatformCore 
================

A collection of classes and methods common to most TopicQuests Solr projects

Status: *alpha*<br/>
Latest edit: 20131125
## Background ##
There are a number of functions which are common to TopicQuests topic map and Solr projects. Namely, the APIs which allow to create and manipulate topics in a topic map, using the Solr platform as the database.

## Update History ##
20131125
	Refactored everything not related directly to Solr out to the TopicQuestsCoreAPI project.
20130725
	Corrected error in creation of JSONObject in Node.java;
	Added JSONUtil to convert Map to JSONObject<br/>
20130628
	Modify SolrEnvironment to load the ISolrClient according to a new config file property: SolrClient; this allows for swapping in either a single Solr installation client, or a SolrCloud client.
	Added a new field for listing "transcludes" of a node<br/>
20130619
	Udate to Solr 4.3.1 required code change for depricated java class<br/>
20130617
	Code cleanup<br/>
20130414
	First major update to the platform, which includes important updates to the merge code, simplification of JSON, including using JSONObject as the primary Node data Map<br/>
## ToDo ##
Mavenize the project<br/>
Create a full unit test suite

## License ##
Apache 2
