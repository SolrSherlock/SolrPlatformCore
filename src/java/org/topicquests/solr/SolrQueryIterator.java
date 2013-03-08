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

import org.topicquests.common.api.IResult;
import org.topicquests.solr.api.ISolrDataProvider;
import org.topicquests.solr.api.ISolrQueryIterator;

/**
 * @author park
 *
 */
public class SolrQueryIterator implements ISolrQueryIterator {
	private SolrEnvironment environment;
	private ISolrDataProvider solr;
	private String _query;
	private int _count;
	private int _cursor;
	private Set<String>_credentials;
	/**
	 * 
	 * @param e
	 */
	public SolrQueryIterator(SolrEnvironment e) {
		environment = e;
		solr = environment.getDataProvider();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.solr.api.ISolrQueryIterator#start(java.lang.String, int)
	 */
	@Override
	public void start(String queryString, int hitCount, Set<String> credentials) {
		_query = queryString;
		_count = hitCount;
		_cursor = 0;
		_credentials = credentials;		
	}

	/* (non-Javadoc)
	 * @see org.topicquests.solr.api.ISolrQueryIterator#next()
	 */
	@Override
	public IResult next() {
		IResult result = runQuery();
		_cursor += _count;
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.solr.api.ISolrQueryIterator#previous()
	 */
	@Override
	public IResult previous() {
		IResult result = runQuery();
		_cursor -= _count;
		if (_cursor < 0)
			_cursor = 0;
		return result;
	}
	
	private IResult runQuery() {
		return solr.runQuery(_query, _cursor, _count, _credentials);
	}

	@Override
	public void reset() {
		_cursor = 0;
	}

}
