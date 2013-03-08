/**
 * 
 */
package org.topicquests.common;

import java.util.*;

/**
 * @author park
 *
 */
public class Utils {

	/**
	 * 
	 */
	public Utils() {}

	
	/**
	 * Perform a Set Union on two lists (no duplicates)
	 * @param a
	 * @param b
	 * @return does not return <code>null</code>
	 */
	public List<String> setUnionString(List<String>a, List<String>b) {
		List<String> result = new ArrayList<String>();
		if (a == null && b != null)
			result.addAll(b);
		else if (a != null && b == null)
			result.addAll(a);
		else {
			if (a.size() > b.size()) {
				result.addAll(a);
				doSetUnionStringList(b,result);
			} else if (a.size() < b.size()) {
				result.addAll(b);
				doSetUnionStringList(a,result);
			} else {
				result.addAll(b);
				doSetUnionStringList(a,result);
			}
		}
		return result;
	}
	
	void doSetUnionStringList(List<String>source, List<String>target) {
		Iterator<String>itr = source.iterator();
		String x;
		while (itr.hasNext()) {
			x = itr.next();
			if (!target.contains(x))
				target.add(x);
		}
	}
}
