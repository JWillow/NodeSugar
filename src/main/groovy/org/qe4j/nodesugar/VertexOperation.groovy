package org.qe4j.nodesugar

import static com.tinkerpop.blueprints.Direction.*;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

class VertexOperation {
	Direction direction
	Vertex vertex
	Map<String,Object> edgeProperties


	Edge createEdgeWithOtherVertex(Vertex vertexArg, Direction directionArg) {
		Graph graph = vertex.graph
		Edge edge
		if(direction.equals(OUT)) {
			edge = graph.addEdge(edgeProperties.id, vertex, vertexArg, edgeProperties.label.toString())
		} else {
			edge = graph.addEdge(edgeProperties.id, vertexArg, vertex, edgeProperties.label.toString())
		}
		edgeProperties.each { key,value ->
			if(key == "id" || key =="label") {
				return
			}
			edge.setProperty(key, value)
		}
		return edge
	}


	def edgeFrom = { Vertex vertex ->
		def result = [:]
		vertex.getEdges(OUT).each {Edge edge ->
			if(equals(edgeProperties,edge)) {
				result.put(edge, edge.getVertex(IN))
			}
		}
		return result
	}
	def edgeTo = { Vertex vertex ->
		def result = [:]
		vertex.getEdges(IN).each {Edge edge ->
			if(equals(edgeProperties,edge)) {
				result.put(edge, edge.getVertex(OUT))
			}
		}
		return result
	}

	List<Vertex> searchVerteces(int depth, Closure closureToApply) {
		def selectEdgeDirection
		if(OUT.equals(direction)) {
			selectEdgeDirection = edgeFrom
		} else {
			selectEdgeDirection = edgeTo
		}
		return searchVerteces(vertex, depth, selectEdgeDirection, closureToApply, true)
	}

	void removeEdges() {
		def selectEdgeDirection
		if(OUT.equals(direction)) {
			selectEdgeDirection = edgeFrom
		} else {
			selectEdgeDirection = edgeTo
		}
		Graph graph = vertex.graph
		selectEdgeDirection(vertex).each { Edge edge, Vertex vertexLinked ->
			graph.removeEdge(edge);
		}
	}

	List<Vertex> searchVerteces(Vertex vertex, int depth, Closure selectEdgeDirection, Closure closureToApply, boolean ignoreApplyClosure) {
		List<Vertex> result = []
		if(depth <= 0) {
			return result
		}
		Map<Edge,Vertex> selection = selectEdgeDirection(vertex);
		if(!ignoreApplyClosure && false == closureToApply(vertex)) {
			return result;
		}
		selection.each { Edge edge, Vertex vertexLinked ->
			result << vertexLinked
			result.addAll(searchVerteces(vertexLinked,--depth,selectEdgeDirection,closureToApply,false))
		}
		return result
	}

	private boolean equals(Map<String,Object> edgeProperties, Edge edge) {
		return edgeProperties.every { key,value ->
			if(key == "id") {
				return edge.getId().equals(value)
			} else if (key == "label") {
				return edge.getLabel().equals(value.toString())
			} else if (key == "select" && value instanceof Closure){
				return value.call(edge)
			} else {
				return edge.getProperty(key).equals(value)
			}
		}
	}
}
