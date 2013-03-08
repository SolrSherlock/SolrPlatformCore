/**
 * 
 */
package org.topicquests.common.api;

/**
 * @author park
 *
 */
public interface IHarvestingOntology {
	/**
	 * Properties (fields)
	 */
	public static final String
			CLUSTER_WEIGHT		= "clusterWeight", //implemented as a <code>double</code> field
			/** resource topics as members of a cluster node */
			CLUSTER_MEMBER_LIST = "clusterMembers",
			/** cluster nodes in a cluster */
			CLUSTER_NODE_MEMBER_LIST = "clusterNodeMembers";
			
	
	/**
	 * Types
	 */
	public static final String
			/** overriding Cluster, based on a query */
			CLUSTER_TYPE				= "ClusterType",
			/** individual cluster node in a cluster, with weights and members */
			CLUSTER_NODE_TYPE			= "ClusterNodeType",
			/** nodes created by carrot2 */
			CARROT2_CLUSTERED_NODE_TYPE	= "Carrot2ClusteredNodeType",
			/** Carrot2 as a user agent*/
			CARROT2_AGENT_USER			= "Carrot2AgentUser",
			/** Tuples, when crafted during agent-based harvesting,
			 * need this scope type; it intends to capture the full
			 * measure of provenance of the <em>assertion</em> made
			 * during harvesting.
			 */
			HARVEST_SCOPE_TYPE			= "HarvestScopeType";

}
