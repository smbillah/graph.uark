package graph.uark.rest.graphdb.models.constants;

import org.neo4j.graphdb.RelationshipType;

public enum REL_TYPE implements RelationshipType {
	coauthor("coauthor"),		
	interest("interest"),
	cites("cites"),
	cited_by("cited_by");
	
	private String type;
	private REL_TYPE(String type){
		this.type= type;
	}		
}


