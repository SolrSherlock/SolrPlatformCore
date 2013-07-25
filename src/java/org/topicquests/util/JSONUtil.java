/**
 * 
 */
package org.topicquests.util;

import java.util.*;
import org.json.simple.JSONObject;

/**
 * @author park
 *
 */
public class JSONUtil {

	public static JSONObject map2JSONObject(Map map) {
		JSONObject result = new JSONObject();
		Iterator itr = map.keySet().iterator();
		Object key;
		while (itr.hasNext()) {
			key = itr.next();
			result.put(key, map.get(key));
		}
		
		return result;
	}
}
