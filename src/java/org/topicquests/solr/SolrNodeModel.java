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

import org.apache.log4j.Logger;
import java.util.*;

import org.topicquests.common.api.IResult;
import org.topicquests.common.ResultPojo;
import org.topicquests.model.api.INodeModel;
import org.topicquests.model.api.INode;
import org.topicquests.model.api.ITuple;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.solr.api.ISolrDataProvider;
import org.topicquests.model.Node;

import org.apache.solr.schema.DateField;

/**
 * @author park
 *
 */
public class SolrNodeModel implements INodeModel {
	private Logger log = Logger.getLogger(SolrNodeModel.class);
	private ISolrDataProvider database;
	private DateField dateField;

	/**
	 * 
	 */
	public SolrNodeModel(ISolrDataProvider p) {
		database = p;
		dateField = new DateField();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public IResult newNode(String locator, String label, String description,
			String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate) {
		INode n = new Node();
		System.out.println("SolrNodeModel.newNode- "+locator);
		IResult result = new ResultPojo();
		result.setResultObject(n);
		n.setLocator(locator);
		n.setCreatorId(userId);
		Date d = new Date();
		n.setDate(d); 
		n.setLastEditDate(d);
		if (label != null)
			n.addLabel(label, lang, userId, false);
		
		if (smallImagePath != null)
			n.setSmallImage(smallImagePath);
		if (largeImagePath != null)
			n.setImage(largeImagePath);
		if (description != null)
			n.addDetails(description, lang, userId, false);
		n.setIsPrivate(isPrivate);
		System.out.println("SolrNodeModel.newNode+ "+locator);		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public IResult newNode(String label, String description, String lang,
			String userId, String smallImagePath, String largeImagePath, boolean isPrivate) {
		IResult result = newNode(database.getUUID(),label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newSubclassNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public IResult newSubclassNode(String locator, String superclassLocator,
			String label, String description, String lang, String userId,
			String smallImagePath, String largeImagePath, boolean isPrivate) {
		IResult result = newNode(locator,label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
		INode n = (INode)result.getResultObject();
		n.addSuperclassId(superclassLocator);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newSubclassNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public IResult newSubclassNode(String superclassLocator, String label,
			String description, String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate) {
		IResult result = newNode(label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
		INode n = (INode)result.getResultObject();
		n.addSuperclassId(superclassLocator);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newNamedSubclassNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 * /
	public IResult newNamedSubclassNode(String superclassLocator, String name,
			String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newNamedSubclassNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 * /
	public IResult newNamedSubclassNode(String locator,
			String superclassLocator, String name, String lang, String userId,
			String smallImagePath, String largeImagePath, boolean isPrivate) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}
*/
	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newInstanceNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public IResult newInstanceNode(String locator, String typeLocator,
			String label, String description, String lang, String userId,
			String smallImagePath, String largeImagePath, boolean isPrivate) {
		IResult result = newNode(locator,label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
		INode n = (INode)result.getResultObject();
		n.setNodeType(typeLocator);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newInstanceNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public IResult newInstanceNode(String typeLocator, String label,
			String description, String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate) {
		IResult result = newNode(label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
		INode n = (INode)result.getResultObject();
		n.setNodeType(typeLocator);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newNamedInstanceNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 * /
	public IResult newNamedInstanceNode(String typeLocator, String name,
			String lang, String userId, boolean isPrivate) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newNamedInstanceNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 * /
	public IResult newNamedInstanceNode(String locator, String typeLocator,
			String name, String lang, String userId, boolean isPrivate) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}
*/
	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#getNode(java.lang.String)
	 * /
	public IResult getNodeByPSI(String psi) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#getNodeByLocator(java.lang.String)
	 * /
	public IResult getNodeByLocator(String locator) {
		String query = ITopicQuestsOntology.LOCATOR_PROPERTY+":"+locator;
		return database.runQuery(query, 0, -1);
	}
*/

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#removeNode(java.lang.String)
	 */
	public IResult removeNode(String locator) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/**
	 * <p><em>Dealing with Version in updating nodes being linked</em></p>
	 * <p>It turns out you must use partialUpdate, and the particular Solr field
	 * being updated needs to be dropped into a <code>Map<String,Object></code>, and
	 * that map is then dropped into the field (property type). Solr then knows what 
	 * to do with that.</p>
	 */
	public IResult relateNodes(String sourceNodeLocator,
			String targetNodeLocator, String relationTypeLocator,
			String userId, String smallImagePath, String largeImagePath, boolean isTransclude, boolean isPrivate) {
		IResult result = new ResultPojo();
		Set<String> credentials = getDefaultCredentials(userId);
		String details = sourceNodeLocator+" "+relationTypeLocator+" "+targetNodeLocator;
		//NOTE that we make the tuple an instance of the relation type, not of TUPLE_TYPE
		ITuple t = (ITuple)this.newInstanceNode(relationTypeLocator, relationTypeLocator, details, "en", userId, null, null, false).getResultObject();
		t.setIsTransclude(isTransclude);
		t.setObject(targetNodeLocator);
		t.setObjectType(ITopicQuestsOntology.NODE_TYPE);
		t.setSubjectLocator(sourceNodeLocator);
		t.setSubjectType(ITopicQuestsOntology.NODE_TYPE);
		if (smallImagePath != null)
			t.setSmallImage(smallImagePath);
		if (largeImagePath != null)
			t.setImage(largeImagePath);
		IResult x = database.putNode(t);
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		//fetch the source actor node
		x = database.getNode(sourceNodeLocator, credentials);
		INode nxA;
		INode nxB;
		Map<String,Object> updateMap = new HashMap<String,Object>();
		Map<String,String> newMap = new HashMap<String,String>();
		Map<String,Object> myMap = null;
		System.out.println("RELATO-1 "+x.hasError()+" "+sourceNodeLocator);
		List<String> theList = null;
		String tLoc = t.getLocator();
		boolean isRestricted = isPrivate; // seed restriction test
		if (!x.hasError()) {
			nxA = (INode)x.getResultObject();
			isRestricted = isRestricted || nxA.getIsPrivate();
			System.out.println("Relate-0: "+nxA.toXML());
			myMap = nxA.getProperties();
			//fetch the target actor node
			x = database.getNode(targetNodeLocator, credentials);
			if (!x.hasError()) {
				nxB = (INode)x.getResultObject();
				isRestricted = isRestricted || nxB.getIsPrivate();
				if (!isRestricted)
					nxA.addTuple(tLoc);
				else
					nxA.addRestrictedTuple(tLoc);
				theList = nxA.listTuples();
				System.out.println("Relate-1: "+isRestricted+" "+nxB.toXML());
				updateMap.put(ITopicQuestsOntology.LOCATOR_PROPERTY, myMap.get(ITopicQuestsOntology.LOCATOR_PROPERTY));
				updateMap.put(ITopicQuestsOntology.SOLR_VERSION_PROPERTY_TYPE, myMap.get(ITopicQuestsOntology.SOLR_VERSION_PROPERTY_TYPE));
				newMap.put(getUpdateKey(theList), t.getLocator());
				if (!isRestricted)
					updateMap.put(ITopicQuestsOntology.TUPLE_LIST_PROPERTY, newMap);
				else
					updateMap.put(ITopicQuestsOntology.TUPLE_LIST_PROPERTY_RESTRICTED, newMap);
				
				x = database.partialUpdateData(updateMap);
				System.out.println("RELATO-2 "+x.hasError()+" "+sourceNodeLocator);
				if (!x.hasError()) {	
					System.out.println("RELATO-3 "+x.hasError()+" "+targetNodeLocator);
					System.out.println("Relate-2: "+nxB.toXML());
					if (!isRestricted)
						nxB.addTuple(tLoc);
					else
						nxB.addRestrictedTuple(tLoc);
					theList = nxB.listTuples();
					System.out.println("Relate-3: "+nxB.toXML());
					myMap = nxB.getProperties();
					updateMap.clear();
					newMap.clear();
					updateMap.put(ITopicQuestsOntology.LOCATOR_PROPERTY, myMap.get(ITopicQuestsOntology.LOCATOR_PROPERTY));
					updateMap.put(ITopicQuestsOntology.SOLR_VERSION_PROPERTY_TYPE, myMap.get(ITopicQuestsOntology.SOLR_VERSION_PROPERTY_TYPE));
					newMap.put(getUpdateKey(theList),t.getLocator());
					if (!isRestricted)
						updateMap.put(ITopicQuestsOntology.TUPLE_LIST_PROPERTY, newMap);
					else
						updateMap.put(ITopicQuestsOntology.TUPLE_LIST_PROPERTY_RESTRICTED, newMap);
					
					x = database.partialUpdateData(updateMap);
					if (x.hasError())
						result.addErrorString(x.getErrorString());
				} else
					result.addErrorString(x.getErrorString());
			} else
				result.addErrorString(x.getErrorString());	
		} else
			result.addErrorString(x.getErrorString());
		return result;
	}

	private String getUpdateKey(List<String>list) {
		if (list.size() > 1)
			return "add";
		else
			return "set";
	}
	public Set<String> getDefaultCredentials(String userId) {
		Set<String>result = new HashSet<String>();
		result.add(userId);
		return result;
	}

	public IResult assertMerge(String sourceNodeLocator,
			String mergedNodeLocator, List<String> mergeRuleLocators,
			double mergeConfidence, String userLocator) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	public IResult updateNode(String nodeLocator, String updatedLabel,
			String updatedDetails, String language, String userId,
			boolean isLanguageAddition) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}
	@Override
	public IResult updateNode(String nodeLocator, String propertyKey,
			List<String> propertyValues) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}
	@Override
	public IResult updateNode(String nodeLocator, String propertyKey,
			String propertyValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public IResult newTuple(String relationType, String subjectId,
			String objectType, String objectVal, String relationId,
			String userId, boolean isTransclude) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	public String dateToSolrDate(Date d) {
		return dateField.formatExternal(d);
	}



}
