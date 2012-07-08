/**
 * 
 */
//autocomple functionality
function autocomplete(hidden_elem_id, form_id, postAction){
	$("#"+form_id).val("");
			
	$("#"+form_id).each(function() {	
		/* create new hidden input with name of original input */
		$(this).after("<input type=\"hidden\" \" id=\"" + hidden_elem_id + "\" />");
			
		$(this).autocomplete({
			source : function(request, response) {
					$.ajax({
							url : "../rest/nodes/autocomplete",
							dataType : "jsonp",
							
							data : {
								featureClass : "P",
								style : "full",
								maxRows : 14,
								name_startsWith : request.term,
								index_type:"authors"
							},
						
							success : function(data){
								ajaxData=data;
								response($.map(data, 
										function(item) {return {label : item.name,	value : item.id}}
								));
							}
					});
			},
			
			minLength : 2,
			
			focus: function(event, ui) {  
						var selectedObj = ui.item;
						$('#'+form_id).val(selectedObj.label);
						return false;						
					},
								
			select : function(event, ui) {
						var selectedObj = ui.item;	
						log(selectedObj ? "Selected: " + selectedObj.label+ " (id:"+selectedObj.value+"); press 'Draw' button now.": 
							"Nothing selected, input was "+ this.value, form_id);
															
						$('#'+form_id).val(selectedObj.label);
						$('#'+hidden_elem_id).val(selectedObj.value);
						if (typeof postAction !== "undefined"){
							window[ postAction](selectedObj.value);
						}
						return false;						
					},
											
			open : function() {$(this).removeClass("ui-corner-all").addClass("ui-corner-top");},
			close : function() {$(this).removeClass("ui-corner-top").addClass("ui-corner-all");}
		});
	});
		
};

//logging
function log(message, suffix) {
	$("<div/>").text(message).prependTo("#log_"+suffix);
	$("#log_"+suffix).scrollTop(0);
};

function loadSourceID(id){
	$("input[name=source_id]").val(id);
};

function loadTargetID(id){
	$("input[name=target_id]").val(id);
};


function loadData(id){
	$.ajax({
		  type: 'GET',
		  url: '../rest/nodes/'+id,
		  //data: { postVar1: 'theValue1', postVar2: 'theValue2' },
		  
		  //beforeSend:function(){
		    // this is where we append a loading image		    
		  //},
		  
		  success:function(data){
			  $("input[name=fname]").val(data.fname);
			  $("input[name=mname]").val(data.mname);
			  $("input[name=lname]").val(data.lname);
			  $("input[name=num_pubs]").val(data.num_pubs);
			  $("input[name=citeseer_id]").val(data.citeseer_id);
			  $("input[name=id]").val(id);
			  		
		  },
		  error:function(){ 
			  $.sticky(responseText);
		  }
		});
};



//mouseover and mouseout functions
function mouseover_node() {
	d3.select(this).select("circle").transition()
	.duration(750)
	.attr("r", 16);
};

function mouseout_node() {
	d3.select(this).select("circle").transition()
	.duration(750)
	.attr("r", 10);
};

function mouseover_link() {
	  d3.select(this).transition()
	     .duration(700)			    
		 .attr("class", "link2")
		 ;
};

function mouseout_link() {
	d3.select(this).transition()
    	.duration(700)
    	.attr("class", "link")
    ;
    
};

function mouseover_path() {
	  d3.select(this).transition()
	     .duration(700)			    
		 .attr("class", "link2")
		 .attr("marker-end", function(d) { return "url(#arrow2)"; })
		 ;
};

function mouseout_path() {
	d3.select(this).transition()
    	.duration(700)
    	.attr("class", "link")
    	.attr("marker-end", function(d) { return "url(#arrow)"; })
    ;
    
};

