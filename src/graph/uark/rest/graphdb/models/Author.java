package graph.uark.rest.graphdb.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Author {
	private String name;
	private String node_id; //node id for graph db
	private String citeseer_id; //auth_id from citeseer db 
	
	private List<String> publications;
	private List<String> interest;
	
	public Author(String name){
		this.name = name;
		this.interest = new ArrayList<String>();
	}
	
	public Author(String name, List<String> interest){
		this.name = name;
		this.interest = interest;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getInterest() {
		return interest;
	}
	public void setInterest(List<String> interest) {
		this.interest = interest;
	}

}
