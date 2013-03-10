/**
 * 
 */
package org.topicquests.solr.merge;

import java.util.*;

import org.topicquests.common.ResultPojo;
import org.topicquests.common.Utils;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.ICoreIcons;
import org.topicquests.model.api.IMergeImplementation;
import org.topicquests.model.api.INode;
import org.topicquests.model.api.INodeModel;
import org.topicquests.model.api.ITuple;
import org.topicquests.model.api.ITupleQuery;
import org.topicquests.solr.SolrEnvironment;
import org.topicquests.solr.api.ISolrDataProvider;
import org.topicquests.solr.api.ISolrQueryIterator;
import org.topicquests.util.LoggingPlatform;

/**
 * @author park
 *
 */
public class MergeBean implements IMergeImplementation {
	private LoggingPlatform log = LoggingPlatform.getInstance();
	private SolrEnvironment solrEnvironment;
	private ISolrDataProvider database;
	private ITupleQuery tupleQuery;
	private INodeModel nodeModel;
	private Set<String>credentials;
	
	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IMergeImplementation#init(org.topicquests.solr.SolrEnvironment)
	 */
	@Override
	public void init(SolrEnvironment environment) {
		solrEnvironment = environment;
		database = solrEnvironment.getDataProvider();
		tupleQuery = database.getTupleQuery();
		credentials = new HashSet<String>();
		credentials.add("admin");
	}
	//////////////////////////////////////////////////
	//Here's the deal:
	//  This is complex.
	//  Merging two topics means forging a VirtualProxy (just another Node)
	//  which represents a kind of Union of the two, and any others for that subject
	//  Where things get complex is that you have to figure out what to do with
	//    whatever links to the merged node.
	//  nodeA is the original node; it is wired into the graph
	//  Given nodeA to be merged with nodeB
	//     create nodeV IF NEEDED (there may already be one)
	//			make sure it represents a SetUnion of the merged nodes
	//     connect nodeA to nodeV with a MergeAssertion
	//     connect nodeB to nodeV with a MergeAssertion
	//	   Find all places where nodeA is wired
	//        that includes as values in key-value pairs
	//        that includes as source or target in tuples
	//			MAKE SURE NOT CONNECTING TO THIS MERGE's TUPLE
	//				which means you need the tuple's locator around
	//        replace that locator with the locator from nodeV
	//  Where this gets complex is that a merge detected between a VirtualProxy
	//    might also be detected between it and any or all of its children
	//    which means a lot of time wasted: the merge might already exist.
	//  If nodeA has a VirtualProxy
	//  Then test all other Merge tuples targets for nodeB already merged.
	//////////////////////////////////////////////////
	//Here's the dark side (unsolved issues)
	//The external MergeEngine is going to find ALL possible nodes like this one
	// and, except if a found node is a VirtualNode, all others will be submitted
	// for merge. This routine must be able to know that it is already merged.
	// For that reason, we must force a refetch on every merge, which means we
	// must:
	//  A: purge this node from the database cache
	//  B: refetch it every time.
	///////////////////////////////////////////////////
	// Logic
	//    If both sourceNode and targetNode already have a virtualNode, DONE
	//    If sourceNode does not have a virtualNode but targetNode does, then
	//		merge against that virtualNode
	//    If sourceNode has a virtualNode but targetNode doesn't, then
	//      merge against that virtualNode
	// NOTE:
	//	sanity test: if both have a virtual node and it's not the same node,
	//		well, shit happens!
	// IT's WORSE THAN THAT
	//	CASES:
	//		Node and Node merge
	//		Tuple and Tuple merge
	//  ERROR CASES
	//		Node and Tuple merge
	//			theoretically impossible unless someone screws up a merge agent
	//	FOR NOW, we are ignoring the cases
	///////////////////////////////////////////////////
	//  WHEN a new VirtualNode is made, just save it
	//  WHEN an existing VirtualNode is modified, do an updateNode
	//     Issues of version???
	//		MUST do updateNode so that the database knows what it's dealing with
	//		HUGE ISSUE HERE: you update a virtualNode and it goes back into the
	//		merge maw except that it will be stopped since we do NOT merge on VirtualNodes
	///////////////////////////////////////////////////
	// What happens when you are merging a public and private node?
	///////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IMergeImplementation#assertMerge(java.lang.String, java.lang.String, java.util.Map, double, java.lang.String)
	 * @param sourceNode is a fresh node to merge; it is never a virtualNode since it's based on something new to Solr
	 * @param targetNodeLocator is an existing node in the database; it could be a virtualNode
	 * NOTE: <code>targetNodeLocator</code> might represent a "virgin" node that hasn't even been seen
	 * by the merge engine yet.
	 */
	//TODO reconsider sending in the proxies to save cycles -- concurrency issues might not exist
	@Override
	public IResult assertMerge(INode sourceNode,
			String targetNodeLocator, Map<String, Double> mergeData,
			double mergeConfidence, String userLocator) {
		// since we are not messing with versions, we should be safe -- it says here
		log.logDebug("MergeBean.assertMerge- "+sourceNode.getLocator()+" "+targetNodeLocator);
		IResult result = new ResultPojo();
		//the theory is that TupleMergeAgent will block sending in a mix of Tuple and Node
		boolean sourceIsTuple = sourceNode.isTuple();
		if (sourceIsTuple)
			return result;
		//Nuclear Weapon -- on occasion, the same nodes are sent in.
		if (targetNodeLocator.equals(sourceNode.getLocator()))
			return result;
		//try to fetch a virtualNode for targetNodeLocator
		IResult firstly = database.getVirtualNodeIfExists(targetNodeLocator, credentials); 
		INode theTarget = (INode)firstly.getResultObject();
		if (firstly.hasError())
			result.addErrorString(firstly.getErrorString());

		if (theTarget == null) {
			result.addErrorString("MergeBean missing virtual target: "+targetNodeLocator);
			return result;
		}
		//extremely unlikely
		boolean sourceVnodeExists = sourceNode.getIsVirtualProxy();
		//quite possible
		boolean targetVnodeExists = theTarget.getIsVirtualProxy();
		//start with an assumption
		boolean sourceNodeHasVirtual = sourceVnodeExists;
		//For each subject there will be one and only one virtualNode
		// when two topics are found to merge, they are automatically assumed
		// to share the same virtualNode; no topic can have > 1 virtualNode
		INode virtualNode=null;
		String virtualNodeLocator = null;
		if (!sourceVnodeExists) {
			//this could happen, but seems unlikely
			firstly = database.getVirtualNodeIfExists(sourceNode.getLocator(), credentials);
			if (firstly.hasError())
				result.addErrorString(firstly.getErrorString());
			
			INode temp = (INode)firstly.getResultObject();
			if (temp.getIsVirtualProxy()) {
				//sourceNode has a virtualProxy
				sourceNodeHasVirtual = true;
				virtualNode = temp;
				virtualNodeLocator = temp.getLocator();
			}
		}
		//we never merge two virtual proxies; it's amazing to think how that
		//might happen...
		if ((sourceVnodeExists || sourceNodeHasVirtual)  && targetVnodeExists)
			return result;
		//purge the cache since we are going to perform surgery here
		database.removeFromCache(sourceNode.getLocator());
		database.removeFromCache(theTarget.getLocator());
		if (virtualNodeLocator != null)
			database.removeFromCache(virtualNodeLocator);

		//if targetNodeLocator is different from targetNode.getLocator(), then we fetched the virtual of a given node
		log.logDebug("MergeBean-1 "+sourceNode.getLocator()+" "+sourceVnodeExists+" "+theTarget.getLocator()+" "+targetVnodeExists+" "+targetNodeLocator+" "+virtualNodeLocator);
		String tupleLocator;
		IResult vr = null;
		//There is the distinct case that the merge platform sent in a virtualNode
		//as the target. That makes life easier
		if (virtualNode == null && targetVnodeExists) {
			virtualNode = theTarget;
			virtualNodeLocator = theTarget.getLocator();
		}
		//
		if (virtualNodeLocator == null) {			
			//at this point, it is possible that no virtualNode exists, which
			// means neither source nor target has been merged
			//must create a virtualNode
			String theType = sourceNode.getNodeType();
			List<String>sups = sourceNode.listSuperclassIds();
			String smallImagePath = sourceNode.getSmallImage();
			String largeImagePath = sourceNode.getImage();
			//WARNING: we presently do not test privacy in merge testing
			boolean isPrivate = sourceNode.getIsPrivate();
			IResult vnr = nodeModel.newNode(null, null, "", userLocator, smallImagePath, largeImagePath, isPrivate);
			virtualNode = (INode)vnr.getResultObject();
			virtualNode.setIsVirtualProxy(true);
			if (theType != null)
				virtualNode.setNodeType(theType);
			if (sups != null && !sups.isEmpty()) {
				Iterator<String>itr = sups.iterator();
				while (itr.hasNext())
					virtualNode.addSuperclassId(itr.next());
			}			
			if (vnr.hasError())
				result.addErrorString(vnr.getErrorString());
			List<String>sTups = sourceNode.listTuples();
			if (sTups != null && !sTups.isEmpty()) {
				Iterator<String>itr = sTups.iterator();
				while (itr.hasNext())
					virtualNode.addTuple(itr.next());
			}
			setUnionProperties(virtualNode,sourceNode);
			setUnionProperties(virtualNode,theTarget);
			//this virtual node is going to exist as a saved node when
			//relation wiring happens, so it must be saved now.
			log.logDebug("XXXX "+virtualNode.toXML());
			database.putNode(virtualNode);
			//wiring to source and target nodes means they exist and
			//surgical changes will be made to them.
			log.logDebug("V1 "+virtualNode.getLocator()+" "+sourceNode.getLocator());
			vr = wireMerge(virtualNode, sourceNode, mergeData, mergeConfidence, userLocator);
			if (vr.hasError())
				result.addErrorString(vr.getErrorString());
			tupleLocator = (String)vr.getResultObject();
			log.logDebug("V1a "+vr.getErrorString()+" | "+tupleLocator);
			vr = this.reWireNodeGraph(sourceNode.getLocator(), virtualNode.getLocator(), tupleLocator);
			if (vr.hasError())
				result.addErrorString(vr.getErrorString());
			log.logDebug("V2 "+virtualNode.getLocator()+" "+targetNodeLocator);
			vr = wireMerge(virtualNode, theTarget, mergeData, mergeConfidence, userLocator);
			if (vr.hasError())
				result.addErrorString(vr.getErrorString());
			tupleLocator = (String)vr.getResultObject();
			vr = this.reWireNodeGraph(theTarget.getLocator(), virtualNode.getLocator(), tupleLocator);
			if (vr.hasError())
				result.addErrorString(vr.getErrorString());			
		} else if (!sourceVnodeExists) {
			if (virtualNodeLocator.equals(sourceNode.getLocator()))
				return result;
			if (virtualNode == null) {
				vr = database.getNode(virtualNodeLocator, credentials);
				if (vr.hasError())
					result.addErrorString(vr.getErrorString());
				virtualNode = (INode)vr.getResultObject();
			}
			if (virtualNode.getIsVirtualProxy() && sourceNode.getIsVirtualProxy())
				return result;
			//the case where a virtualNode exists but not by the sourceNode
			//Copy over all labels and details for both nodes
			//this is a surgical change to virtualNode
			if (setUnionProperties(virtualNode,sourceNode)) {
				virtualNode.getProperties().remove(ITopicQuestsOntology.SOLR_VERSION_PROPERTY_TYPE);
				database.updateNode(virtualNode); //TODO possible issues with version numbers here
			}
			//since surgery was
			log.logDebug("V3 "+virtualNode.getLocator()+" "+sourceNode.getLocator());
			vr = wireMerge(virtualNode, sourceNode, mergeData, mergeConfidence, userLocator);
			if (vr.hasError())
				result.addErrorString(vr.getErrorString());
			tupleLocator = (String)vr.getResultObject();
			vr = this.reWireNodeGraph(sourceNode.getLocator(), virtualNode.getLocator(), tupleLocator);
			if (vr.hasError())
				result.addErrorString(vr.getErrorString());
			
		} else {
			if (virtualNodeLocator.equals(theTarget.getLocator()))
				return result;
			if (virtualNode == null) {
				vr = database.getNode(virtualNodeLocator, credentials);
				if (vr.hasError())
					result.addErrorString(vr.getErrorString());
				virtualNode = (INode)vr.getResultObject();
			}
			if (virtualNode.getIsVirtualProxy() && theTarget.getIsVirtualProxy())
				return result;
			//the case where a virtualNode exists but not by the targetNode
			//Copy over all labels and details for both nodes
			if (setUnionProperties(virtualNode,theTarget)) {
				virtualNode.getProperties().remove(ITopicQuestsOntology.SOLR_VERSION_PROPERTY_TYPE);
				database.updateNode(virtualNode);
			}
			log.logDebug("V4 "+virtualNode.getLocator()+" "+theTarget.getLocator()+" "+mergeData);

			vr = wireMerge(virtualNode, theTarget, mergeData, mergeConfidence, userLocator);
			if (vr.hasError())
				result.addErrorString(vr.getErrorString());
			tupleLocator = (String)vr.getResultObject();
			vr = this.reWireNodeGraph(theTarget.getLocator(), virtualNode.getLocator(), tupleLocator);
			if (vr.hasError())
				result.addErrorString(vr.getErrorString());			
		}
		log.logDebug("MergeBean-2 "+result.getErrorString()+" | "+virtualNode.getLocator());
		return result;
	}
	
