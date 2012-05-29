var width = 960,
    height = 500;

var color = d3.scale.category20();

var force = d3.layout.force()
    .charge(-100)
    .gravity(.05)
    .linkDistance(150)
    .size([width, height]);


var svg = d3.select("#graph").append("svg")
    .attr("width", width)
    .attr("height", height);



d3.json("../rest/graphs/1", function(json) {
  force
      .nodes(json.nodes)
      .links(json.links)
      .start();

  var link = svg.selectAll("line.link")
      .data(json.links)
      .enter().append("line")
      .attr("class", "link")
      .style("stroke-width", function(d) { return Math.sqrt(d.value); });

  var node = svg.selectAll("circle.node")
      .data(json.nodes)
      .enter().append("circle")
      .attr("class", "node")
      .attr("r", 10)
      .style("fill", function(d) { return color(d.web_id); })
      .call(force.drag)
      ;

  node.append("title")
      .text(function(d) { return d.name; });
  
  node.append("svg:text")
  	   .attr("class", "nodetext")
  	   .attr("dx", 12)
  	   .attr("dy", ".35em")
  	   .text(function(d) { return d.name });

  force.on("tick", function() {
    link.attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; });

    
    node.attr("cx", function(d) { return d.x; })
        .attr("cy", function(d) { return d.y; });
  });
});