//draw coauthor graph
function coauthor_graph(div_id, node_id, depth, width, height){
	var force = d3.layout.force()
    .charge(-130)
    .gravity(.005)
    .theta(.1)
    .linkDistance(60)
    .size([width, height])
    ;

var svg = d3.select("#"+div_id).append("svg:svg")
	.attr("width", width)
	.attr("height", height)
	.attr("pointer-events", "all")
	//.append('svg:g')
		//.call(d3.behavior.zoom().x(x).y(y).scaleExtent([1, 8]).on("zoom", 
			//	function(){redraw(svg );}))
	//.append('svg:g')
	;


  //library call
	d3.json("../rest/graphs/"+div_id+"/"+node_id+"/"+depth, function(json) {
	  json.nodes[0].fixed=true;
	  json.nodes[0].x=width/2;
	  json.nodes[0].y=height/2;
	  force.nodes(json.nodes)
	       .links(json.links)
	       .linkDistance(function(d,i){return 50 + 60 * (-d.value+json.max_connection)/json.max_connection;})
	       .start();
	
	  var link = svg.selectAll("line.link")
	      .data(json.links)
	      .enter().append("svg:line")
	      .attr("class", "link")
	      .on("mouseover", mouseover_link)
		  .on("mouseout", mouseout_link)
	      .style("stroke-width", function(d) { return 2+ 5*d.value/json.max_connection; });
	  
	  link.append("title")
	  .text(function(d) { return "coauthored "+d.value+ " times"; });
	
	
	
	  var node = svg.selectAll("g.node")
  		.data(json.nodes)
		.enter().append("svg:g")
  		.attr("class", "node")
  		.on("mouseover", mouseover_node)
		.on("mouseout", mouseout_node)
		.on("dblclick",node_select )    				
		//.on("dblclick", function(d,i) { node_select(d.id); })
  		.call(force.drag)
  		;
	  
	  //var node = svg.selectAll("a.node")
	    //.data(json.nodes)
	  //.enter().append("a")
	   // .attr("class", "node")
	    //.attr("xlink:href", "http://whatever.com")
	    //.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
	    //.call(force.drag);
	  
	  node.append("svg:circle")					      
	      //.attr("class", "node")
	      .attr("r", function(d){ return 10;})
	      .attr("class", function(d) {if(d.web_id) return "coauthornode";
	    	  						  else return "centernode";})					      
	      ;
	  
	  node.append("svg:text")
	  	   .attr("class", "nodetext")
	  	   .attr("dx", 12)
	  	   .attr("dy", ".35em")
	  	   .text(function(d) { return d.name; });
	
	  node.append("title")
	  .text(function(d) { return d.num_pubs>1?	"published "+d.num_pubs+ " articles":	
		  										"published "+d.num_pubs+ " article"; });				  
	
	  
	  force.on("tick", function() {
	    link.attr("x1", function(d) { return d.source.x; })
	        .attr("y1", function(d) { return d.source.y; })
	        .attr("x2", function(d) { return d.target.x; })
	        .attr("y2", function(d) { return d.target.y; });
	
	    
	    node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
	  });
	});
};

//draw citation graph
function citation_graph(div_id, node_id, depth, width, height){
	var force = d3.layout
		.force()
		    .charge(-130)
		    .gravity(.005)
		    .theta(.1)
		    //.linkDistance(150)
		    .size([width, height])
		    ;
			
	var svg = d3.select("#"+div_id)
		.append("svg:svg")
			.attr("width", width)
			.attr("height", height)
			.attr("pointer-events", "all")			
			;

	//Per-type markers, as they don't inherit styles.
	svg.append("svg:defs").selectAll("marker")
	.data(["arrow","arrow2"])
	  .enter().append("svg:marker")
		.attr("id", String)
		.attr("viewBox", "0 -5 10 10")
		.attr("refX", 20)
		.attr("refY", -2.0)
		.attr("markerWidth", 6)
		.attr("markerHeight", 6)
		.attr("orient", "auto")
		  .append("svg:path")
		  	.attr("d", "M0,-5L10,0L0,5");
			
  
      //library call
	d3.json("../rest/graphs/"+div_id+"/"+node_id+"/"+depth, function(json) {
	  json.nodes[0].fixed=true;
	  json.nodes[0].x=width/2;
	  json.nodes[0].y=height/2;
	  force.nodes(json.nodes)
	       .links(json.links)
	      //.linkDistance(function(d,i){return 50 + 60 * (-d.value+json.max_connection)/json.max_connection;})
      .start()
      ;
  
	var path = svg.append("svg:g").selectAll("path")
    	.data(json.links)
      .enter().append("svg:path")
	    .attr("class", "link")
	    .attr("marker-end", function(d) { return "url(#arrow)"; })
	  	.on("mouseover", mouseover2)
		.on("mouseout", mouseout2)		
		;

	path.append("title")
	.text(function(d) { return "cites "+d.value+ " times"; });
  

	var node = svg.selectAll("g.node")
		.data(json.nodes)
	   .enter().append("svg:g")
		.attr("class", "node")
		.on("mouseover", mouseover)
		.on("mouseout", mouseout)
		.on("dblclick",node_select )    				
		//.on("dblclick", function(d,i) { node_select(d.id); })
		.call(force.drag)
		;

	node.append("svg:circle")					      
      .attr("class", "node")
      .attr("r", function(d){ return 10;})
      .style("fill", function(d) { 
    	  				if(d.web_id) return node_color;
    	  				else return "rgb(31, 119, 180)";})					      
      ;
  
	node.append("svg:text")
  	   .attr("class", "nodetext")
  	   .attr("dx", 12)
  	   .attr("dy", ".35em")
  	   .text(function(d) { return d.name;})
  	   ;

	node.append("title")
		.text(function(d) { return d.num_pubs>1?	"cited "+d.num_pubs+ " times":	
	  												"cited "+d.num_pubs+ " time"; });				  

	// Use elliptical arc path segments to doubly-encode directionality.
	force.on("tick", function() {	
		path.attr("d", function(d) {
		    var dx = d.target.x - d.source.x,
		        dy = d.target.y - d.source.y,
		        dr = Math.sqrt(dx * dx + dy * dy);
		    return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
		  });					
	  
	  node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });					  
	});
  });
};


