package graph.uark.rest.graphdb.resources;

import graph.uark.rest.graphdb.models.constants.NODE_PROP;

import javax.xml.bind.annotation.XmlRootElement;

import org.neo4j.rest.graphdb.entity.RestNode;

@XmlRootElement
public class Author {

	private String fname, lname, mname;
	private int num_pubs, citeseer_id;

	public Author() {
		setFname("");
		setMname("");
		setLname("");

		setNum_pubs(0);
		setCiteseer_id(0);

	}

	public Author(RestNode node) {
		this();
		setFname("" + node.getProperty("" + NODE_PROP.fname, ""));
		setMname(" " + node.getProperty("" + NODE_PROP.mname, ""));
		setLname(" " + node.getProperty("" + NODE_PROP.lname, ""));
		
		try{
			 setNum_pubs(Integer.parseInt(""+node.getProperty(""+NODE_PROP.num_pubs, "0")));
			 setCiteseer_id(Integer.parseInt(""+node.getProperty(""+NODE_PROP.citeseer_id, "0")));
		}
		catch(Exception ex){
			setNum_pubs(-1);
			setCiteseer_id(-1);
		} 
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public int getCiteseer_id() {
		return citeseer_id;
	}

	public void setCiteseer_id(int citeseer_id) {
		this.citeseer_id = citeseer_id;
	}

	public int getNum_pubs() {
		return num_pubs;
	}

	public void setNum_pubs(int num_pubs) {
		this.num_pubs = num_pubs;
	}
}
