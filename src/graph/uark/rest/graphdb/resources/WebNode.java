package graph.uark.rest.graphdb.resources;

import graph.uark.rest.graphdb.models.constants.NODE_PROP;
import graph.uark.rest.graphdb.models.constants.NODE_TYPE;

import javax.xml.bind.annotation.XmlRootElement;

import org.neo4j.rest.graphdb.entity.RestNode;

import com.sun.jersey.api.uri.UriComponent.Type;


@XmlRootElement
public class WebNode {
	private long id;
	private String name;
	private int citeseer_id;
	private int web_id;
	private int num_pubs;
	
	private NODE_TYPE TYPE; 	
	
	public WebNode(){
		
	}

	public WebNode(long id, int web_id, String name) {
		this.id = id;		
		this.setName(name);		
		this.setNum_pubs(0);
		
	}
	public WebNode(long id) {
		this.id = id;								
	}
	
	public WebNode(RestNode node) {
		this(node, NODE_TYPE.AUTHOR);
	}
	
	public WebNode(RestNode node, NODE_TYPE type) {
		this(node.getId());
		String name="";
								
		switch(type){
		case AUTHOR:			
			name= ""+node.getProperty(""+NODE_PROP.fname, "")+" "+node.getProperty(""+NODE_PROP.mname, "")+
	 				" "+node.getProperty(""+NODE_PROP.lname, "");
			break;
		
		case INTEREST:
			name= ""+node.getProperty(""+NODE_PROP.interest, "");
			break;
		
		case PAPER:
			name= ""+node.getProperty(""+NODE_PROP.title, "");
			break;			
		}
		
		
		setName(name);
		try{
			 int num_pubs= Integer.parseInt(node.getProperty(NODE_PROP.num_pubs.toString(), "0").toString());
			 setNum_pubs(num_pubs);
		}catch(Exception ex){
			setNum_pubs(-1);
		}
		setCiteseer_id(-1);
		setWeb_id(0);
	}
	
	public WebNode(RestNode node, int web_index){
		this(node);
		this.setWeb_id(web_index); 	
	}
	
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public NODE_TYPE getType() {
		return TYPE;
	}

	public void setType(NODE_TYPE type) {
		this.TYPE = type;
	}

	public int getCiteseer_id() {
		return citeseer_id;
	}

	public void setCiteseer_id(int citeseer_id) {
		this.citeseer_id = citeseer_id;
	}

	public int getWeb_id() {
		return web_id;
	}

	public void setWeb_id(int web_id) {
		this.web_id = web_id;
	}

	public int getNum_pubs() {
		return num_pubs;
	}

	public void setNum_pubs(int num_pubs) {
		this.num_pubs = num_pubs;
	}

	
		
}