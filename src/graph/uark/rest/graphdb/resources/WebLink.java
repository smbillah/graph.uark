package graph.uark.rest.graphdb.resources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WebLink {
	private int source;
	private int target;
	private int value;
	
	public WebLink(){
		
	}
	
	public WebLink(WebNode source, WebNode target, int value){		
		this.setSource(source.getWeb_id());
		this.setTarget(target.getWeb_id());
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}
}
