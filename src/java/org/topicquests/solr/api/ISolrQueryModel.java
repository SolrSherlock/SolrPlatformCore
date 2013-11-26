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
package org.topicquests.solr.api;

import java.util.Set;

import org.topicquests.model.api.INode;
import org.topicquests.model.api.ITuple;

/**
 * @author park
 *
 */
public interface ISolrQueryModel {
	
	/**
	 * <p>Return an iterator which provides instances of {@link INode} which contain
	 * <code>label</code> for the given <code>language</code>.</p>
	 * @param label
	 * @param language
	 * @param count
	 * @param credentials
	 * @return
	 */
	ISolrQueryIterator listNodesByLabel(String label, String language, int count, Set<String>credentials);
	
	/**
	 * <p>Return an iterator which provides instances of {@link INode} which contain
	 * <code>details</code> for the given <code>language</code>.</p>
	 * @param details
	 * @param language
	 * @param count
	 * @param credentials
	 * @return
	 */
	ISolrQueryIterator listNodesByDetails(String details, String language, int count, Set<String> credentials);
	
	/**
	 * <p>Return an iterator which provides instances of {@link INode} which are
	 *  for the given <code>relationType>. Note, each resulting node must be
	 *  cast to {@link ITuple}</p>
	 * @param relationType
	 * @param count
	 * @param credentials
	 * @return
	 */
	ISolrQueryIterator listTuplesByRelation(String relationType, int count, Set<String>credentials);
	
	/**
	 * <p>Return an iterator which provides instances of {@link INode} which are instances of
	 * <code>nodeTypeLocator</code></p>
	 * @param nodeTypeLocator
	 * @param count
	 * @param credentials
	 * @return
	 */
	ISolrQueryIterator listNodeInstances(String nodeTypeLocator, int count, Set<String>credentials);
	
	/**
	 * <p>Return an iterator which provides instances of {@link INode} which are subclasses of
	 * <code>superClassLocator</code></p>
	 * @param superClassLocator
	 * @param count TODO
	 * @param credentials TODO
	 * @return
	 */
	ISolrQueryIterator listNodeSubclasses(String superClassLocator, int count, Set<String> credentials);
	
}