//draw citation graph
function directed_graph(div_id, node_id, depth, width, height){
	var force = d3.layout
		.force()
		    .charge(-130)
		    .gravity(.005)
		    .theta(.1)
		    //.linkDistance(150)
		    .size([width, height])
		    ;
			
	var svg = d3.select("#"+div_id)
		.append("svg:svg")
			.attr("width", width)
			.attr("height", height)
			.attr("pointer-events", "all")			
			;

	//Per-type markers, as they don't inherit styles.
	svg.append("svg:defs").selectAll("marker")
	.data(["arrow","arrow2"])
	  .enter().append("svg:marker")
		.attr("id", String)
		.attr("viewBox", "0 -5 10 10")
		.attr("refX", 24)
		.attr("refY", 0)
		.attr("markerWidth", 8)
		.attr("markerHeight", 8)
		.attr("orient", "auto")
		  .append("svg:path")
		  	.attr("d", "M0,-5L10,0L0,5");
			
  
      //library call
	d3.json("../rest/graphs/"+div_id+"/"+node_id+"/"+depth, function(json) {
	  json.nodes[0].fixed=true;
	  json.nodes[0].x=width/2;
	  json.nodes[0].y=height/2;
	  force.nodes(json.nodes)
	       .links(json.links)
	       .linkDistance(function(d,i){return 50 + 60 * (-d.value+json.max_connection)/json.max_connection;})
      .start()
      ;
  
	var path = svg.append("svg:g").selectAll("line.link")
    	.data(json.links)
      .enter().append("svg:line")
	    .attr("class", "link")
	    .attr("marker-end", function(d) { return "url(#arrow)"; })
	  	.on("mouseover", mouseover_path)
		.on("mouseout", mouseout_path)		
		;

	path.append("title")
	.text(function(d) { return "cites "+d.value+ " times"; });
  

	var node = svg.selectAll("g.node")
		.data(json.nodes)
	   .enter().append("svg:g")
		.attr("class", "node")
		.on("mouseover", mouseover_node)
		.on("mouseout", mouseout_node)
		.on("dblclick",node_select )    				
		//.on("dblclick", function(d,i) { node_select(d.id); })
		.call(force.drag)
		;

	node.append("svg:circle")					      
      //.attr("class", "node")
      .attr("r", function(d){ return 10;})
      .attr("class", function(d) {if(d.web_id) return "coauthornode";
	    	  						  else return "centernode";})					      
      ;
  
	node.append("svg:text")
  	   .attr("class", "nodetext")
  	   .attr("dx", 12)
  	   .attr("dy", ".35em")
  	   .text(function(d) { return d.name;})
  	   ;

	node.append("title")
		.text(function(d) { return d.num_pubs>1?	"cited "+d.num_pubs+ " times":	
	  												"cited "+d.num_pubs+ " time"; });				  

	// Use elliptical arc path segments to doubly-encode directionality.
	force.on("tick", function() {	
		path.attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; });					
	  
	  node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });					  
	});
  });
};
