package org.q4j.nodesugar

import org.qe4j.nodesugar.Language;
import org.qe4j.nodesugar.VertexOperation

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import spock.lang.Specification

class EdgeSpecification extends Specification {

	Vertex vertexA = Mock(Vertex);
	Vertex vertexB = Mock(Vertex);
	Graph graph = Mock(Graph);

	VertexOperation operation = Mock(VertexOperation)

	static {
		Language.load()
	}

	def "Creation - To create an edge between verteces just use '<<' and specified a label"() {
		when:
		vertexA << "link" << vertexB
		then:
		1 * vertexA.getProperty("graph") >> graph
		1 * graph.addEdge(null,vertexB,vertexA,"link")
		0 * _._
	}

	def "Creation - To create an edge between verteces just use '<<' and specified a label. You can change the direction"() {
		when:
		vertexA >> "link" >> vertexB
		then:
		1 * vertexA.getProperty("graph") >> graph
		1 * graph.addEdge(null,vertexA,vertexB,"link")
		0 * _._
	}

	enum EgdeLabel {
		LABEL_1
	};

	def "Creation - for the edge label you can specified an enum"() {
		when:
		vertexA >> EgdeLabel.LABEL_1 >> vertexB
		then:
		1 * vertexA.getProperty("graph") >> graph
		1 * graph.addEdge(null,vertexA,vertexB,"LABEL_1")
		0 * _._
	}
	
	def "Creation - for the edge label you can specified an enum, you can change the direction"() {
		when:
		vertexA << EgdeLabel.LABEL_1 << vertexB
		then:
		1 * vertexA.getProperty("graph") >> graph
		1 * graph.addEdge(null,vertexB,vertexA,"LABEL_1")
		0 * _._
	}
	
	def "Creation - Properties, you can specified properties"() {
		setup:
		Edge mockEdge = Mock(Edge)
		when:
		Edge edge = vertexA << [label:'myLabel',key:'value'] << vertexB
		then:
		1 * vertexA.getProperty("graph") >> graph
		1 * graph.addEdge(null,vertexB,vertexA,"myLabel") >> mockEdge 
		edge == mockEdge
		1 * mockEdge.setProperty("key","value")
		0 * _._
	}
	
	def "Creation - Properties, you can specified properties. You can change the direction"() {
		setup:
		Edge mockEdge = Mock(Edge)
		when:
		Edge edge = vertexA >> [label:'myLabel',key:'value'] >> vertexB
		then:
		1 * vertexA.getProperty("graph") >> graph
		1 * graph.addEdge(null,vertexA,vertexB,"myLabel") >> mockEdge
		edge == mockEdge
		1 * mockEdge.setProperty("key","value")
		0 * _._
	}
	
	def "Remove - To remove an edge just use '--'"() {
		setup:
		Edge edge = Mock(Edge)
		when:
		edge--
		then:
		1 * edge.getVertex(Direction.OUT) >> vertexA
		1 * vertexA.getProperty("graph") >> graph
		1 * graph.removeEdge(edge)
		0 * _._
	}
}
