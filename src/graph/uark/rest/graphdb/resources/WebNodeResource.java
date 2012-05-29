package graph.uark.rest.graphdb.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

public class WebNodeResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;

	public WebNodeResource(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}

	// Application integration
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public WebNode getNode() {
		WebNode node = WebNodeDao.instances.getWebNode(id);
		
		if (node == null)
			throw new RuntimeException("Get: WebNode with " + id + " not found");
		return node;
	}
	
	// 

	// For the browser
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public WebNode getNodeXML() {
		WebNode node = WebNodeDao.instances.getWebNode(id);
		if (node == null)
			throw new RuntimeException("Get: WebNode with " + id + " not found");
		return node;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public WebNode getNodeHTML() {
		WebNode node = WebNodeDao.instances.getWebNode(id);
		if (node == null)
			throw new RuntimeException("Get: WebNode with " + id + " not found");
		return node;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putNode(JAXBElement<WebNode> node) {
		WebNode c = node.getValue();
		return putAndGetResponse(c);
	}

	@DELETE
	public void deleteNode() {
		WebNode c = WebNodeDao.instances.removeNode(id);
		if (c == null)
			throw new RuntimeException("Delete: WebNode with " + id + " not found");
	}

	private Response putAndGetResponse(WebNode node) {
		Response res;
		if (WebNodeDao.instances.isExists(node.getId())) {
			res = Response.noContent().build();
		} else {
			res = Response.created(uriInfo.getAbsolutePath()).build();
		}
		WebNodeDao.instances.createNode(node);
		return res;
	}

}
