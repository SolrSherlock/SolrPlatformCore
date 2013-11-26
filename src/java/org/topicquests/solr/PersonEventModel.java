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

import java.util.Date;

import org.topicquests.common.api.ICoreIcons;
import org.topicquests.common.api.IResult;
import org.topicquests.model.api.IEventLegend;
import org.topicquests.model.api.INode;
import org.topicquests.model.api.INodeModel;
import org.topicquests.model.api.IPersonEvent;
import org.topicquests.model.api.IPersonEventModel;
import org.topicquests.model.api.IPersonLegend;
import org.topicquests.solr.api.ISolrDataProvider;

/**
 * @author park
 *
 */
public class PersonEventModel implements IPersonEventModel {
	private SolrEnvironment environment;
	private ISolrDataProvider solr;
	private INodeModel model;
	/**
	 * 
	 */
	public PersonEventModel(SolrEnvironment e) {
		environment = e;
		solr = (ISolrDataProvider)environment.getDataProvider();
		model = solr.getNodeModel();
		
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IPersonEventModel#newPerson(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public IResult newPerson(String firstName, String middleNames,
			String familyName, String nameAppendages, String description,
			String language, Date dateOfBirth, String locationOfBirth,
			String userId, boolean isPrivate) {
		IResult result = model.newInstanceNode(IPersonLegend.PERSON_TYPE, "", description, language, userId, 
				ICoreIcons.PROPERTY_ICON_SM, ICoreIcons.PERSON_ICON, isPrivate);
		IPersonEvent n = (IPersonEvent)result.getResultObject();
		String label = "";
		if (firstName != null && !firstName.equals("")) {
			n.setFirstName(firstName);
			label += firstName;
		} 
		if (middleNames != null && !middleNames.equals("")) {
			n.setMiddleNames(middleNames);
			label += " "+middleNames;
		} 
		if (familyName != null && !familyName.equals("")) {
			label += " "+familyName;
		}
		n.addLabel(label.trim(), language, userId, false);
		if (nameAppendages != null && !nameAppendages.equals("")) {
			n.setNameAppendages(nameAppendages);
			label += " "+nameAppendages;
			n.addLabel(label.trim(), language, userId, false);
		}
		if (dateOfBirth != null)
			n.setStartDate(dateOfBirth);
		if (locationOfBirth != null && !locationOfBirth.equals(""))
			n.setLocationOfOriginName(locationOfBirth);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IPersonEventModel#newEvent(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date, java.lang.String, boolean)
	 */
	@Override
	public IResult newEvent(String eventTypeLocator, String eventName,
			String eventLocation, String description, String language,
			Date eventStartDate, Date eventEndDate, String userId, boolean isPrivate) {
		String eventLox = eventTypeLocator;
		if (eventLox == null || eventLox.equals(""))
			eventLox = IEventLegend.EVENT_TYPE;
		IResult result = model.newInstanceNode(eventLox, eventName, description, language, userId, 
				ICoreIcons.CLASS_ICON_SM, ICoreIcons.CLASS_ICON, isPrivate);
		return result;
	}

}
