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
	private float confidence;
	private boolean failed;
	private boolean fromAbove;
	private float partialMatchPercentage;
	
	public Prediction(String prediction, String associatedPattern, int associatedPatternIndex) {
		this.prediction = prediction;
		this.associatedPattern = associatedPattern;
		this.associatedPatternIndex = associatedPatternIndex;
		this.predictionMet = false;
		this.failed = false;
		this.setFromAbove(false);
		this.partialMatchPercentage = -1;
	}
	
	public Prediction(String prediction, float confidence) {
		this.prediction = prediction;
		this.confidence = confidence;
		this.predictionMet = false;
		this.failed = false;
		this.setFromAbove(false);
		this.partialMatchPercentage = -1;
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

	public float getConfidence() {
		return this.confidence;
	}

	public void setConfidence(float strength) {
		this.confidence = strength;
	}

	public void hasFailed() {
		this.failed = true;
	}
	
	public boolean isFailed() {
		return this.failed;
	}
	
	public String toString() {
		return this.prediction;
	}

	public boolean isFromAbove() {
		return fromAbove;
	}

	public void setFromAbove(boolean fromAbove) {
		this.fromAbove = fromAbove;
		if (this.fromAbove == true) {
			this.associatedPatternIndex = -1;
		}
	}

	public float getPartialMatchPercentage() {
		return partialMatchPercentage;
	}

	public void setPartialMatchPercentage(float partialMatchPercentage) {
		this.partialMatchPercentage = partialMatchPercentage;
	}
}
