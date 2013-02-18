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
package org.topicquests.model;

import java.util.*;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.solr.schema.DateField;
import org.topicquests.model.api.ICitation;
import org.topicquests.model.api.INode;
import org.topicquests.model.api.ITuple;
import org.topicquests.model.api.IValueMatrix;
import org.topicquests.model.api.IXMLFields;
import org.topicquests.model.api.IConceptualGraph;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.common.api.IRelationsLegend;

/**
 * @author park
 *
 */
public class Node implements 
		INode, ITuple, ICitation, 
		IValueMatrix, IConceptualGraph {
	private Map<String,Object>properties;
	
	/**
	 * 
	 */
	public Node() {
		this(new HashMap<String,Object>());
	}

	/**
	 * Constructor used when creating from a Solr hit
	 * @param props
	 * @param d  can be <code>null</code> when creating a new node
	 * but must not be <code>null</code> when fetching a node for later
	 * possible updates
	 */
	public Node(Map<String,Object>props) {
//		db = d;
//		model = db.getNodeModel();
		//TODO
		//IT's not this simple: must pull apart that map to make ours
		//NOTE: seems to work!
		properties = props;
	}
	
	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#getLocator()
	 */
	public String getLocator() {
		return (String)properties.get(ITopicQuestsOntology.LOCATOR_PROPERTY);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#getLabel()
	 */
	public String getLabel(String language) {
		String field = makeField(ITopicQuestsOntology.LABEL_PROPERTY,language);
		return getFirstListValue(field);
	}
	
	String makeField(String fieldBase, String language) {
		String result = fieldBase;
		if (!language.equals("en"))
			result += language;
		return result;
	}
//	String stripLanguage(String inString) {
//		return inString.substring(0,inString.lastIndexOf('@'));
//	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#getNodeType()
	 */
	public String getNodeType() {
		return (String)properties.get(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#getSmallImage()
	 */
	public String getSmallImage() {
		return (String)properties.get(ITopicQuestsOntology.SMALL_IMAGE_PATH);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#getImage()
	 */
	public String getImage() {
		return (String)properties.get(ITopicQuestsOntology.LARGE_IMAGE_PATH);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#getCreatorId()
	 */
	public String getCreatorId() {
		return (String)properties.get(ITopicQuestsOntology.CREATOR_ID_PROPERTY);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#getDetails()
	 */
	public String getDetails(String language) {
		String field = makeField(ITopicQuestsOntology.DETAILS_PROPERTY,language);
		return getFirstListValue(field);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#setLabel(java.lang.String)
	 * IS MULTIVALUED
	 */
	public void addLabel(String label, String language, String userId, boolean isLanguageAddition) {
		String field = makeField(ITopicQuestsOntology.LABEL_PROPERTY,language);
		addMultivaluedSetStringProperty(field, label);
	}

	private void addMultivaluedSetStringProperty(String key, String value) {
//		System.out.println("AMSSP- "+key+" | "+value);
//		System.out.println("AMSSP-1 "+properties);
		Object o = properties.get(key);
		List<String> ll;
		if (o == null) {
			ll = new ArrayList<String>();
			properties.put(key, ll);
		} else if ( o instanceof String) {
			ll = new ArrayList<String>();
			ll.add((String)o);
			properties.put(key, ll);
		} else {
			ll = (List<String>)o;
		}
		if (!ll.contains(value))
			ll.add(value);
//		System.out.println("AMSSP-2 "+properties);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#setDetails(java.lang.String)
	 */
	public void addDetails(String details, String language, String userId, boolean isLanguageAddition) {
		String field = makeField(ITopicQuestsOntology.DETAILS_PROPERTY,language);
		addMultivaluedSetStringProperty(field, details);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#setLocator(java.lang.String)
	 */
	public void setLocator(String locator) {
		properties.put(ITopicQuestsOntology.LOCATOR_PROPERTY, locator);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#setDate(java.util.Date)
	 * /
	public void setDate(String date) {
		//In Theory, Solr converts this to Z (UTC)
		properties.put(ITopicQuestsOntology.CREATED_DATE_PROPERTY, date);
	} */
	public void setDate(Date date) {
		properties.put(ITopicQuestsOntology.CREATED_DATE_PROPERTY, date);
	}

	public void setLastEditDate(Date date) {
		properties.put(ITopicQuestsOntology.LAST_EDIT_DATE_PROPERTY, date);
	}

/*	public void setLastEditDate(String date) {
		//TODO this is an update issue
		properties.put(ITopicQuestsOntology.LAST_EDIT_DATE_PROPERTY, date);
	}
*/
	
	public Date getDate() {
		return (Date)properties.get(ITopicQuestsOntology.CREATED_DATE_PROPERTY);
	}
	public Date getLastEditDate() {
		return (Date)properties.get(ITopicQuestsOntology.CREATED_DATE_PROPERTY);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#setSmallImage(java.lang.String)
	 */
	public void setSmallImage(String img) {
		properties.put(ITopicQuestsOntology.SMALL_IMAGE_PATH, img);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#setImage(java.lang.String)
	 */
	public void setImage(String img) {
		properties.put(ITopicQuestsOntology.LARGE_IMAGE_PATH, img);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#setCreatorId(java.lang.String)
	 */
	public void setCreatorId(String id) {
		properties.put(ITopicQuestsOntology.CREATOR_ID_PROPERTY, id);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#setNodeType(java.lang.String)
	 */
	public void setNodeType(String typeLocator) {
		properties.put(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, typeLocator);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#addSuperclassId(java.lang.String)
	 */
	public void addSuperclassId(String superclassLocator) {
		List<String> ids = listSuperclassIds();
		if (ids == null || ids.size() == 0) {
			ids = new ArrayList<String>();
			properties.put(ITopicQuestsOntology.SUBCLASS_OF_PROPERTY_TYPE, ids);
		}
		if (!ids.contains(superclassLocator))
			ids.add(superclassLocator);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#listSuperclassIds()
	 */
	public List<String> listSuperclassIds() {
		return getMultivaluedProperty(ITopicQuestsOntology.SUBCLASS_OF_PROPERTY_TYPE);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#setIsPrivate(boolean)
	 */
	public void setIsPrivate(boolean isPrivate) {
		String x = (isPrivate ? "true":"false");
		properties.put(ITopicQuestsOntology.IS_PRIVATE_PROPERTY, x);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#getIsPrivate()
	 */
	public boolean getIsPrivate() {
		Object x = properties.get(ITopicQuestsOntology.IS_PRIVATE_PROPERTY);
		if (x != null) {
			if (x instanceof String) {
				if (x != null) {
					return Boolean.parseBoolean((String)x);
				}
			} else {
				return ((Boolean)x).booleanValue();
			}
		}
			
		return false;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#setURL(java.lang.String)
	 */
	public void setURL(String url) {
		properties.put(ITopicQuestsOntology.RESOURCE_URL_PROPERTY, url);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#getURL()
	 */
	public String getURL() {
		return (String)properties.get(ITopicQuestsOntology.RESOURCE_URL_PROPERTY);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INode#toMap()
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	public List<String> listRestrictionCredentials() {
		List<String> result = getMultivaluedProperty(ITopicQuestsOntology.RESTRICTION_PROPERTY_TYPE);
//		if (result == null)
//			result = new ArrayList<String>();
		return result;
	}

	/**
	 * <p>Utility method for multivalued properties which may return a String</p>
	 * <p>If this turns a string value into a list value, it installs that list in properties</p>
	 * @param key
	 * @return does not return <code>null</code>
	 */
	List<String> getMultivaluedProperty(String key) {
		List<String> result = null;
		Object op = properties.get(key);
//		System.out.println("GMP-1 "+getLocator()+" "+key+" "+op+" "+(op instanceof ArrayList));
		if (op != null) {
			if (op instanceof String) {
				result = new ArrayList<String>();
				result.add((String)op);
//				System.out.println("GMP-2" +result);
			} else {
				result = (List<String>)op;
//				System.out.println("GMP-3" +result);
			} 
		} else {
			result = new ArrayList<String>();
			properties.put(key, result);
		}
		return result;
	}
	public void addRestrictionCredential(String userId) {
		List<String> l = getMultivaluedProperty(ITopicQuestsOntology.RESTRICTION_PROPERTY_TYPE);
		if (!l.contains(userId))
			l.add(userId);
	}

	public void removeRestrictionCredential(String userId) {
		List<String> l = (List<String>)properties.get(ITopicQuestsOntology.RESTRICTION_PROPERTY_TYPE);
		if (l != null)
			l.remove(userId);
		//TODO technically speaking, if l == null, this is really an error condition
		//since the caller expects that userId does exist on a list
//		if (db != null)
//			model.updateNode(this.getLocator(), ITopicQuestsOntology.RESTRICTION_PROPERTY_TYPE, l);
	}

	public boolean containsRestrictionCredentials(String userId) {
		List<String> creds = listRestrictionCredentials();
		return creds.contains(userId);
	}
	
	public void addPSI(String psi) {
		properties.put(ITopicQuestsOntology.PSI_PROPERTY_TYPE, psi);
		List<String> ids = getMultivaluedProperty(ITopicQuestsOntology.PSI_PROPERTY_TYPE);
		// no duplicates allowed
		if (!ids.contains(psi))
			ids.add(psi);
//		if (db != null)
//			model.updateNode(this.getLocator(), ITopicQuestsOntology.PSI_PROPERTY_TYPE, 
//				(List<String>)properties.get(ITopicQuestsOntology.PSI_PROPERTY_TYPE));
	}

	public List<String> listPSIValues() {
		List<String> result = getMultivaluedProperty(ITopicQuestsOntology.PSI_PROPERTY_TYPE);
		return result;
	}

//TODO HUGE ISSUE WITH internal updates -- must do version if _version_ is available

	public void addTuple(String tupleLocator) {
//		System.out.println("ADDTUPLE- "+properties);
		addMultivaluedSetStringProperty(ITopicQuestsOntology.TUPLE_LIST_PROPERTY,tupleLocator);
//		System.out.println("ADDTUPLE-0 "+properties.get(ITopicQuestsOntology.TUPLE_LIST_PROPERTY));
	}
	@Override
	public void addRestrictedTuple(String tupleLocator) {
//		System.out.println("ADDTUPLERestricted- "+properties);
		addMultivaluedSetStringProperty(ITopicQuestsOntology.TUPLE_LIST_PROPERTY_RESTRICTED,tupleLocator);
//		System.out.println("ADDTUPLERestricted-0 "+properties.get(ITopicQuestsOntology.TUPLE_LIST_PROPERTY_RESTRICTED));
	}

	public List<String> listTuples() {
		List<String> result = this.getMultivaluedProperty(ITopicQuestsOntology.TUPLE_LIST_PROPERTY);
		if (result == null)
			result = new ArrayList<String>();
		return result;
	}

	public List<String> listRestrictedTuples() {
		List<String> result = this.getMultivaluedProperty(ITopicQuestsOntology.TUPLE_LIST_PROPERTY_RESTRICTED);
		if (result == null)
			result = new ArrayList<String>();
		return result;
	}

	public List<String> listLabels() {
		List<String> l = fetchLabels(ITopicQuestsOntology.LABEL_PROPERTY);
		return concatinateStringLists(l);
	}

	public List<String> listDetails() {
		List<String> l = fetchLabels(ITopicQuestsOntology.DETAILS_PROPERTY);
		return concatinateStringLists(l);
	}

	public void setObject(String value) {
		properties.put(ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY, value);
	}

	public void setObjectType(String typeLocator) {
		properties.put(ITopicQuestsOntology.TUPLE_OBJECT_TYPE_PROPERTY, typeLocator);
	}

	public String getObject() {
		return (String)properties.get(ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY);
	}

	public String getObjectType() {
		return (String)properties.get(ITopicQuestsOntology.TUPLE_OBJECT_TYPE_PROPERTY);
	}

	public void setSubjectLocator(String locator) {
		properties.put(ITopicQuestsOntology.TUPLE_SUBJECT_PROPERTY, locator);
	}

	public String getSubjectLocator() {
		return (String)properties.get(ITopicQuestsOntology.TUPLE_SUBJECT_PROPERTY);
	}

	public void setSubjectType(String subjectType) {
		properties.put(ITopicQuestsOntology.TUPLE_SUBJECT_TYPE_PROPERTY, subjectType);
	}

	public String getSubjectType() {
		return (String)properties.get(ITopicQuestsOntology.TUPLE_SUBJECT_TYPE_PROPERTY);
	}
/**
	public void setRelationType(String typeLocator) {
		properties.put(ITopicQuestsOntology.TUPLE_RELATION_TYPE_PROPERTY, typeLocator);
	}

	public String getRelationType() {
		return (String)properties.get(ITopicQuestsOntology.TUPLE_RELATION_TYPE_PROPERTY);
	}

	public void setRelationLocator(String relationLocator) {
		properties.put(ITopicQuestsOntology.TUPLE_RELATION_LOCATOR_PROPERTY, relationLocator);
	}

	public String getRelationLocator() {
		return (String)properties.get(ITopicQuestsOntology.TUPLE_RELATION_LOCATOR_PROPERTY);
	}
*/
	public void setIsTransclude(boolean isT) {
		String x = (isT ? "true":"false");
		properties.put(ITopicQuestsOntology.TUPLE_IS_TRANSCLUDE_PROPERTY, x);
	}

	public boolean getIsTransclude() {
		Object x = properties.get(ITopicQuestsOntology.TUPLE_IS_TRANSCLUDE_PROPERTY);
		if (x != null) {
			if (x instanceof String) {
				if (x != null) {
					return Boolean.parseBoolean((String)x);
				}
			} else {
				return ((Boolean)x).booleanValue();
			}
		}
		return false;
	}

	public String toJSON() {
		JSONObject jo = (JSONObject) JSONSerializer.toJSON( properties ); 
		//Solr date properties are actually some sort of Date object
		jo.put(ITopicQuestsOntology.CREATED_DATE_PROPERTY, properties.get(ITopicQuestsOntology.CREATED_DATE_PROPERTY).toString());
		//TODO if we add edited date property, must fix that too
		return jo.toString();
	}

	public IResult doUpdate() {
		IResult result = new ResultPojo();
		IResult temp;
		//change lastEditDate
		setLastEditDate(new Date());
		return result;
	}

	@Override
	public void setVersion(String version) {
		properties.put(ITopicQuestsOntology.SOLR_VERSION_PROPERTY_TYPE, version);
	}

	@Override
	public String getVersion() {
		return (String)properties.get(ITopicQuestsOntology.SOLR_VERSION_PROPERTY_TYPE);
	}

	@Override
	public String toXML() {
		StringBuilder buf = new StringBuilder();
		String nx = IXMLFields.NODE;
		String test = getSubjectLocator();
		//is this a node or a tuple?
		if (isTuple())
			nx = IXMLFields.TUPLE;
		buf.append("<"+nx+" "+IXMLFields.LOCATOR_ATT+"=\""+getLocator()+"\">\n");
		Iterator<String> keys = this.properties.keySet().iterator();
		String key;
		Object val;
		while (keys.hasNext()) {
			key = keys.next();
			if (!key.equals(ITopicQuestsOntology.LOCATOR_PROPERTY)) {
				buf.append("  <"+IXMLFields.PROPERTY+" "+IXMLFields.KEY_ATT+"=\""+key+"\" >\n");
				val = properties.get(key);
				appendValue(val,buf,key);
				buf.append("  </"+IXMLFields.PROPERTY+">\n");
			}
		}
		buf.append("</"+nx+">\n");
//		System.out.println("XXXX "+buf.toString());
		return buf.toString();
	}

	private void appendValue(Object value, StringBuilder buf, String key) {
		if (value instanceof List) {
			Iterator<String>itr = ((List<String>)value).iterator();
			while (itr.hasNext()) 
				buf.append("    <"+IXMLFields.VALUE+"><![CDATA["+itr.next()+"]]></"+IXMLFields.VALUE+">\n");

		} else {
			//MUST TEST FOR DATES
			if (key.equals(ITopicQuestsOntology.CREATED_DATE_PROPERTY) || 
				key.equals(ITopicQuestsOntology.LAST_EDIT_DATE_PROPERTY)) {
				Date d = (Date)(value); //
				buf.append("    <"+IXMLFields.VALUE+"><![CDATA["+DateField.formatExternal(d)+"]]></"+IXMLFields.VALUE+">\n");
			} else
				buf.append("    <"+IXMLFields.VALUE+"><![CDATA["+value+"]]></"+IXMLFields.VALUE+">\n");
		}
	}

	@Override
	public boolean isTuple() {
		String test = getSubjectLocator();
		//is this a node or a tuple?
		if (test != null && ! test.equals(""))
			return true;
		return false;
	}

	@Override
	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	@Override
	public Object getProperty(String key) {
		return properties.get(key);
	}

	@Override
	public void addPropertyValue(String key, String value) {
		Object vx = getProperty(key);
		if (vx == null)
			setProperty(key,value);
		else {
			List<String>vl = null;
			if (vx instanceof String) {
				vl = new ArrayList<String>();
				vl.add((String)vx);
				properties.put(key, vl);
			} else
				vl = (List<String>)vx;
			vl.add(value);
		}
		
	}

	@Override
	public void setIsTransclude(String t) {
		if (t.equalsIgnoreCase("true"))
			setIsTransclude(true);
		else
			setIsTransclude(false);
	}

	@Override
	public void setIsPrivate(String t) {
		if (t.equalsIgnoreCase("true"))
			setIsPrivate(true);
		else
			setIsPrivate(false);		
	}

	@Override
	public void setObjectRole(String roleLocator) {
		properties.put(ITopicQuestsOntology.TUPLE_OBJECT_ROLE_PROPERTY, roleLocator);
	}

	@Override
	public String getObjectRole() {
		return (String)properties.get(ITopicQuestsOntology.TUPLE_OBJECT_ROLE_PROPERTY);
	}

	@Override
	public void setSubjectRole(String roleLocator) {
		properties.put(ITopicQuestsOntology.TUPLE_SUBJECT_ROLE_PROPERTY, roleLocator);
	}

	@Override
	public String getSubjectRole() {
		return (String)properties.get(ITopicQuestsOntology.TUPLE_SUBJECT_ROLE_PROPERTY);
	}

	@Override
	public void addScope(String scopeLocator) {
		this.addPropertyValue(ITopicQuestsOntology.SCOPE_LIST_PROPERTY_TYPE, scopeLocator);
	}

	@Override
	public List<String> listScopes() {
		return this.getMultivaluedProperty(ITopicQuestsOntology.SCOPE_LIST_PROPERTY_TYPE);
	}

	@Override
	public void setRelationWeight(double weight) {
		properties.put(IRelationsLegend.RELATION_WEIGHT, new Double(weight));
	}

	@Override
	public double getRelationWeight() {
		Double d = (Double)properties.get(IRelationsLegend.RELATION_WEIGHT);
		if (d == null)
			return -9999;
		return d.doubleValue();
	}

	@Override
	public void addSmallLabel(String label, String language, String userId,
			boolean isLanguageAddition) {
		String field = makeField(ITopicQuestsOntology.SMALL_LABEL_PROPERTY,language);
		addMultivaluedSetStringProperty(field, label);
	}

	@Override
	public String getSmallLabel(String language) {
		String field = makeField(ITopicQuestsOntology.SMALL_LABEL_PROPERTY,language);
		return getFirstListValue(field);
	}

	String getFirstListValue(String key) {
		List<String> l = this.getMultivaluedProperty(key);
		if (l.size()> 0)
			return l.get(0);		
		return "";
	}
	@Override
	public List<String> listLabels(String language) {
		String field = makeField(ITopicQuestsOntology.DETAILS_PROPERTY,language);
		return this.getMultivaluedProperty(field);
	}

	@Override
	public List<String> listSmallLabels() {
		List<String> l = fetchLabels(ITopicQuestsOntology.SMALL_LABEL_PROPERTY);
		return concatinateStringLists(l);
	}

	@Override
	public List<String> listSmallLabels(String language) {
		String field = makeField(ITopicQuestsOntology.SMALL_LABEL_PROPERTY,language);
		return this.getMultivaluedProperty(field);
	}

	@Override
	public List<String> listDetails(String language) {
		String field = makeField(ITopicQuestsOntology.DETAILS_PROPERTY,language);
		return this.getMultivaluedProperty(field);
	}

	/**
	 * Concatinate all the List<String> values indexed by <code>keys</code>
	 * @param keys
	 * @return
	 */
	List<String> concatinateStringLists(List<String>keys) {
		List<String>result = new ArrayList<String>();
		int len= keys.size();
		String key;
		for (int i=0;i<len;i++) {
			key = keys.get(i);
			result.addAll(this.getMultivaluedProperty(key));
		}
		return result;
	}
	/**
	 * Given a <code>baseField</code>, e.g. "label", fetch all
	 * label fields regardless of language codes
	 * @param baseField
	 * @return
	 */
	List<String> fetchLabels(String baseField) {
		List<String>result = new ArrayList();
		Iterator<String>itr = properties.keySet().iterator();
		String key;
		while (itr.hasNext()) {
			key = itr.next();
			if (key.startsWith(baseField))
				result.add(key);
		}
		return result;
	}

	@Override
	public List<String> listConceptLocators() {
		List<String> result = getMultivaluedProperty(ITopicQuestsOntology.GRAPH_CONCEPT_LIST_PROPERTY_TYPE);
		return result;
	}

	@Override
	public void addConceptLocator(String locator) {
		addMultivaluedSetStringProperty(ITopicQuestsOntology.GRAPH_CONCEPT_LIST_PROPERTY_TYPE,locator);
	}

	@Override
	public void removeConceptLocator(String locator) {
		List<String> l = (List<String>)properties.get(ITopicQuestsOntology.GRAPH_CONCEPT_LIST_PROPERTY_TYPE);
		if (l != null)
			l.remove(locator);
	}

	@Override
	public List<String> listRelationLocators() {
		List<String> result = getMultivaluedProperty(ITopicQuestsOntology.GRAPH_RELATION_LIST_PROPERTY_TYPE);
		return result;
	}

	@Override
	public void addRelationLocator(String locator) {
		addMultivaluedSetStringProperty(ITopicQuestsOntology.GRAPH_RELATION_LIST_PROPERTY_TYPE,locator);
	}

	@Override
	public void removeRelationLocator(String locator) {
		List<String> l = (List<String>)properties.get(ITopicQuestsOntology.GRAPH_RELATION_LIST_PROPERTY_TYPE);
		if (l != null)
			l.remove(locator);
	}

	@Override
	public String getParentGraphLocator() {
		return (String)properties.get(ITopicQuestsOntology.GRAPH_PARENT_GRAPH_PROPERTY_TYPE);
	}

	@Override
	public void setParentGraphLocator(String locator) {
		properties.put(ITopicQuestsOntology.GRAPH_PARENT_GRAPH_PROPERTY_TYPE, locator);
	}

}
