/**
 * 
 */
package org.topicquests.solr.api;

import org.topicquests.model.api.IMergeImplementation;
import org.topicquests.solr.SolrEnvironment;

/**
 * @author park
 *
 */
public interface ISolrMergeImplementation extends IMergeImplementation {

	/**
	 * Initialize the engine
	 * @param environment
	 */
	void init(SolrEnvironment environment);

}
