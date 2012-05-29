package graph.uark.rest.graphdb.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.json.JSONWithPadding;



//Will map the resource to the URL todos
@Path("/nodes")
public class WebNodesResource {

	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	// Return the list of todos to the user in the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<WebNode> getNodesBrowser() {
		List<WebNode> nodes = new ArrayList<WebNode>();
		nodes.add(WebNodeDao.instances.getWebNode("1"));
		return nodes;
	}

	// Return the list of todos for applications
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<WebNode> getNodes() {
		List<WebNode> nodes = new ArrayList<WebNode>();
		nodes.add(WebNodeDao.instances.getWebNode("1"));
		return nodes;
	}

	// retuns the number of nodes
	// Use http://localhost:8081/graph.uark/rest/nodes/count
	// to get the total number of records
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		int count = 10;
		return String.valueOf(count);
	}
	
	@POST
	@Path("search")
	@Produces({ MediaType.APPLICATION_JSON})	
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<WebNode> executeQuery(@FormParam("query") String query) {
		return WebNodeDao.instances.getWebNodesFromQuery(query.toLowerCase());
	}
	
	@GET
	@Path("autocomplete")
	@Produces({"application/x-javascript"})	
	public JSONWithPadding executeAutoCompleteQuery(@QueryParam("callback") String callback, @QueryParam("maxRows") String maxRows, @QueryParam("name_startsWith") String startsWith ) {
		//http://ws.geonames.org/searchJSON?callback=jQuery17&featureClass=P&style=full&maxRows=12&name_startsWith=fa&_=1337684744030
//		return new WebSearchResource(WebNodeDao.instances.getWebNodesFromQuery(startsWith.toLowerCase()+"*"));
		return new JSONWithPadding(WebNodeDao.instances.getWebNodesFromQuery(startsWith.toLowerCase()+"*"), callback);
	}
	

	@POST
	@Path("add_author")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String addAuthor(@FormParam("fname") String fname,
			@FormParam("mname") String mname,
			@FormParam("lname") String lname,
			@FormParam("num_pubs") String num_pubs,
			@FormParam("citeseer_id") String citeseer_id,
			@FormParam("index") String index,
			@Context HttpServletResponse servletResponse) throws IOException {
				

		WebNode node= WebNodeDao.instances.createAuthor(fname,mname,lname,num_pubs,citeseer_id,index);
		if(node!=null){
			return  node.getName()+ " {id:"+node.getId()+"}"+" created successfully!";
		}else{
			return "An error has been occured :(";
		}
				
//		servletResponse.sendRedirect("../../pages/forms.htm");
//		servletResponse.sendRedirect("../create_node.htm");
		
	}
	
	@POST
	@Path("add_paper")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String addPaper(@FormParam("title") String title,			
			@FormParam("index") String index,
			@Context HttpServletResponse servletResponse) throws IOException {
				

		WebNode node= WebNodeDao.instances.createPaper(title, index);
		if(node!=null){
			return  node.getName()+ " {id:"+node.getId()+"}"+" created successfully!";
		}else{
			return "An error has been occured :(";
		}
				
//		servletResponse.sendRedirect("../../pages/forms.htm");
//		servletResponse.sendRedirect("../create_node.htm");
		
	}
	@POST
	@Path("add_interest")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String addInterest(@FormParam("name") String name,			
			@FormParam("index") String index,
			@Context HttpServletResponse servletResponse) throws IOException {
				

		WebNode node= WebNodeDao.instances.createInterest(name, index);
		if(node!=null){
			return  node.getName()+ " {id:"+node.getId()+"}"+" created successfully!";
		}else{
			return "An error has been occured :(";
		}
				
//		servletResponse.sendRedirect("../../pages/forms.htm");
//		servletResponse.sendRedirect("../create_node.htm");
		
	}



	// Allows to type http://localhost:8081/graph.uark/rest/nodes/1
	// 1 will be treaded as parameter node and passed to WebNodeResource
	@Path("{node}")
	public WebNodeResource getNode(@PathParam("node") String id) {
		return new WebNodeResource(uriInfo, request, id);
	}
	
	
}