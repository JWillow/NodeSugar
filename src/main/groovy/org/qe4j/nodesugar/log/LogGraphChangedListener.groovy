package org.qe4j.nodesugar.log

import org.slf4j.LoggerFactory;
import org.slf4j.Logger
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;

class LogGraphChangedListener implements GraphChangedListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogGraphChangedListener.class)

	private final Graph graph;

	public LogGraphChangedListener(final Graph graph) {
		this.graph = graph;
	}

	public void vertexAdded(Vertex vertex) {
		LOGGER.info("vertexAdded - {}", vertex);
	}

	public void vertexPropertyChanged(Vertex vertex, String key, Object setValue) {
		LOGGER.info("vertexPropertyChanged - {} - {} - {}", vertex, key, setValue);
	}

	public void vertexPropertyRemoved(Vertex vertex, String key, Object removedValue) {
		LOGGER.info("vertexPropertyRemoved - {} - {} - {}", vertex, key, removedValue);
	}

	public void vertexRemoved(Vertex vertex) {
		LOGGER.info("vertexRemoved - {}", vertex);
	}

	public void edgeAdded(Edge edge) {
		LOGGER.info("edgeAdded - {}", edge);
	}

	public void edgePropertyChanged(Edge edge, String key, Object setValue) {
		LOGGER.info("edgePropertyChanged - {} - {} - {}", edge, key, setValue);
	}

	public void edgePropertyRemoved(Edge edge, String key, Object removedValue) {
		LOGGER.info("edgePropertyRemoved - {} - {} - {}", edge, key, removedValue);
	}

	public void edgeRemoved(Edge edge) {
		LOGGER.info("edgeRemoved - {}", edge);
	}
}
