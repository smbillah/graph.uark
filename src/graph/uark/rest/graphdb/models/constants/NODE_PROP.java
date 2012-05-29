package graph.uark.rest.graphdb.models.constants;

public enum NODE_PROP {
	
	fname("fname"),mname("mname"), lname("lname"), num_pubs("num_pubs"), citeseer_id("citeseer_id"),
	title("title"), interest("interest");
	
	
	private String prop;
	private NODE_PROP(String prop){
		this.prop = prop;
	}
}
