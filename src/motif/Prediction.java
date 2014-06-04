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
	private String associatedPattern;
	private int associatedPatternIndex;
	private float strength;
	private boolean failed;
	
	public Prediction(String prediction, String associatedPattern, int associatedPatternIndex) {
		this.prediction = prediction;
		this.associatedPattern = associatedPattern;
		this.associatedPatternIndex = associatedPatternIndex;
		this.predictionMet = false;
		this.failed = false;
	}
	
	public String getPrediction() {
		return this.prediction;
	}
	
	public void hasBeenMet() {
		this.predictionMet = true;
	}
	
	public boolean isMet() {
		return this.predictionMet;
	}
	
	public String getAssociatedPattern() {
		return this.associatedPattern;
	}
	
	public int getAssociatedPatternIndex() {
		return this.associatedPatternIndex;
	}

	public float getStrength() {
		return this.strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public void hasFailed() {
		this.failed = true;
	}
	
	public boolean isFailed() {
		return this.failed;
	}
	
}
