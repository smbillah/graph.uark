package graph.uark.rest.graphdb.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Path("/graphs")
public class WebGraphResources {
	
	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;


	
	@Path("{graph}")
	public WebGraphResource getGraph(@PathParam("graph") String id) {
		return new WebGraphResource(uriInfo, request, id);
	}

}
