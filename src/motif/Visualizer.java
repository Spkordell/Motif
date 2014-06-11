/**
 * 
 */
package motif;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

/**
 * @author Steve Kordell
 *
 */

public class Visualizer {
	
	private static Visualizer visualizer;
	private Graph<AbstractNode, Integer> g;
	
	private Visualizer() {
	      g = new DirectedSparseGraph<AbstractNode, Integer>();
	}
	
	public static Visualizer getInstance() {
		if (visualizer == null) {
			visualizer = new Visualizer();
		}
		return visualizer;
	}
	
	public Component drawGraph() {
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<AbstractNode, Integer> layout = new SpringLayout<AbstractNode, Integer>(g);
		layout.setSize(new Dimension(400,400)); // sets the initial size of the layout space
		BasicVisualizationServer<AbstractNode,Integer> vv = new BasicVisualizationServer<AbstractNode,Integer>(layout);	
		vv.setPreferredSize(new Dimension(400,400)); //Sets the viewing area size

	    // Setup up a new vertex to paint transformer...
	    Transformer<AbstractNode,Paint> vertexPaint = new Transformer<AbstractNode,Paint>() {
	    	public Paint transform(AbstractNode i) {
	    		return i.getVisualizationColor();
	        }
	    };
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		
		return vv;
	}
	
	
	public Graph<AbstractNode, Integer> getGraph() {
		return this.g;
	}
	
	public void updateGraph(List<AbstractNode> vertexes) {
		for (AbstractNode node : vertexes) {
			this.g.addVertex(node);
		}
		int edgeCount = 0;
		for (AbstractNode node : vertexes) {
			for (AbstractNode aReturn : node.getReturns()) {
				this.g.addEdge(edgeCount++,node,aReturn);
			}
		}
	}
		
}

