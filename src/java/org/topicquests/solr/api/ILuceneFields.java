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

/**
 * @author park
 * @license Apache2
 * @copyright 2012, TopicQuests
 */
public interface ILuceneFields {
	public static final String
		//e.g. BigData (tag), gardenfelder (user)
		HANDLE				= "handle",
		//e.g. http://twitter.com/bigdata/statuses/200980448644575235
		GUID				= "id", // cannot be "guid"
		//e.g. Thu, 10 May 2012 17:50:02 +0000
		DATE				= "datestring",
		BODY				= "body",
		//#tag
		TAG					= "tag",
		// @user
		USER_REF			= "uref",
		//hrefs come in as bit.ly -- need to unbundle those
		HREF				=  "hrefstring",
		//individual, url, or tag
		TYPE				= "type";
	

}
