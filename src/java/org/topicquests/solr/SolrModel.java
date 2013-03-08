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
package org.topicquests.solr;

import java.util.Set;

import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.solr.api.ISolrModel;
import org.topicquests.solr.api.ISolrQueryIterator;

/**
 * @author park
 *
 */
public class SolrModel implements ISolrModel {
	private SolrEnvironment environment;
	private final String labelQuery = ITopicQuestsOntology.LABEL_PROPERTY;
	private final String detailsQuery = ITopicQuestsOntology.DETAILS_PROPERTY;
	private final String instanceQuery = ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE+":";
	private final String subClassQuery = ITopicQuestsOntology.SUBCLASS_OF_PROPERTY_TYPE+":";

	/**
	 * 
	 */
	public SolrModel(SolrEnvironment e) {
		environment = e;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.solr.api.ISolrModel#listNodesByLabel(java.lang.String, java.lang.String, int, java.util.Set)
	 */
	@Override
	public ISolrQueryIterator listNodesByLabel(String label, String language,
			int count, Set<String> credentials) {
		ISolrQueryIterator itr = new SolrQueryIterator(environment);
		
		itr.start(makeField(labelQuery,language)+":"+QueryUtil.escapeQueryCulprits(label), count, credentials);
		return itr;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.solr.api.ISolrModel#listNodesByDetails(java.lang.String, java.lang.String, int, java.util.Set)
	 */
	@Override
	public ISolrQueryIterator listNodesByDetails(String details,
			String language, int count, Set<String> credentials) {
		ISolrQueryIterator itr = new SolrQueryIterator(environment);
		itr.start(makeField(detailsQuery,language)+":"+QueryUtil.escapeQueryCulprits(details), count, credentials);
		return itr;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.solr.api.ISolrModel#listTuplesByRelation(java.lang.String, int, java.util.Set)
	 */
	@Override
	public ISolrQueryIterator listTuplesByRelation(String relationType,
			int count, Set<String> credentials) {
		ISolrQueryIterator itr = new SolrQueryIterator(environment);
		itr.start(instanceQuery+relationType, count, credentials);
		return itr;
	}
	
	/**
	 * Calculate the appropriate Solr field
	 * @param fieldBase
	 * @param language
	 * @return
	 */
	String makeField(String fieldBase, String language) {
		String result = fieldBase;
		if (!language.equals("en"))
			result += language;
		return result;
	}

	@Override
	public ISolrQueryIterator listNodeInstances(String nodeTypeLocator,
			int count, Set<String> credentials) {
		return listTuplesByRelation(nodeTypeLocator, count, credentials);
	}

	@Override
	public ISolrQueryIterator listNodeSubclasses(String superClassLocator, int count, Set<String> credentials) {
		ISolrQueryIterator itr = new SolrQueryIterator(environment);
		itr.start(subClassQuery+superClassLocator, count, credentials);
		return itr;
	}

}
