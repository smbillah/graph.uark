package graph.uark.rest.graphdb.resources;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.neo4j.rest.graphdb.entity.RestNode;

@XmlRootElement
public class WebGraph {
	
	private LinkedHashMap<Long, WebNode> nodes;	
	private List<WebLink> links;
	private int max_connection;
	
	public WebGraph(){}
	
	public WebGraph(RestNode start){
		nodes = new LinkedHashMap<Long, WebNode>();
		links= new ArrayList<WebLink>();
		max_connection=1;
		addNode(start);			
	}
	
	public boolean addNode(RestNode node){
		if(!nodes.containsKey(node.getId())){
			WebNode w = new WebNode(node);
			w.setWeb_id(nodes.size());
			nodes.put(w.getId(), w);
			return true;
		}
		return false;
		
	}
	
	public void addLink(RestNode source, RestNode target, int value){
		WebLink wl= new WebLink(nodes.get(source.getId()), nodes.get(target.getId()), value);
		links.add(wl);
		
		if(value>max_connection)
			max_connection=value;
	}
	
	@XmlElement(name="nodes")
	public List<WebNode> getNodes(){
		return (new ArrayList<WebNode>(nodes.values()));
	}
	
	@XmlElement(name="links")
	public List<WebLink> getLink(){
		return links;
	}

	@XmlElement(name="max_connection")
	public int getMax_connection() {
		return max_connection;
	}

	public void setMax_connection(int max_connection) {
		this.max_connection = max_connection;
	}
	
}
