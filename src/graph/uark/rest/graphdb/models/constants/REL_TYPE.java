package graph.uark.rest.graphdb.models.constants;

import org.neo4j.graphdb.RelationshipType;

public enum REL_TYPE implements RelationshipType {
	coauthor("coauthor"),		
	interest("interest");
	
	private String type;
	private REL_TYPE(String type){
		this.type= type;
	}		
}


