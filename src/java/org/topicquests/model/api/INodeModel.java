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
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.topicquests.common.api.IResult;

/**
 * @author park
 *
 */
public interface INodeModel {

	  IResult newNode(String locator,String label, String description, String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate);
	  IResult newNode(String label, String description, String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate);
	  
	  IResult newSubclassNode(String locator,String superclassLocator,String label, String description, String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate);
	  IResult newSubclassNode(String superclassLocator,String label, String description, String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate);
//	  IResult newNamedSubclassNode(String superclassLocator, String name, String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate);
//	  IResult newNamedSubclassNode(String locator, String superclassLocator, String name, String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate);
	  
	  IResult newInstanceNode(String locator,String typeLocator,String label, String description, String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate);
	  IResult newInstanceNode(String typeLocator,String label, String description, String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate);

//	  IResult newNamedInstanceNode(String typeLocator, String name, String lang, String userId, boolean isPrivate);
//	  IResult newNamedInstanceNode(String locator, String typeLocator, String name, String lang, String userId, boolean isPrivate);

	  
	  /**
	   * <p>Used for
	   * <li>Editing an existing node to change a label or details or both</li>
	   * <li>Adding or editing some language translation</li></p>
	   * @param nodeLocator
	   * @param updatedLabel  <code>null</code> if no change
	   * @param updatedDetails <code>null</code> if no change
	   * @param language
	   * @param userId
	   * @param isLanguageAddition <code>true</code> if is new translation
	   * @return
	   */
	  IResult updateNode(String nodeLocator, String updatedLabel, String updatedDetails, String language, String userId, boolean isLanguageAddition);
	  
	  IResult updateNode(String nodeLocator, String propertyKey, List<String> propertyValues);
	  IResult updateNode(String nodeLocator, String propertyKey, String propertyValue);
	  /**
	   * <p>Form an {@link ITuple} between <code>sourceNodeLocator</code> and <code>targetNodeLocator</code></p>
	   * <p>The meaning of <code>isTransclude</code> is that <code>targetNodeLocator</code> is a
	   * <em>transcluded</code> {@link INode} when <code>isTransclude</code> is <code>true</code></p>
	   * <p>Note: internally, this must deal with restricted vs unrestricted nodes</p>
	   * @param sourceNodeLocator
	   * @param targetNodeLocator
	   * @param relationTypeLocator
	   * @param userId
	   * @param smallImagePath TODO
	   * @param largeImagePath TODO
	   * @param isTransclude
	   * @param isPrivate TODO
	   * @return
	   */
	  IResult relateNodes(String sourceNodeLocator, String targetNodeLocator, String relationTypeLocator, String userId, String smallImagePath, String largeImagePath, boolean isTransclude, boolean isPrivate);
	  
	  /**
	   * Assert a merge, which fires up a VirtualProxy, creates a MergeAssertion node (not a triple)
	   * and adds the list of rule locators to the merge assertion proxy
	   * @param sourceNodeLocator
	 * @param mergedNodeLocator
	 * @param mergeRuleLocators
	 * @param mergeConfidence TODO
	 * @param userLocator
	   * @return
	   */
	  IResult assertMerge(String sourceNodeLocator, String mergedNodeLocator, List<String>mergeRuleLocators, double mergeConfidence, String userLocator);
	  
//	  IResult getNodeByLocator(String locator);
	  
//	  IResult getNodeByPSI(String psi);

	  
	  IResult removeNode(String locator);
	  
	  Set<String> getDefaultCredentials(String userId);
	  
	  /**
	   * Utility method to create Solr date strings
	   * @param d
	   * @return
	   */
	  String dateToSolrDate(Date d);

	  IResult newTuple(String relationType, String subjectId, String objectType, String objectVal, String relationId, String userId, boolean isTransclude);
}
