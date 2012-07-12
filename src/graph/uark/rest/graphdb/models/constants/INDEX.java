package graph.uark.rest.graphdb.models.constants;

public enum INDEX {

	authors("authors"), interests("interests"), papers("papers");
	
	private String prop;
	private INDEX(String prop){
		this.prop = prop;
	}
}
