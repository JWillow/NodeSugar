package org.q4j.nodesugar

import static com.tinkerpop.blueprints.Direction.*;
import org.qe4j.nodesugar.Language;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import spock.lang.Specification

class SearchSpecification extends Specification {
	
	Graph graph = Mock(Graph);
	Vertex vertexOrigin = Mock(Vertex);
	Vertex vertexLvl1 = Mock(Vertex);
	Edge edgeLvl1 = Mock(Edge)
	Iterable<Edge> edgesLvl1 = Mock(Iterable)
	Iterator iteratorLvl1 = Mock(Iterator)
	
	static {
		Language.load()
	}
	
	def "Search - To search vertices, you need to specifie the depth of the search, the direction based on vertex search origin and criteria, here the edge label."() {
		when:
			Collection<Vertex> resultVerteces = 1 % vertexOrigin << [label:"myRelation"]
		then:
			1 * vertexOrigin.getEdges(IN, []) >> edgesLvl1
			1 * edgesLvl1.iterator() >> iteratorLvl1
			2 * iteratorLvl1.hasNext() >>> [true, false]
			1 * iteratorLvl1.next() >> edgeLvl1
			1 * edgeLvl1.getLabel() >> "myRelation"
			1 * edgeLvl1.getVertex(OUT) >> vertexLvl1
			1 == resultVerteces.size()
			resultVerteces.contains(vertexLvl1)
			0 * _._
	}
	
	def "Search - You can search verteces reference by edge properties"() {
		when: 
		Collection<Vertex> resultVerteces = 1 % vertexOrigin << [label:"myRelation",key1:"test"]
		then:
		1 * vertexOrigin.getEdges(IN, []) >> edgesLvl1
		1 * edgesLvl1.iterator() >> iteratorLvl1
		2 * iteratorLvl1.hasNext() >>> [true, false]
		1 * iteratorLvl1.next() >> edgeLvl1
		1 * edgeLvl1.getLabel() >> "myRelation"
		1 * edgeLvl1.getProperty("key1") >> "test"
		1 * edgeLvl1.getVertex(OUT) >> vertexLvl1
		1 == resultVerteces.size()
		resultVerteces.contains(vertexLvl1)
		0 * _._
	}

	def "Search - Depth - The depth specified the level of the search. If the search is a success to level 1 and you specified a depth of 2, the search continue with same criteria for the next level."() {
		setup:
			Vertex vertexLvl2 = Mock(Vertex)
			Edge edgeLvl2 = Mock(Edge)
			Iterable<Edge> edgesLvl2 = Mock(Iterable)
			Iterator iteratorLvl2 = Mock(Iterator)
		when:
			Collection<Vertex> resultVerteces = 2 % vertexOrigin << [label:"myRelation"]
		then:
			// LVL 1
			1 * vertexOrigin.getEdges(IN, []) >> edgesLvl1
			1 * edgesLvl1.iterator() >> iteratorLvl1
			2 * iteratorLvl1.hasNext() >>> [true, false]
			1 * iteratorLvl1.next() >> edgeLvl1
			1 * edgeLvl1.getLabel() >> "myRelation"
			1 * edgeLvl1.getVertex(OUT) >> vertexLvl1
			// LVL 2
			1 * vertexLvl1.getEdges(IN, []) >> edgesLvl2
			1 * edgesLvl2.iterator() >> iteratorLvl2
			2 * iteratorLvl2.hasNext() >>> [true, false]
			1 * iteratorLvl2.next() >> edgeLvl2
			1 * edgeLvl2.getLabel() >> "myRelation"
			1 * edgeLvl2.getVertex(OUT) >> vertexLvl2
			
			2 == resultVerteces.size()
			resultVerteces.contains(vertexLvl1)
			resultVerteces.contains(vertexLvl2)
			0 * _._
	}
	
	def "Search - Depth - If you specified 0 as depth, the search has no level limit"() {
		setup:
			Vertex vertexLvl2 = Mock(Vertex)
			Edge edgeLvl2 = Mock(Edge)
			Iterable<Edge> edgesLvl2 = Mock(Iterable)
			Iterator iteratorLvl2 = Mock(Iterator)
			
			Edge edgeLvl3 = Mock(Edge)
			Iterable<Edge> edgesLvl3 = Mock(Iterable)
			Iterator iteratorLvl3 = Mock(Iterator)
			Vertex vertexLvl3 = Mock(Vertex)
		when:
			Collection<Vertex> resultVerteces = 0 % vertexOrigin << [label:"myRelation"]
		then:
			// LVL 1
			1 * vertexOrigin.getEdges(IN, []) >> edgesLvl1
			1 * edgesLvl1.iterator() >> iteratorLvl1
			2 * iteratorLvl1.hasNext() >>> [true, false]
			1 * iteratorLvl1.next() >> edgeLvl1
			1 * edgeLvl1.getLabel() >> "myRelation"
			1 * edgeLvl1.getVertex(OUT) >> vertexLvl1
			
			// LVL 2
			1 * vertexLvl1.getEdges(IN, []) >> edgesLvl2
			1 * edgesLvl2.iterator() >> iteratorLvl2
			2 * iteratorLvl2.hasNext() >>> [true, false]
			1 * iteratorLvl2.next() >> edgeLvl2
			1 * edgeLvl2.getLabel() >> "myRelation"
			1 * edgeLvl2.getVertex(OUT) >> vertexLvl2
			
			// LVL 3
			1 * vertexLvl2.getEdges(IN, []) >> edgesLvl3
			1 * edgesLvl3.iterator() >> iteratorLvl3
			2 * iteratorLvl3.hasNext() >>> [true, false]
			1 * iteratorLvl3.next() >> edgeLvl3
			1 * edgeLvl3.getLabel() >> "otherRelation"
			
			// RESULT
			2 == resultVerteces.size()
			resultVerteces.contains(vertexLvl1)
			resultVerteces.contains(vertexLvl2)
			0 * _._
	}
	
}
