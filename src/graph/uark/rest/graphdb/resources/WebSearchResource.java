package graph.uark.rest.graphdb.resources;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WebSearchResource {

	private List<WebNode> results;
	public WebSearchResource() {}
	
//	GenericEntity<Collection<>>
	public WebSearchResource(List<WebNode> results) {		
		this.setResults(results);
	}

	@XmlElement(name="authors")
	public List<WebNode> getResults() {
		return results;
	}

	public void setResults(List<WebNode> results) {
		this.results = results;
	}

	
	
}