	/**
	 * <p>Perform a <em>Set Union</em> on various key/value pairs</p>
	 * @param virtualNode
	 * @param mergedNode
	 * @param isTuple
	 * @return
	 */
	boolean setUnionProperties(INode virtualNode, INode mergedNode) {
		boolean result = false;
				//Copy over all labels and details for both nodes
			//	installLabelsAndDetails(virtualNode,mergedNode);
		//grab unrestricted tuples first
		log.logDebug("MergeBean.setUnionProperties- "+virtualNode.getLocator()+" "+mergedNode.getLocator());
		//other properties -- doing surgery on the map
		//note: this will pick up all the labels and details in all languages
		Map<String,Object>sourceMap = mergedNode.getProperties();
		Map<String,Object>virtMap = virtualNode.getProperties();
		Iterator<String>keys = sourceMap.keySet().iterator();
		String key;
		Object os;
		Object ov;
		List<String>sxx;
		List<String>vxx;
		while (keys.hasNext()) {
			key = keys.next();
			log.logDebug("MergeBean.setUnionProperties-1 "+key);
			if (okToUse(key)) {
				os = sourceMap.get(key);
				ov = virtMap.get(key);
				log.logDebug("MergeBean.setUnionProperties-2 "+key+" "+ov+" "+os);
				if (os instanceof String ||
					os instanceof Date ||
					os instanceof Long ||
					os instanceof Double ||
					os instanceof Integer ||
					os instanceof Float ||
					os instanceof Boolean) {
					if (ov == null) {
						virtMap.put(key, os);
						result = true;
					} else {
						//TODO what to do if they are not equal?
					}
				} else {
					sxx = (List<String>)os;
					if (ov == null)
						vxx = new ArrayList<String>();
					else
						vxx = (List<String>)ov;
					int len = sxx.size();
					for (int i=0;i<len;i++) {
						if (!vxx.contains(sxx.get(i))) {
							vxx.add(sxx.get(i));
							result = true;
						}
					}
					virtMap.put(key, vxx);
					log.logDebug("MergeBean.setUnionProperties-3 "+key+" "+vxx);
				}
			}
		}
		return result;
	}
	
