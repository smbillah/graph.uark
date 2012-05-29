 1 var width = 960,
 2     height = 500;
 3 
 4 var color = d3.scale.category20();
 5 
 6 var force = d3.layout.force()
 7     .charge(-120)
 8     .linkDistance(30)
 9     .size([width, height]);
10 
11 var svg = d3.select("#chart").append("svg")
12     .attr("width", width)
13     .attr("height", height);
14 
15 d3.json("miserables.json", function(json) {
16   force
17       .nodes(json.nodes)
18       .links(json.links)
19       .start();
20 
21   var link = svg.selectAll("line.link")
22       .data(json.links)
23     .enter().append("line")
24       .attr("class", "link")
25       .style("stroke-width", function(d) { return Math.sqrt(d.value); });
26 
27   var node = svg.selectAll("circle.node")
28       .data(json.nodes)
29     .enter().append("circle")
30       .attr("class", "node")
31       .attr("r", 5)
32       .style("fill", function(d) { return color(d.group); })
33       .call(force.drag);
34 
35   node.append("title")
36       .text(function(d) { return d.name; });
37 
38   force.on("tick", function() {
39     link.attr("x1", function(d) { return d.source.x; })
40         .attr("y1", function(d) { return d.source.y; })
41         .attr("x2", function(d) { return d.target.x; })
42         .attr("y2", function(d) { return d.target.y; });
43 
44     node.attr("cx", function(d) { return d.x; })
45         .attr("cy", function(d) { return d.y; });
46   });
47 });