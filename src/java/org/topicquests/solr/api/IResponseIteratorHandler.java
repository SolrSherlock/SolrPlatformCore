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

import org.topicquests.common.api.IResult;

/**
 * @author park
 * Just an idea, yet to be developed; the idea is to iterate
 * along long sequences in Solr where you can only fetch a limited
 * number at a time
 */
public interface IResponseIteratorHandler {

	/**
	 * <p>A SolrResponseIterator is a kind of query response iterator.
	 * It will continue to fetch results from a query that returns blocks
	 * of results</p>
	 * <p><code>response</code> will contain <code>null</code> when nothing
	 * is available (end of query), or it will contain an instance of {@link SolrDocumentList}
	 * when data is available</p>
	 * <p>The <code>IResponseIteratorHandler must deal with each batch of hits</p>
	 * @param response
	 */
	void handleSolrIteratorResponse(IResult response);
}