	/**
	 * Filter out certain keys
	 * @param key
	 * @return
	 */
	boolean okToUse(String key) {
		if (key.equals(ITopicQuestsOntology.LOCATOR_PROPERTY) ||
			key.equals(ITopicQuestsOntology.CREATED_DATE_PROPERTY) ||
			key.equals(ITopicQuestsOntology.LAST_EDIT_DATE_PROPERTY) ||
			key.equals(ITopicQuestsOntology.SOLR_VERSION_PROPERTY_TYPE)||
			key.equals(ITopicQuestsOntology.CREATOR_ID_PROPERTY) ||
			key.equals(ITopicQuestsOntology.IS_PRIVATE_PROPERTY))
			return false;
		return true;
	}

	/**
	 * 
	 * @param o
	 * @return can return <code>null</code>
	 */
	List<String> makeListIfNeeded(Object o) {
		List<String>result = null;
		if (o == null)
			result = new ArrayList<String>();
		else if (o instanceof List)
			result = (List<String>)o;
		else {
			result = new ArrayList<String>();
			result.add((String)o);
		}
		return result;
	}
	/////////////////////////////////////////////////////////////////
	// Rewiring the graph
	// CASE: Merged nodes
	// 	A Node was merged with another node.
	// 	A VirtualNode was created
	// 	For every place in the graph where either node is referenced
	//		surgically replace that node locator with the virtual node locator
	// CASE: Merged tuples
	//  A Tuple has merged with another tuple
	//		NOTE: these are NOT Merge-related tuples, only knowledge graph tuples
	//  For every node which references a merged tuple in its tuple field
	//    	surgically replace that tuple locator with the new tuple locator
	//        	this entails overwriting a tuple *in place* in a list of tuples
	//          and doing an update on that list in the database
	/////////////////////////////////////////////////////////////////
	// Emergent Issue
	//	If a new node is saved and it is immediately related to another node
	//		it will be in a merge process while the relation is occuring.
	//		THIS means that there MIGHT be NO TUPLE available to be captured
	//		during the merge. Thus, the VirtualNode MIGHT end up with no tuple
	//////////////////////////////////////////////////////////////////
	/**
	 * Substitute <code>virtualProxyLocator</code> for all hits of <code>mergedProxyLocator</code>
	 * @param mergedProxyLocator
	 * @param virtualProxyLocator
	 * @return
	 */
	IResult reWireNodeGraph(String mergedProxyLocator, String virtualProxyLocator, String mergeTupleLocator) {
		//this really must deal with tuples first
		//TODO sort out what other propertyTypes entail symbolic links, then
		//chase those
		log.logDebug("MergeBean.reWireNodeGraph- "+mergedProxyLocator+" "+virtualProxyLocator+" "+mergeTupleLocator);
		IResult result = new ResultPojo();
		//Find all tuples where mergedProxyLocator isA subject and fix them
		IResult xx = tupleQuery.listTuplesBySubject(mergedProxyLocator, credentials);
		if (xx.hasError())
			result.addErrorString(xx.getErrorString());
		List<INode>n = (List<INode>)xx.getResultObject();
		ITuple t;
		Iterator<INode>itr;
		IResult surgR;
		if (n != null && !n.isEmpty()) {
			//time for surgery
			itr = n.iterator();
			while (itr.hasNext()) {
				t = (ITuple)itr.next();
				if (!t.getLocator().equals(mergeTupleLocator)) {
					log.logDebug("MergeBean.reWireGraph-1 "+t.getLocator());
					surgR = performTupleSurgery(t,virtualProxyLocator,true);
					if (surgR.hasError())
						result.addErrorString(surgR.getErrorString());
					//notice the distinct possibility that no surgery got performed
					//and the topic map will have errors
				}
			}
		}
		//Find all tuples where mergedProxyLocator isA object and fix them
		xx = tupleQuery.listTuplesByObjectLocator(mergedProxyLocator, credentials);
		if (xx.hasError())
			result.addErrorString(xx.getErrorString());
		n = (List<INode>)xx.getResultObject();
		if (n != null && !n.isEmpty()) {
			//time for surgery
			itr = n.iterator();
			while (itr.hasNext()) {
				t = (ITuple)itr.next();
				if (!t.getLocator().equals(mergeTupleLocator)) {
					log.logDebug("MergeBean.reWireGraph-2 "+t.getLocator());
					surgR = performTupleSurgery(t,virtualProxyLocator,false);
					if (surgR.hasError())
						result.addErrorString(surgR.getErrorString());
					//notice the distinct possibility that no surgery got performed
					//and the topic map will have errors
				}
			}
		}
		return result;
	}

