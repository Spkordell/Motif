/**
 * 
 */
package motif;

/**
 * @author Steven Kordell
 *
 */
public class Prediction {
	
	private String prediction;
	private boolean predictionMet;
	private int associatedPattern;
	private float strength;
	
	public Prediction(String prediction, int associatedPattern) {
		this.prediction = prediction;
		this.associatedPattern = associatedPattern;
		this.predictionMet = false;
	}
	
	public String getPrediction() {
		return this.prediction;
	}
	
	public void met() {
		this.predictionMet = true;
	}
	
	public boolean hasBeenMet() {
		return this.predictionMet;
	}
	
	public int getAssociatedPattern() {
		return this.associatedPattern;
	}

	public float getStrength() {
		return this.strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}
}
