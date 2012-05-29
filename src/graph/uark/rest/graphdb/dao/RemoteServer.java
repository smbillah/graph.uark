/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.uark.rest.graphdb.dao;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.rest.graphdb.ExecutingRestRequest;
import org.neo4j.rest.graphdb.GraphDatabaseFactory;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.batch.RecordingRestRequest;
import org.neo4j.rest.graphdb.batch.RestOperations;
import org.neo4j.rest.graphdb.traversal.RestTraversal;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * 
 * @author Masum
 */
public class RemoteServer {
	private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";
	private static GraphDatabaseService neo4jdb;
	
	private static ExecutingRestRequest executingRequest = new ExecutingRestRequest(SERVER_ROOT_URI);
	private static RecordingRestRequest recordingRequest = new RecordingRestRequest(executingRequest, new RestOperations());
	

	// make it singleton class
	private RemoteServer() {
		       
	}

	public static int checkStatus() {
		WebResource resource = Client.create().resource(SERVER_ROOT_URI);
		ClientResponse response = resource.get(ClientResponse.class);

		System.out.println(String.format("GET on [%s], status code [%d]",
				SERVER_ROOT_URI, response.getStatus()));

		response.close();
		return response.getStatus();
	}

	// first get the database
	public static GraphDatabaseService getDB() {
		try {
			int status = checkStatus();
			if (200 == status) {
				if (neo4jdb == null)
					neo4jdb = GraphDatabaseFactory.databaseFor(SERVER_ROOT_URI);
				System.out.println("database is running");
				return neo4jdb;
			}

		} catch (Exception ex) {
			System.out.println("An error occurs. check the Server status...");
		}
		System.out.println("An error occurs. check the Server status...");
		return null;

	}

	public static void testGetWithData() {
		;		
	}

	// testing goes here
	public static void main(String[] args) {
		GraphDatabaseService db = getDB();
		if (db == null)
			return;
		//create some node
		RestAPI rest =new RestAPI(SERVER_ROOT_URI);
		Map<String, Object> properties = new HashMap<String, Object>();
		Node node1= rest.getNodeById(1);
		
		
		
		
//		System.out.println(gson.toJson(node1.));
//		Relationship rel= node1.getSingleRelationship(REL_TYPE.SON,Direction.OUTGOING);
//		System.out.println(rel.getEndNode().getProperty("name"));
		
//		rel.delete();
//		properties.put("name", "Farhani Momo");
		
//		WebNode node = rest.createNode(properties);
//		System.out.println("New node is created :D "+node.getId());
//		properties.put("name", "Syed Masum Billah");
//		neo4jdb.createNode()
		
//		WebNode node = neo4jdb.getNodeById(0);
		// node.relationship
//		System.out.println(node.hasProperty("name"));
		// checkStatus();

//		for (Relationship r : node.getRelationships()) {
//			System.out.println(r.getStartNode().getId() + " : "
//					+ r.getEndNode().getId());
//			for (String prop : r.getPropertyKeys())
//				System.out.println(prop);
//
//			System.out.println(r.isType(RelType.COAUTHOR));
//		}

//		final Relationship rel = node1.getRelationships(REL_TYPE.COAUTHOR).iterator().next();
		final TraversalDescription traversalDescription = RestTraversal.description().maxDepth(10).breadthFirst();
//		System.out.println("traversalDescription = " + traversalDescription);
		final Traverser traverser = traversalDescription.traverse(node1);
		final Iterable<Node> nodes = traverser.nodes();
		
		for (Node n : nodes) {
			System.out.println(n.getId() + " "+n.getProperty("name"));
		}
		Index<Node> n=db.index().forNodes("index_node");
		 IndexHits<Node> x= n.query("hiep");
		 Node xx= x.getSingle();
		 long l =xx.getId();
		
		
		System.out.print( "index: " + db.index().existsForNodes("index_node"));
		
		
//		RequestResult response = recordingRequest.post("/node", 0);
//		System.out.print(response.getBatchId()+ " "+recordingRequest.getRecordedRequests().size());
		
		

	}
}
