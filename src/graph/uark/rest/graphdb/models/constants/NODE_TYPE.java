package graph.uark.rest.graphdb.models.constants;

public enum NODE_TYPE {
	AUTHOR("AUTHOR"),
	PAPER("PAPER"),
	INTEREST("INTEREST"),
	VENUE("VENUE");
	
	private NODE_TYPE(String value){
		this.value= value;
	}
	private String value;
}
