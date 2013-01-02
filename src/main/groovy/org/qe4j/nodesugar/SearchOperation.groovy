package org.qe4j.nodesugar

import com.tinkerpop.blueprints.Vertex;

class SearchOperation {
	public enum Element {VERTEX,EDGE}
	Vertex vertex
	int depth;
	Element element;
	Closure closureToApply = {}
}
