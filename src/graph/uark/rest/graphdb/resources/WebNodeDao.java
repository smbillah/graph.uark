package graph.uark.rest.graphdb.resources;

import graph.uark.rest.graphdb.models.constants.NODE_PROP;
import graph.uark.rest.graphdb.models.constants.NODE_TYPE;
import graph.uark.rest.graphdb.models.constants.REL_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.entity.RestRelationship;
import org.neo4j.rest.graphdb.index.RestIndex;
import org.neo4j.rest.graphdb.index.RestNodeIndex;
import org.neo4j.rest.graphdb.traversal.RestTraversal;

public enum WebNodeDao {
	instances;
	private NODE_PROP PROP;
	private NODE_TYPE TYPE;
	private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";

	RestAPI restAPI;
	RestNodeIndex restIndex;
	RestRelationship relations;

	private WebNodeDao() {
		restAPI = new RestAPI(SERVER_ROOT_URI);
		relations = new RestRelationship(SERVER_ROOT_URI, restAPI);
	}

	public RestAPI getDB() {
		return restAPI;
	}

	public WebNode getWebNode(String str_id) {
		long id = 0;
		try {
			id = Long.parseLong(str_id);
			RestNode restNode = restAPI.getNodeById(id);
			WebNode node = new WebNode(restNode);
			return node;
		} catch (Exception ex) {
			return null;
		}
	}

	public WebNode getWebNode(long id) {
		try {
			RestNode restNode = restAPI.getNodeById(id);
			WebNode node = new WebNode(restNode);
			return node;
		} catch (Exception ex) {
			return null;
		}
	}

	public List<WebNode> getWebNodesFromQuery(String query) {
		ArrayList<WebNode> results = new ArrayList<WebNode>();
		try {
			RestIndex<RestNode> authors = restAPI.getIndex("authors");
			int count=0; int maxCount= 7;

			//add authors that match in first name
			for (RestNode author : authors.query("fname", query)) {
				if (count>= maxCount){
					count=0;
					break;
				}
				results.add(new WebNode(author));
				count++;
			}
			
			//add authors that match in last name 
			for (RestNode author : authors.query("lname", query)) {
				if (count>=maxCount){
					count=0;
					break;
				}
				results.add(new WebNode(author));
				count++;
			}
		} catch (Exception ex) {

		}
		if (results.size() == 0) {
			WebNode empty = new WebNode(-1);
			empty.setName("No result available");
			results.add(empty);
		}

		return results;
	}

	private WebGraph expandNetwork(WebGraph graph, RestNode source, int depth) {
		if (depth <= 0)
			return graph;

		final TraversalDescription traversalDescription = RestTraversal
				.description().maxDepth(1).breadthFirst()
				.relationships(REL_TYPE.coauthor, Direction.OUTGOING);

		final Traverser traverser = traversalDescription.traverse(source);
		final Iterable<Node> nodes = traverser.nodes();

		for (Node node : nodes) {
			RestNode n = (RestNode) node;
			graph.addNode(n);
			graph.addLink(source, n);
		}

		for (Node node : nodes) {
			RestNode n = (RestNode) node;
			expandNetwork(graph, n, depth - 1);
		}
		return graph;

	}

	public WebGraph getCoAuthor(String str_id) {
		long id = 0;
		WebGraph graph;

		try {
			id = Long.parseLong(str_id);
			RestNode restNode = restAPI.getNodeById(id);
			graph = new WebGraph(restNode);
			graph = expandNetwork(graph, restNode, 3);
			return graph;

		} catch (Exception ex) {
			return null;
		}
	}

	public WebNode removeNode(String str_id) {
		long id = 0;
		try {
			id = Long.parseLong(str_id);
			RestNode restNode = restAPI.getNodeById(id);

			WebNode node = new WebNode(restNode);
			restNode.delete();
			return node;
		} catch (Exception ex) {
			return null;
		}
	}

	public WebNode createAuthor(String fname, String mname, String lname,
			String num_pubs, String citeseer_id, String indexName) {
		Map<String, Object> props = new HashMap<String, Object>();

		props.put(NODE_PROP.fname+"", fname.toLowerCase());
		props.put(NODE_PROP.mname+"", mname.toLowerCase());
		props.put(NODE_PROP.lname+"", lname.toLowerCase());
		props.put(NODE_PROP.citeseer_id+"", citeseer_id);
		props.put(NODE_PROP.num_pubs+"", num_pubs);

		try {
			RestNode rest_node = restAPI.createNode(props);

			RestIndex<RestNode> index = restAPI.getIndex(indexName);

			restAPI.addToIndex(rest_node, index, NODE_PROP.fname+"", fname.toLowerCase());
			restAPI.addToIndex(rest_node, index, NODE_PROP.mname+"", mname.toLowerCase());
			restAPI.addToIndex(rest_node, index, NODE_PROP.lname+"", lname.toLowerCase());

			return new WebNode(rest_node, NODE_TYPE.AUTHOR);
		} catch (Exception ex) {
			return null;
		}

	}
	
	public WebNode createInterest(String interest, String indexName) {
		Map<String, Object> props = new HashMap<String, Object>();

		props.put(NODE_PROP.interest+"", interest.toLowerCase());
		
		try {
			RestNode rest_node = restAPI.createNode(props);
			RestIndex<RestNode> index = restAPI.getIndex(indexName);
			restAPI.addToIndex(rest_node, index, NODE_PROP.interest+"", interest.toLowerCase());
			

			return new WebNode(rest_node, NODE_TYPE.INTEREST);
		} catch (Exception ex) {
			return null;
		}

	}
	
	public WebNode createPaper(String title, String indexName) {
		Map<String, Object> props = new HashMap<String, Object>();

		props.put(NODE_PROP.title+"", title.toLowerCase());		

		try {
			RestNode rest_node = restAPI.createNode(props);
			RestIndex<RestNode> index = restAPI.getIndex(indexName);
			restAPI.addToIndex(rest_node, index, NODE_PROP.title+"", title.toLowerCase());
			
			return new WebNode(rest_node, NODE_TYPE.PAPER);
		} catch (Exception ex) {
			return null;
		}

	}



	public void createNode(WebNode node) {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("id", node.getId());
		props.put("name", node.getName());
		props.put("citeseer_id", node.getCiteseer_id());
		restAPI.createNode(props);

	}

	public boolean isExists(long id) {
		return getWebNode(id) != null ? true : false;
	}

	public static void main(String args[]) {
		String id = "4";
		// WebGraph g= WebNodeDao.instances.getCoAuthor(id);
		// WebNodeDao.instances.createNode("siddika", "authors");
		List<WebNode> auths = WebNodeDao.instances
				.getWebNodesFromQuery("Sirajul Islam");
		System.out.println("Happy with this so far" + auths.get(0).getName());

	}
}
