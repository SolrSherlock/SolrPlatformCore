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
import java.io.Writer;

import org.topicquests.common.api.IMergeRuleMethod;
import org.topicquests.common.api.IResult;

/**
 * @author park
 * Serves as a core API,to be extended for different databases
 */
public interface IDataProvider {

	INodeModel getNodeModel();
	
	ITupleQuery getTupleQuery();
	  /**
	   * Returns a UUID String
	   * @return
	   */
	  String getUUID();
	  String getUUID_Pre(String prefix);
	  String getUUID_Post(String suffix);
	  
	  /**
	   * Export the entire database to <code>out</code>
	   * @param out
	   * @param credentials
	   * @return
	   * @deprecated   WILL NOT SCALE -- not implemented
	   */
	  IResult exportXmlFile(Writer out, Set<String> credentials);
	  
	  /**
	   * <p>Export a tree root and it's entire subtree.</p>
	   * <p>A subtree is defined as all related nodes; this gets rather complex
	   * because it calls for uprooting all tuples as well as nodes which are related
	   * to the given <code>treeRootLocator</code>.</p>
	   * <p>There is a risk that this method will not scale due to really rich graphs
	   * growing up around some root.</p>
	   * <p>There is a risk that this method, at larger graphs, will need to be threaded
	   * in order to prevent blocking normal operation of the server.</p>
	   * <p>When we enter with <code>treeRootLocator</code>, we do not explore and export
	   * any parent nodes. If this node is a subclass, or instance of some type or parent,
	   * we do not rise above and export those.</p>
	   * @param treeRootLocator
	   * @param out
	   * @param credentials
	   * @return
	   */
	  IResult exportXmlTreeFile(String treeRootLocator, Writer out, Set<String> credentials);
	  
	  /**
	   * <p>Create an {@link INode} which represents a merge rule based on the
	   * given {@link IMergeRuleMethod}</p>
	   * <p>This rule's locator, referenced in the method, will be added as a
	   * scope to any merge tuple when merge events occur</p>
	   * @param theMethod
	   * @return
	   */
	  IResult createMergeRule(IMergeRuleMethod theMethod);
	  
	  /**
	   * <p>Fetch a node. <code>credentials</code> are required in case
	   * the node is private and a credential must be tested</p>
	   * <p>Error message will be returned if the node is private and insufficient
	   * credentials are presented</p>
	   * <p>Returns <code>null</code> as the result object if there is no node or
	   * if credentials are insufficient</p>
	   * @param locator
	   * @param credentials
	   * @return
	   */
	  IResult getNode(String locator, Set<String> credentials);
	  
	  /**
	   * Assemble a node view based on the node and its various related nodes
	   * @param locator
	   * @param credentials
	   * @return
	   */
	  IResult getNodeView(String locator, Set<String>credentials);
	  
	  IResult removeNode(String locator);

	  IResult putNode(INode node);
	  
	  
		/**
		 * Returns a Boolean <code>true if there exists an {@link ITuple} of <code>relationLocator</code> and
		 * either a <em>subject</em> or </em>object</em> identified by <code>theLocator</code>
		 * @param theLocator
		 * @param relationLocator
		 * @return
		 */
		IResult existsTupleBySubjectOrObjectAndRelation(String theLocator, String relationLocator);

		/**
	   * <p>Will list only those nodes for which sufficient credentials are presented,
	   * if a given node is private</p>
	   * @param nameString
	   * @param credentials
	   * @return
	   */
//	  IResult listNodesByNameString(String nameString, Set<String> credentials);
	  
//	  IResult listNodesByNameStringLike(String nameFragment, int start, int count, Set<String> credentials);

	  /**
	   * Tests whether <code>nodeLocator</code> is of type or a subclass of <code>targetTypeLocator</code>
	   * @param nodeLocator
	 * @param targetTypeLocator
	 * @param credentials TODO
	   * @return
	   */
	  IResult nodeIsA(String nodeLocator, String targetTypeLocator, Set<String> credentials);
	  
	  
	  IResult listNodesByPSI(String psi, int start, int count, Set<String> credentials);
	  
