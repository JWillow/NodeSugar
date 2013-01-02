package org.q4j.nodesugar

import org.qe4j.nodesugar.Language;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerVertex;

import spock.lang.Specification

class VertexSpecification extends Specification {

	Graph graph = Mock(Graph.class)

	static {
		Language.load()
	}

	def "Interrogation - No need to call get method on vertex to get property value, get it directly on vertex"() {
		setup:
		Vertex vertex = Mock(Vertex.class)
		when:
		vertex['key']
		then:
		1 * vertex.getProperty("key")
	}

	def "Interrogation - No need to call set method on vertex to affect new property, set it directly"(){
		setup:
		Vertex vertex = Mock(Vertex.class)
		when:
		vertex.key = "value"
		then:
		1 * vertex.setProperty("key", "value")
	}

	def "Creation - Properties - You can add to the graph a property 'id' then a vertex is automaticly created with this id, and a reference is done with the graph"() {
		setup:
		Vertex vertex = Mock(Vertex.class)
		when:
		Vertex otherVertex = graph << [id:'test']
		then:
		1 * graph.addVertex("test") >> vertex
		1 * vertex.setProperty("graph", graph)
		otherVertex == vertex
		0 * _._
	}

	def "Creation - Properties - Id is optional, if you don't specified one, we generate one, and a reference is done with the graph"() {
		setup:
		Vertex vertex = Mock(Vertex.class)
		when:
		Vertex otherVertex = graph << [:]
		then:
		1 * graph.addVertex("1") >> vertex
		1 * vertex.setProperty("graph", graph)
		otherVertex == vertex
		0 * _._
	}

	def "Creation - Properties - You can add to the graph a property 'id' with other properties then a vertex is automaticly created with this id, a reference is done with the graph, and others properties are affected as properties"() {
		setup:
		Vertex vertex = Mock(Vertex.class)
		when:
		Vertex otherVertex = graph << [id:'test',key:'value',key1:'value1']
		then:
		1 * graph.addVertex("test") >> vertex
		1 * vertex.setProperty("graph", graph)
		1 * vertex.setProperty("key", "value")
		1 * vertex.setProperty("key1", "value1")
		otherVertex == vertex
		0 * _._
	}

	def "Creation - Properties - Type is a special property, it can be use to compose the id if no id is specified"() {
		setup:
		Vertex vertex = Mock(Vertex.class)
		when:
		Vertex otherVertex = graph << [type:"USER"]
		then:
		1 * graph.addVertex({it =~ /USER.+/ }) >> vertex
		1 * vertex.setProperty("graph", graph)
		1 * vertex.setProperty("type", "USER")
		otherVertex == vertex
		0 * _._
	}

	enum EnumForTest {
		TYPE1
	}
	def "Creation - Enum - You can create Vertex based on a simple enum. The enum is used as type, and the id is automaticly generated"() {
		setup:
		Vertex vertex = Mock(Vertex.class)
		when:
		Vertex otherVertex = graph << EnumForTest.TYPE1
		then:
		1 * graph.addVertex({it =~ /TYPE1.+/ }) >> vertex
		1 * vertex.setProperty("graph", graph)
		1 * vertex.setProperty("type", "TYPE1")
		otherVertex == vertex
		0 * _._
	}

	def "Remove - You can remove a vertex on a graph simply with '--'. Before that you need to reference the graph as property"() {
		setup:
		Vertex vertex = Mock(Vertex.class)
		when:
		vertex--
		then:
		1 * vertex.getProperty('graph') >> graph
		1 * graph.removeVertex(vertex)
	}

	def "Remove - You can remove a vertex on a graph simply with '--'. But If you don't have graph as property an IllegalStateException is thrown"() {
		setup:
		Vertex vertex = Mock(Vertex.class)
		when:
		vertex--
		then:
		thrown(IllegalStateException)
	}
}
