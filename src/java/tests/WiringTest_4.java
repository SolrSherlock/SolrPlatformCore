/**
 * 
 */
package tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

import org.apache.solr.schema.DateField;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.model.api.INode;
import org.topicquests.model.api.INodeModel;
import org.topicquests.solr.Node;
import org.topicquests.solr.SolrEnvironment;
import org.topicquests.solr.SolrDataProvider;
import org.topicquests.solr.SolrNodeModel;
import org.topicquests.solr.api.ISolrDataProvider;
import org.topicquests.solr.api.ISolrQueryModel;
import org.topicquests.solr.api.ISolrQueryIterator;

/**
 * @author park
 * This one to mess with different dates to see how Solr deals with them
 */
public class WiringTest_4 {
	private SolrEnvironment environment;
	private ISolrDataProvider solr;
	private INodeModel model;
	private ISolrQueryModel solrModel;

	/**
	 * 
	 */
	public WiringTest_4() {
		try {
			//create an environment without a desktop window
			environment = new SolrEnvironment();
			//grab the solr database
			solr = new SolrDataProvider(environment, 1024);
			//grab the node model
			model = solr.getNodeModel();
			solrModel = environment.getSolrModel();
			runTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void runTest() throws Exception {
		System.out.println("Starting Test");
		String loc1 = "MyFirstNode"+System.currentTimeMillis();
		String loc2 = "MySecondNode"+System.currentTimeMillis();
		Set<String>credentials = new HashSet<String>();
		credentials.add("admin");
		Date d = new Date();

		//build two nodes
		IResult x = new ResultPojo(); //model.newInstanceNode( loc1,"Class1Type","My First Node", "Something to use as a source node", "en", "admin", null, null, false);
		INode n1 = makeNode(loc1,"Class1Type",d,"MyFirstNode","<Something to use as a source node>");
		IResult xx = solr.putNode(n1);
		if (xx.hasError())
			x.addErrorString(xx.getErrorString());
	//	IResult y = model.newInstanceNode( loc2,"Class2Type","My Second Node", "Something to use as a target node", "en", "admin", null, null, false); 
	// = HandyTools.toDate(D3);
		INode n2 = makeNode(loc2,"Class2Type",d,"My Second Node","<note>here & there</note>");
		xx = solr.putNode(n2);
		if (xx.hasError())
			x.addErrorString(xx.getErrorString());
		//relate them unrestricted
		IResult z = model.relateNodes(loc1, loc2, "TestRelationType", "admin", null, null, false, false);
		if (z.hasError())
			x.addErrorString(z.getErrorString());
		//		if (x.hasError())
//			z.addErrorString(x.getErrorString());
//		if (y.hasError())
//			z.addErrorString(y.getErrorString());
		//relate them again, restricted
		z = model.relateNodes(loc1, loc2, "SecondTestRelationType", "admin", null, null, false, true);
		if (z.hasError())
			x.addErrorString(z.getErrorString());
//		if (x.hasError())
//			z.addErrorString(x.getErrorString());
//		if (y.hasError())
//			z.addErrorString(y.getErrorString());		
		//export the graph starting with the first node
/*		File f = new File("EX"+System.currentTimeMillis()+".xml");
		System.out.println("Exporting "+f);
		FileOutputStream fos = new FileOutputStream(f);
		PrintWriter out = new PrintWriter(fos);
		IResult result = 	solr.exportXmlTreeFile(loc1, out, credentials);
		if (z.hasError())
			result.addErrorString(z.getErrorString());
		
		
		System.out.println("Ending Test "+result.getErrorString());
		*/
		System.out.println("Starting relation iterator");
		ISolrQueryIterator itrx = solrModel.listTuplesByRelation("TestRelationType", 10, credentials);
		IResult rx = itrx.next();
		System.out.println("RelatioIterator "+rx.hasError()+" "+rx.getResultObject());
		itrx.reset();
		itrx = solrModel.listTuplesByRelation("SecondTestRelationType", 10, credentials);
		rx = itrx.next();
		System.out.println("RelatioIterator-2 "+rx.hasError()+" "+rx.getResultObject());
	}
	
	INode makeNode(String loc, String sup, Date d, String label, String details ) {
		INode result = new Node();
		result.setLocator(loc);
		result.setCreatorId("admin");
		result.setDate(d);
		result.setLastEditDate(d);
		result.setIsPrivate(false);
		result.setNodeType(sup);
		result.addDetails(details, "en", "admin", false);
		result.addLabel(label, "en", "admin", false);
		return result;
	}
}