	  IResult listNodesByLabelAndType(String label, String typeLocator,int start, int count, Set<String> credentials);
	  
	  IResult listNodesByLabel(String label,int start, int count, Set<String> credentials);
	  
	  /**
	   * <p>Return nodes with labels that are <em>like</em> <code>labelFragment</code></p>
	   * <p>A <em>wildcard</em> is added before and after <code>labelFragment</code></p>
	   * <p>Example: given the string "My favorite topic"; would be matched with My, favorite, or topic</p>
	   * <p>Results are case sensitive</p>
	   * @param labelFragment
	 * @param start
	 * @param count
	 * @param credentials TODO
	   * @return
	   */
	  IResult listNodesByLabelLike(String labelFragment, int start, int count, Set<String> credentials);
	  
	  IResult listNodesByDetailsLike(String detailsFragment, int start, int count, Set<String> credentials);
	  
	  IResult listNodesByQuery(String queryString,int start, int count, Set<String> credentials);
	  
	  IResult listNodesByCreatorId(String creatorId, int start, int count, Set<String> credentials);
	  
	  IResult listNodesByType(String typeLocator,int start, int count, Set<String> credentials);
	  
	  /**
	   * Really, this is the same as <code>listNodesByType</code>
	   * @param typeLocator
	   * @param start
	   * @param count
	   * @param credentials TODO
	   * @return a list of [@link INode} objects or <code>null</code>
	   */
	  IResult listInstanceNodes(String typeLocator, int start, int count, Set<String> credentials);
	  
	  IResult listSubclassNodes(String superclassLocator, int start, int count, Set<String> credentials);

	  ////////////////////////////////////
	  //Tuple support
	  ///////////////////////////////////
	  /**
	   * Return a list of <code>ITuple</code> objects inside an {@link IResult} object
	   * @param predType
	   * @param subjectId
	   * @param start
	   * @param count
	   * @return -- an IResult object that contains a List[ITuple] or an error message
	   */
	  IResult listTuplesByPredTypeAndSubjectId(String predType, String subjectId, int start, int count);
	  

	  
	  
	  IResult putTuple(ITuple tuple);
	  
	  /**
	   * Return an <code>ITuple</code> inside an {@link IResult} object or <code>null</code> if not found
	   * @param tupleLocator
	 * @param credentials TODO
	   * @return -- an IResult object that contains either an ITuple or an error message
	   */
	  IResult getTuple(String tupleLocator, Set<String> credentials);
	  
	  /**
	   * <p>Return a list of <code>ITuple</code> objects inside an {@link IResult} object</p>
	   * <p>This is the core way to fetch an entire {@link INode} by its <code>subjectId</code>
	   * locator.</p>
	   * @param subjectLocator
	 * @param start TODO
	 * @param count TODO
	   * @return -- an IResult object that contains List[ITuple] or an error message
	   */ //moved to ITupleQuery
//	  IResult listTuplesBySubjectId(String subjectLocator, int start, int count);
	  
	  /**
	   * <p>Return a list of <code>ITuple</code> objects inside an {@link IResult} object</p>
	   * <p>This is the core way to fetch an list of <code>ITuple</code> object when
	   * the desired result is to learn all the <code>subjectId</code> values that contain that
	   * key/value pair.</p>
	   * @param predType
	 * @param obj
	 * @param start TODO
	 * @param count TODO
	   * @return -- an IResult object that contains List[ITuple] or an error message
	   */ //moved to ITupleQuery
//	  IResult listTuplesByPredTypeAndObject(String predType, String obj, int start, int count);
	  
	  /**
	   * @deprecated
	   * @param tuple
	   * @return
	   */
	  IResult updateTuple(ITuple tuple);
	  
	  /**
	   * @deprecated
	   * @param tuple
	   * @return
	   */
	  IResult removeTuple(ITuple tuple);
	  
	  IResult updateNode(INode node);

	  //////////////////////////////////////////////////
	  // General query support
	  //////////////////////////////////////////////////
	  /**
	   * 
	   * @param queryString
	 * @param start
	 * @param count
	 * @param credentials TODO
	   * @return
	   */
	  IResult runQuery(String queryString, int start, int count, Set<String> credentials);
}