	/**
	 * <p>This is supposed to perform surgery on {@link ITuple} objects only.</p>
	 * @param t
	 * @param newLocator
	 * @param isSubject
	 * @return
	 */
	IResult performTupleSurgery(ITuple t, String newLocator, boolean isSubject) {
		String key = ITopicQuestsOntology.TUPLE_SUBJECT_PROPERTY;
		if (!isSubject)
			key = ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY;
		//We are performing surgery on a tuple which might have already had
		//surgery earlier, which means the version number will be out of date.
		//Two options: remove the version number, or refetch the tuple before
		// doing this surgery
		t.getProperties().remove(ITopicQuestsOntology.SOLR_VERSION_PROPERTY_TYPE);
		IResult result = nodeModel.changePropertyValue(t, key, newLocator);
		
		return result;
	}
	
	/**
	 * Wire up a MergeAssertion
	 * @param virtualProxy
	 * @param targetProxy
	 * @param mergeData key = reason, value = vote
	 * @param mergeConfidence not used at the moment
	 * @param userLocator
	 * @return tupleLocator is included
	 */
	IResult wireMerge(INode virtualProxy, INode targetProxy, Map<String, Double> mergeData,
			double mergeConfidence, String userLocator) {
		//force relation engine to re-fetch the nodes for accurate surgery
		//changed my mind
		//TODO rethink this algorithm; it's a lot of node fetches
		IResult result = relateNodes(virtualProxy, targetProxy, 
				 userLocator, ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, 
				 false, targetProxy.getIsPrivate(), mergeData);
		// result contains the tuple's locator
		// use that to fetch it and wire up scopes from mergeData
		return result;
	}

