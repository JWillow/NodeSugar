package org.qe4j.nodesugar

import static com.tinkerpop.blueprints.Direction.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.qe4j.nodesugar.log.VertexDecoratorForLog
import org.qe4j.nodesugar.trigger.OnVertexCreation;
import org.slf4j.Logger;

import org.qe4j.nodesugar.log.VertexDecoratorForLog;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerVertex;

class Language {
	static load() {
	}

	private static AtomicInteger ai = new AtomicInteger();


	static {

		Closure addVertexCmd = {Graph graph, Map<String,Object> properties ->
			String id = properties['id']
			if(id == null) {
				if(properties.containsKey('type')) {
					id = "${properties['type'].toString()}_${ai.incrementAndGet()}";
				} else {
					id = "${ai.incrementAndGet()}";
				}
			}
			Vertex vertex = graph.addVertex(id)
			vertex.graph = graph
			properties.each{key,value ->
				if(key == 'id') {
					return
				}
				vertex.setProperty(key, value)
			}
			// Add behaviour
			//OnVertexCreation type = properties.type;
			//type.afterCreation(vertex);
			return vertex
		}

		Closure decorateVertexForLog = {Object[] args ->
			List<Object> newArray = [];
			if(args  != null) {
				for(int i = 0; i < args.length;i++) {
					if(args[i] instanceof Vertex) {
						newArray << new VertexDecoratorForLog(vertex:args[i]);
					} else {
						newArray << args[i]
					}
				}
			}
			return newArray.toArray();
		}


		ExpandoMetaClass.enableGlobally()

		Element.metaClass {
			getProperty = {name ->
				return delegate.getProperty(name)
			}

			setProperty = {name, val ->
				delegate.setProperty(name, val)
			}
		}

		Graph.metaClass {
			// <<
			leftShift << {  Map<String,Object> properties ->
				return addVertexCmd(delegate,properties)
			}

			// <<
			leftShift << {  Enum enumArg ->
				return addVertexCmd(delegate,[type:enumArg.toString()])
			}
		}

		Logger.metaClass {
			infoG << {String format, Object[] argArray ->
				if(delegate.isDebugEnabled()) {
					delegate.info(format,decorateVertexForLog(argArray));
				} else {
					delegate.info(format,argArray);
				}
			};
			debugG << {String format, Object[] argArray ->
				delegate.debug(format,decorateVertexForLog(argArray));
			};
		}

		Edge.metaClass {
			// --
			previous << {
				Graph graph = delegate.getVertex(OUT).graph
				if(graph == null) {
					throw new IllegalStateException("No graph is referenced !")
				}
				graph.removeEdge(delegate)
			}
		} 
		
		Vertex.metaClass {

			// --
			previous << {
				Graph graph = delegate.graph
				if(graph == null) {
					throw new IllegalStateException("No graph is referenced !")
				}
				graph.removeVertex(delegate)
			}
			// <<
			leftShift << {  String  edgeLabel->
				VertexOperation vo = new VertexOperation(direction:IN,vertex:delegate,edgeProperties:[label:edgeLabel])
				return vo
			}
			// <<
			leftShift << {  Enum  enumArg->
				VertexOperation vo = new VertexOperation(direction:IN,vertex:delegate,edgeProperties:[label:enumArg.name()])
				return vo
			}
			// <<
			leftShift << {  Map<String, Object>  edgeProperties->
				VertexOperation vo = new VertexOperation(direction:IN,vertex:delegate,edgeProperties:edgeProperties)
				return vo
			}
			// >>
			rightShift << {  String edgeLabel ->
				VertexOperation vo = new VertexOperation(direction:OUT,vertex:delegate,edgeProperties:[label:edgeLabel])
				return vo
			}
			
			// >>
			rightShift << {  Enum  enumArg->
				VertexOperation vo = new VertexOperation(direction:OUT,vertex:delegate,edgeProperties:[label:enumArg.name()])
				return vo
			}
			// >>
			rightShift << { Map<String, Object>  edgeProperties->
				VertexOperation vo = new VertexOperation(direction:OUT,vertex:delegate,edgeProperties:edgeProperties)
				return vo
			}

			/*methodMissing <<  {String name, args ->
				def method = dynamicMethods.find { it.match(name) }
				if(method) {
					GORM.metaClass."$name" = { Object[] varArgs ->
						method.invoke(delegate, name, varArgs)
					}
					return method.invoke(delegate,name, args)
				}
				else throw new MissingMethodException(name, delegate, args)
			}*/

		}

		VertexOperation.metaClass {
			// <<
			leftShift << {  Vertex vertex ->
				return delegate.createEdgeWithOtherVertex(vertex, OUT)
			}

			// >>
			rightShift << {  Vertex vertex ->
				return delegate.createEdgeWithOtherVertex(vertex, IN)
			}

			// --
			previous << { delegate.removeEdges() }
		}

		SearchOperation.metaClass {
			// <<
			leftShift << {  Enum  enumArg->
				VertexOperation vo = new VertexOperation(direction:IN,vertex:delegate.vertex,edgeProperties:[label:enumArg.name()])
				return vo.searchVerteces(delegate.depth,delegate.closureToApply)
			}
			// <<
			leftShift << {  Map<String, Object>  edgeProperties->
				VertexOperation vo = new VertexOperation(direction:IN,vertex:delegate.vertex,edgeProperties:edgeProperties)
				return vo.searchVerteces(delegate.depth,delegate.closureToApply)
			}
			// >>
			rightShift << {  Enum  enumArg->
				VertexOperation vo = new VertexOperation(direction:OUT,vertex:delegate.vertex,edgeProperties:[label:enumArg.name()])
				return vo.searchVerteces(delegate.depth,delegate.closureToApply)
			}
			// >>
			rightShift << { Map<String, Object>  edgeProperties->
				VertexOperation vo = new VertexOperation(direction:OUT,vertex:delegate.vertex,edgeProperties:edgeProperties)
				return vo.searchVerteces(delegate.depth,delegate.closureToApply)
			}
		}

		Integer.metaClass {
			mod << { Vertex vertex ->
				if(delegate == 0) {
					delegate = Integer.MAX_VALUE
				}
				return new SearchOperation(vertex:vertex,depth:delegate,element:SearchOperation.Element.VERTEX)
			}
		}

		Closure.metaClass {
			mod << { Vertex vertex ->
				return new SearchOperation(vertex:vertex,depth:Integer.MAX_VALUE,element:SearchOperation.Element.VERTEX,closureToApply:delegate)
			}
		}

	}
}
