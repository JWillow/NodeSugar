package org.qe4j.nodesugar.trigger;

import com.tinkerpop.blueprints.Vertex;

public interface OnVertexCreation {
	void afterCreation(Vertex vertex);
}
