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
package org.topicquests.model.api;
import java.util.Set;

import org.topicquests.common.api.IResult;

/**
 * @author park
 * <p>Common Queries to be implemented for different databases</p>
 */
public interface ITupleQuery {
	
	/**
	 * 
	 * @param subjectLocator
	 * @param credentials
	 * @return
	 */
	IResult listTuplesBySubject(String subjectLocator, Set<String>credentials);

	  /**
	   * <p>Return a list of <code>ITuple</code> objects inside an {@link IResult} object</p>
	   * <p>This is the core way to fetch an list of <code>ITuple</code> object when
	   * the desired result is to learn all the <code>subjectId</code> values that contain that
	   * key/value pair.</p>
	   * @param predType
	 * @param obj
	 * @param start TODO
	 * @param count TODO
	 * @param credentials TODO
	   * @return -- an IResult object that contains List[ITuple] or an error message
	   */
	  IResult listTuplesByPredTypeAndObject(String predType, String obj, int start, int count, Set<String> credentials);
	
	/**
	 * <p>Return a possibly empty list of {@link INode} objects which correspond
	 * with <code>objectLocator</code> by relation <code>relationLocator</code></p>
	 * @param objectLocator
	 * @param relationLocator
	 * @param credentials
	 * @return <code>List<INode></code>
	 */
	IResult listSubjectNodesByObjectAndRelation(String objectLocator, String relationLocator, Set<String>credentials);
	
	/**
	 * <p>Return a possibly empty list of {@link INode} objects which correspond with
	 * <code>subjectLocator</code> by relation <code>relationLocator</code></p>
	 * @param subjectLocator
	 * @param relationLocator
	 * @param credentials
	 * @return <code>List<INode></code>
	 */
	IResult listObjectNodesBySubjectAndRelation(String subjectLocator, String relationLocator, Set<String>credentials);
	
	
	/**
	 * <p>Return a possibly empty list of {@link INode} objects which correspond with
	 * <code>subjectLocator</code> by relation <code>relationLocator</code>, and includes the scope <code>scopeLocator</code></p>
	 * @param subjectLocator
	 * @param relationLocator
	 * @param credentials
	 * @return <code>List<INode></code>
	 */
	IResult listObjectNodesBySubjectAndRelationAndScope(String subjectLocator, String relationLocator, String scopeLocator, Set<String>credentials);
	
	/**
	 * <p>Return a possibly empty list of {@link INode} objects which correspond
	 * with <code>objectLocator</code> by relation <code>relationLocator</code></p>
	 * @param objectLocator
	 * @param relationLocator
	 * @param credentials
	 * @return <code>List<INode></code>
	 */
	IResult listSubjectNodesByObjectAndRelationAndScope(String objectLocator, String relationLocator, String scopeLocator, Set<String>credentials);
	
	IResult listSubjectNodesByRelationAndObjectRole(String relationLocator, String objectRoleLocator, Set<String>credentials);
	
	IResult listSubjectNodesByRelationAndSubjectRole(String relationLocator, String subjectRoleLocator, Set<String>credentials);
	
	IResult listObjectNodesByRelationAndSubjectRole(String relationLocator, String subjectRoleLocator, Set<String>credentials);
	
	IResult listObjectNodesByRelationAndObjectRole(String relationLocator, String objectRoleLocator, Set<String>credentials);
	//TODO fetching tuples which weight criteria
}
