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

import java.util.Set;

import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.ITupleQuery;
import org.topicquests.solr.api.ISolrClient;
import org.topicquests.solr.api.ISolrDataProvider;

/**
 * @author park
 *
 */
public class SolrTupleQuery implements ITupleQuery {
	private ISolrDataProvider database;
	private ISolrClient solr;
	/**
	 * 
	 */
	public SolrTupleQuery(ISolrDataProvider db) {
		database = db;
		solr = database.getSolrClient();
	}

	/* (non-Javadoc) tested
	 * @see org.topicquests.model.api.ITupleQuery#listSubjectNodesByObjectAndRelation(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public IResult listSubjectNodesByObjectAndRelation(String objectLocator,
			String relationLocator, Set<String> credentials) {
		String queryString = ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE+":"+relationLocator+ //the relation
				" AND "+ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY+":"+objectLocator; // an object
		//NOTE: we need to run this as an iterator
		IResult result = database.runQuery(queryString, 0, 50, credentials);
		return result;
	}

	/* (non-Javadoc) tested
	 * @see org.topicquests.model.api.ITupleQuery#listObjectNodesBySubjectAndRelation(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public IResult listObjectNodesBySubjectAndRelation(String subjectLocator,
			String relationLocator, Set<String> credentials) {
		String queryString = ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE+":"+relationLocator+ //the relation
				" AND "+ITopicQuestsOntology.TUPLE_OBJECT_TYPE_PROPERTY+":"+ITopicQuestsOntology.NODE_TYPE+ //require only nodes, not literals
				" AND "+ITopicQuestsOntology.TUPLE_SUBJECT_PROPERTY+":"+subjectLocator; // a subject
		//NOTE: we need to run this as an iterator
		IResult result = database.runQuery(queryString, 0, 50, credentials);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listObjectNodesBySubjectAndRelationAndScope(java.lang.String, java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public IResult listObjectNodesBySubjectAndRelationAndScope(
			String subjectLocator, String relationLocator, String scopeLocator,
			Set<String> credentials) {
		String queryString = ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE+":"+relationLocator+ //the relation
				" AND "+ITopicQuestsOntology.TUPLE_OBJECT_TYPE_PROPERTY+":"+ITopicQuestsOntology.NODE_TYPE+ //require only nodes, not literals
				" AND "+ITopicQuestsOntology.TUPLE_SUBJECT_PROPERTY+":"+subjectLocator + // a subject
				" AND "+ITopicQuestsOntology.SCOPE_LIST_PROPERTY_TYPE+":"+scopeLocator; // the scope
		//NOTE: we need to run this as an iterator
		IResult result = database.runQuery(queryString, 0, 50, credentials);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listSubjectNodesByObjectAndRelationAndScope(java.lang.String, java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public IResult listSubjectNodesByObjectAndRelationAndScope(
			String objectLocator, String relationLocator, String scopeLocator,
			Set<String> credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listSubjectNodesByRelationAndObjectRole(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public IResult listSubjectNodesByRelationAndObjectRole(
			String relationLocator, String objectRoleLocator,
			Set<String> credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listSubjectNodesByRelationAndSubjectRole(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public IResult listSubjectNodesByRelationAndSubjectRole(
			String relationLocator, String subjectRoleLocator,
			Set<String> credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listObjectNodesByRelationAndSubjectRole(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public IResult listObjectNodesByRelationAndSubjectRole(
			String relationLocator, String subjectRoleLocator,
			Set<String> credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listObjectNodesByRelationAndObjectRole(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public IResult listObjectNodesByRelationAndObjectRole(
			String relationLocator, String objectRoleLocator,
			Set<String> credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult listTuplesBySubject(String subjectLocator,
			Set<String> credentials) {
		String queryString = ITopicQuestsOntology.TUPLE_SUBJECT_PROPERTY+":"+subjectLocator;
		//NOTE: we need to run this as an iterator
		IResult result = database.runQuery(queryString, 0, 50, credentials);
		return result;
	}

	@Override
	public IResult listTuplesByPredTypeAndObject(String predType, String obj,
			int start, int count, Set<String> credentials) {
		String queryString = ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE+":"+predType+ //the relation
				" AND "+ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY+":"+obj; // an object
		//NOTE: we need to run this as an iterator
		IResult result = database.runQuery(queryString, 0, 50, credentials);
		return result;
	}


}
