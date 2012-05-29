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
	
	public WebGraph(){}
	
	public WebGraph(RestNode start){
		nodes = new LinkedHashMap<Long, WebNode>();
		links= new ArrayList<WebLink>();
		
		addNode(start);			
	}
	
	public void addNode(RestNode node){
		if(!nodes.containsKey(node.getId())){
			WebNode w = new WebNode(node);
			w.setWeb_id(nodes.size());
			nodes.put(w.getId(), w);
		}			
		
	}
	
	public void addLink(RestNode source, RestNode target){
		WebLink wl= new WebLink(nodes.get(source.getId()), nodes.get(target.getId()), 1);
		links.add(wl);
		
	}
	
	@XmlElement(name="nodes")
	public List<WebNode> getNodes(){
		return (new ArrayList<WebNode>(nodes.values()));
	}
	
	@XmlElement(name="links")
	public List<WebLink> getLink(){
		return links;
	}
	
}