	@Override
	public void setNodeModel(INodeModel m) {
		this.nodeModel = m;
	}

	/**
	 * Forge a merge relation--not using the version in INodeModel
	 * @param sourceNode
	 * @param targetNode
	 * @param userId
	 * @param smallImagePath
	 * @param largeImagePath
	 * @param isTransclude
	 * @param isPrivate
	 * @param mergeData
	 * @return
	 */
	private IResult relateNodes(INode sourceNode, INode targetNode,
			String userId, String smallImagePath,
			String largeImagePath, boolean isTransclude, boolean isPrivate, Map<String, Double> mergeData) {
		String relationTypeLocator = ITopicQuestsOntology.MERGE_ASSERTION_TYPE;
		database.removeFromCache(sourceNode.getLocator());
		database.removeFromCache(targetNode.getLocator());
		IResult result = new ResultPojo();
		//NOTE that we make the tuple an instance of the relation type, not of TUPLE_TYPE
		ITuple t = (ITuple)nodeModel.newInstanceNode(relationTypeLocator, relationTypeLocator, 
				sourceNode.getLocator()+" "+relationTypeLocator+" "+targetNode.getLocator(), "en", userId, smallImagePath, largeImagePath, isPrivate).getResultObject();
		t.setIsTransclude(isTransclude);
		t.setObject(targetNode.getLocator());
		t.setObjectType(ITopicQuestsOntology.NODE_TYPE);
		t.setSubjectLocator(sourceNode.getLocator());
		t.setSubjectType(ITopicQuestsOntology.NODE_TYPE);
		Iterator<String>itx = mergeData.keySet().iterator();
		String reason;
		while (itx.hasNext()) {
			reason = itx.next();
			t.addMergeReason(reason+" "+mergeData.get(reason));
		}
		IResult x = database.putNode(t);
		log.logDebug("MergeBean.relateNodes "+sourceNode.getLocator()+" "+targetNode.getLocator()+" "+t.getLocator());
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		String tLoc = t.getLocator();
		//save the tuple's locator in the output
		result.setResultObject(tLoc);
		sourceNode.setMergeTupleLocator(tLoc);
		IResult vr = changePropertyValue(sourceNode,ITopicQuestsOntology.MERGE_TUPLE_PROPERTY,tLoc);
		if (vr.hasError())
			result.addErrorString(vr.getErrorString());
		targetNode.setMergeTupleLocator(tLoc);
		vr = changePropertyValue(targetNode,ITopicQuestsOntology.MERGE_TUPLE_PROPERTY,tLoc);
			if (vr.hasError())
				result.addErrorString(vr.getErrorString());
		log.logDebug("MergeBean.relateNodes+ "+result.getErrorString());
		return result;
	}
	
	/**
	 * Surgically change a property value
	 * @param node
	 * @param key
	 * @param newValue
	 * @return
	 */
	IResult changePropertyValue(INode node, String key, String newValue) {

		String sourceNodeLocator = node.getLocator();
		Map<String,Object> updateMap = new HashMap<String,Object>();
		Map<String,String> newMap = new HashMap<String,String>();
		updateMap.put(ITopicQuestsOntology.LOCATOR_PROPERTY, sourceNodeLocator);
		newMap.put("set",newValue);
		updateMap.put(key, newMap);
		IResult result = database.partialUpdateData(updateMap);;
		database.removeFromCache(sourceNodeLocator);
		return result;
	}

}