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
 * <p>These are the language codes for which Solr offers text analyzers for
 * indexing as of 4.1 in schema.xml</p>
 * <p>NOTE: "en" is a default set of fields</p>
 * <p>NOTE: nothing for Chinese has been found</p>
 * @see http://www.science.co.il/language/locale-codes.asp
 * <p>The intent is to defined language fields for label, smalllabel, and details
 * with these extensions, to use the full Solr indexing power</p>
 */
public interface ISolrLanguageCodes {
	public static final String
		TURKISH				= "tr",
		THAI				= "th",
		SWEDISH			 	= "sv",
		RUSSIAN				= "ru",
		ROMAINAN			= "ro",
		PORTUGUESE			= "pt",
		NORWEGION			= "no",
		DUTCH				= "nl",
		LATVIAN				= "lv",
		JAPANESE			= "ja",
		ITALIAN				= "it",
		INDONESIAN			= "id",
		ARMENIAN			= "hy",
		HUNGARIAN			= "hu",
		HINDI				= "hi",
		GALICIAN			= "gl",
		IRISH				= "ga",
		FRENCH				= "fr",
		FINNISH				= "fi",
		PERSIAN				= "fa",
		BASQUE				= "eu",
		SPANISH				= "es",
		GREEK				= "el",
		GERMAN  			= "de",
		DANISH				= "da",
		CZECH				= "cz",
		CATALAN				= "ca",
		BULGARIAN			= "bg",
		ARABIC				= "ar";

}
