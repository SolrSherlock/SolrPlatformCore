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

import org.apache.tika.language.LanguageIdentifier;

/**
 * @author park
 *
 */
public class LanguageUtil {

	/**
	 * <p>Can return the wrong language; defaults to "en"</p>
	 * <p>NOTE: we may want to simply truncate the string to some
	 * short max length.</p>
	 * @param string
	 * @return
	 */
	public static String identifyLanguage(String string) {
		if (string == null)
			return "en";
		LanguageIdentifier identifier = new LanguageIdentifier(trimString(string));
    	String language = identifier.getLanguage();
    	boolean isReasonablyCertain = identifier.isReasonablyCertain();
    	if (language.equals("") || !isReasonablyCertain)
    		language = "en"; //defaults
    	return language;
	}
	
	private static String trimString(String string) {
		String result = "";
		String [] x = string.split(" ");
		int len = x.length;
		for (int i = 0;i<len;i++) {
			if (i == 5)
				break;
			result += x[i]+" ";
		}
		return result.trim();
	}

}
