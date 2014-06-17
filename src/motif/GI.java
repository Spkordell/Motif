/**
 * 
 */
package motif;

import java.awt.Color;
import java.awt.Paint;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Global Input Class (Feeds data into nodes)
 * @author Steve Kordell
 *
 */
public class GI extends AbstractNode {

	private Queue<Integer> data;
	public GI() {
		super();
		this.data = new LinkedList<Integer>();
	}
	
	public void addData(int item) {
		this.data.add(item);
	}

	public void addData(int[] data) {
		for (int item : data) {
			this.data.add(item);
		}
	}
	
	public void stepOne() {
		try {
			this.setAxon(this.data.poll());
		} catch (NullPointerException e) {
			this.setAxon(-1);
		}
	}
	
	/*
	public int getAxon() {
		return this.data.poll();
	}*/
	
	protected int checkAxon() {
		/*if (this.data.peek() != null) {
			return this.data.peek();
		} else {
			return -1;
		}*/
		return this.getAxon();
	}

	public boolean isEmpty() {
		return this.data.isEmpty();
	}
	
	public Paint getVisualizationColor() {
		return Color.green;
	}

	public Prediction getBestPrediction() {
		float maxConfidence = -1;
		Prediction bestPrediction = null;
		for (Prediction prediction : this.getReturns().getFirst().getCurrentPredictions(this)) {
			if (prediction.getConfidence() > maxConfidence) {
				maxConfidence = prediction.getConfidence();
				bestPrediction = prediction;
			}
		}
		return bestPrediction;
	}
}
