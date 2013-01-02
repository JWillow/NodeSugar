package org.qe4j.nodesugar.log

import com.tinkerpop.blueprints.Vertex;
class VertexDecoratorForLog {
	Vertex vertex;

	public String toString() {
		StringBuffer strBuffer = new StringBuffer(vertex.toString());
		strBuffer.append("contains-(");
		switch(vertex.type) {
			case EVENT :
				strBuffer.append(vertex.event.toString());
				break;
			default:
				return vertex.toString();
		}
		strBuffer.append(")");
		return strBuffer.toString();
	}
}
