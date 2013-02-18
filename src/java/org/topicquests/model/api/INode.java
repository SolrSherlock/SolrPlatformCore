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
import java.util.List;
import java.util.Map;
import java.util.Date;

import org.topicquests.common.api.IResult;
/**
 * @author park
 *
 */
public interface INode {

	/**
	 * Do a single update event when changes have been made as compared to
	 * individual updates. This method would update LastEditDate
	 * @return
	 */
	IResult doUpdate();
	
	/**
	 * Locator is the identifier for this tuple
	 * @param tupleLocator
	 */
	void setLocator(String tupleLocator);
	String getLocator();

	void setCreatorId(String id);
	String getCreatorId();
	
	/**
	 * Return <code>true</code> if this {@link INode} is a {@link ITuple}
	 * @return
	 */
	boolean isTuple();

	/**
	 * SOLR4 does a <code>_version</code> field
	 * @param version
	 */
	void setVersion(String version);
	String getVersion();
	
	/**
	 *  YYYY-MM-DDThh:mm:ssZ
	 * @param date
	 */
	//void setDate(String date);
	void setDate(Date date);
	Date getDate();
	
	//void setLastEditDate(String date);
	void setLastEditDate(Date date);
	Date getLastEditDate();
	
    /**
     * Solr expects a {@link Map}
     * @return
     */
	Map<String,Object> getProperties();

	/**
	 * Convert this node to a JSON String
	 * @return
	 */
	String toJSON();
	
	/**
	 * Return this node expressed in XML
	 * @return
	 */
	String toXML();
	
	
//	  String getLanguage();
	  
	  /**
	   * Labels can come in many languages, or also be synonyms, acronyms, etc
	   * @param label
	 * @param language
	 * @param userId can be <code>null</code> if not an update
	 * @param isLanguageAddition almost always false
	   */
	  void addLabel(String label, String language, String userId, boolean isLanguageAddition);
	  
	  /**
	   * Small label is limited to 70 characters, as found in http://debategraph.org/
	   * @param label
	   * @param language
	   * @param userId
	   * @param isLanguageAddition
	   */
	  void addSmallLabel(String label, String language, String userId, boolean isLanguageAddition);
	  
	  /**
	   * Will return the <em>first</em>label for the given <code>language</code>
	   * but if that <code>language</code> does not exist, returns the first label
	   * @param language
	   * @return can return "" empty string by default
	   */
	  String getLabel(String language);
	  
	  /**
	   * Return first label if many
	   * @param language
	   * @return can return "" if none found
	   */
	  String getSmallLabel(String language);
	  
	  /**
	   * List all labels
	   * @return
	   */
	  List<String> listLabels();
	  
	  /**
	   * List labels for given <code>language</code>
	   * @param language
	   * @return
	   */
	  List<String> listLabels(String language);
	  
	  /**
	   * List all small labels
	   * @return
	   */
	  List<String> listSmallLabels();
	  
	  /**
	   * List small labels for <code>language</code>
	   * @param language
	   * @return
	   */
	  List<String> listSmallLabels(String language);
	  
	  /**
	   * Details can come in many languages
	   * @param details
	 * @param language
	 * @param userId can be <code>null</code> if not an update
	 * @param isLanguageAddition almost always false
	   */
	  void addDetails(String details, String language, String userId, boolean isLanguageAddition);

	  /**
	   * Will return the <em>first</em>details for the given <code>language</code>
	   * but if that <code>language</code> does not exist, returns the first details
	   * @param language
	   * @return can return "" empty string by default
	   */
	  String getDetails(String language);

	  /**
	   * List all details; ignores language codes
	   * @return
	   */
	  List<String> listDetails();
	  
	  /**
	   * List details for <code>language</code>; returns first entry if many
	   * @param language
	   * @return
	   */
	  List<String> listDetails(String language);
	  	  
	  /**
	   * Images are really icons, not pictures
	   * @param img can be <code>null</code>
	   * <em>should not be an empty string</em>
	   */
	  void setSmallImage(String img);
	  /**
	   * 
	   * @param img can be <code>null</code>
	   * <em>should not be an empty string</em>
	   */
	  void setImage(String img);
	  
	  /**
	   * 
	   * @return can return <code>null</code>
	   */
	  String getSmallImage();
	  
	  /**
	   * 
	   * @return can return <code>null</code>
	   */
	  String getImage();

	  void setNodeType(String typeLocator);
	  
	  /**
	   * 
	   * @return can return <code>null</code>
	   */
	  String getNodeType();

	  void addSuperclassId(String superclassLocator);
	  
	  /**
	   * <p>A generic method<p>
	   * <em>Risks overwriting a property</em>
	   * @param key
	   * @param value one of <code>String</code> or <code>List<String></code>
	   */
	  void setProperty(String key, Object value);
	  
	  /**
	   * <p>Utility method. The first time a <code>value</code> is
	   * added to this <key>, it goes in as a String. The next time,
	   * the property will be converted to a <code>List</code></p>
	   * @param key
	   * @param value
	   */
	  void addPropertyValue(String key, String value);
	  
	  /**
	   * Returns one of <code>String</code> or <code>List<String></code
	   * @param key
	   * @return can return <code>null</code>
	   */
	  Object getProperty(String key);
	  
	  
	  /**
	   * @return can return <code>null</code>
	   */
	  List<String> listSuperclassIds();
	  
//	  void setLanguage(String lang);
	  
	  
	  void setIsPrivate(boolean isPrivate);
	  
	  /**
	   * Utility
	   * @param t
	   */
	  void setIsPrivate(String t);
	  /**
	   * Defaults to <code>false</code>
	   * @return
	   */
	  boolean getIsPrivate();
	  
	  /**
	   * 
	   * @return does not return <code>null</code>
	   */
	  List<String> listRestrictionCredentials();
	  void addRestrictionCredential(String userId);
	  void removeRestrictionCredential(String userId);
	  boolean containsRestrictionCredentials(String userId);
	  
	  void setURL(String url);
	  String getURL();
	  
	  /**
	   * <p>A node can be <em>tagged</em> with Published Subject Indicator values.</p>
	   * <p>If one remains faithful to the topic mapping standards, a node (topic) can
	   * have just <em>one</em> PSI; in work outside topic mapping, it is possible for
	   * a node to have more than one PSI.</p>
	   * @param psi
	   */
	  void addPSI(String psi);
	  
	  /**
	   * 
	   * @return does not return <code>null</code>
	   */
	  List<String> listPSIValues();
	  
	  /**
	   * Add an unrestricted {@link ITuple} to this node
	   * @param tupleLocator
	   */
	  void addTuple(String tupleLocator);
	  
	  /**
	   * Add a restricted (not public) {@link ITuple} to this node
	   * @param tupleLocator
	   */
	  void addRestrictedTuple(String tupleLocator);
	  
	  /**
	   * List tuples linked to this node which are unrestricted
	   * 
	   * @return does not return <code>null</code>
	   */
	  List<String> listTuples();
	  
	  /**
	   * List tuples linked to this node which are restricted (not public)
	   * 
	   * @return does not return <code>null</code>
	   */
	  List<String> listRestrictedTuples();
	  
}
