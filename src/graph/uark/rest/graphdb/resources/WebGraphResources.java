package graph.uark.rest.graphdb.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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


	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("coauthor/{graph}/{depth}")
	public WebGraph getGraph(@PathParam("graph") String id, @PathParam("depth") String depth) {
		//Application integration
		WebGraph graph = WebNodeDao.instances.buildCoAuthorGraph(id, depth);
		if (graph == null)
			throw new RuntimeException("Get: WebNode with " + id + " not found");
		return graph;
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("citing/{graph}/{depth}")
	public WebGraph getCitingGraph(@PathParam("graph") String id, @PathParam("depth") String depth) {
		//Application integration
		WebGraph graph = WebNodeDao.instances.buildCitingAuthorGraph(id,depth);
		if (graph == null)
			throw new RuntimeException("Get: WebNode with " + id + " not found");
		return graph;
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("cited/{graph}/{depth}")
	public WebGraph getCitedGraph(@PathParam("graph") String id, @PathParam("depth") String depth) {
		//Application integration
		WebGraph graph = WebNodeDao.instances.buildCitedAuthorGraph(id,depth);
		if (graph == null)
			throw new RuntimeException("Get: WebNode with " + id + " not found");
		return graph;
	}
}
