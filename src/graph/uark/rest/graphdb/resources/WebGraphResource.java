package graph.uark.rest.graphdb.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

public class WebGraphResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;

	public WebGraphResource(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}

	//Application integration
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public WebGraph getGraph() {
		WebGraph graph = WebNodeDao.instances.buildCoAuthorGraph2(id, "3");
		
		if (graph == null)
			throw new RuntimeException("Get: WebNode with " + id + " not found");
		return graph;
	}
}
