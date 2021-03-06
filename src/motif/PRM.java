/**
 * 
 */
package motif;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsat.DataSet;
import jsat.SimpleDataSet;
import jsat.classifiers.DataPoint;
import jsat.clustering.DBSCAN;
import jsat.linear.DenseVector;
import jsat.linear.Vec;

/**
 * @author Steve Kordell
 *
 */
public class PRM extends AbstractNode {	
	private String data;
	private LinkedList<DataPoint> frames;
	private ArrayList<String> patterns; //TODO: contains is used a lot on this object and is probably the limiting factor for performance. Consider using a different data type.
	private LinkedList<Prediction> currentPredictions;
	private LinkedList<Prediction> predictionsFromAbove;
	private boolean allDendritesWereReady;
	
	private DBSCAN dbscan;
	private boolean classiferEnabled;
	private boolean caughtRuntimeException;
	private List<List<DataPoint>> cluster;
	
	private static final String elementRegex = "(\\d+)\\s*"; ; 
	private static final Pattern elementPattern = Pattern.compile(elementRegex);
	
	public PRM() {
		super();
		this.data = "";
		this.patterns = new ArrayList<String>(3000);
		this.currentPredictions = new LinkedList<Prediction>();
		this.dbscan = new DBSCAN();
		this.frames = new LinkedList<DataPoint>();
		this.allDendritesWereReady = false;
		this.caughtRuntimeException = false;
		this.classiferEnabled = true;
	}
	
	public void stepOne() throws TooManyDendritesException {	

		if (this.allDendritesWereReady = allDendritesReady()) {
			this.classify();
			if (!this.caughtRuntimeException) {
				System.out.println("------"+this.data+"------");
				/*
				//Print raw incoming data
				for (DataPoint frame : this.frames) {
					System.out.print(frame.getNumericalValues());
				}
				System.out.println();
			    */
				this.updateOutput();
			}
		}
	}
	
	public void stepTwo() {
		if (this.allDendritesWereReady && !this.caughtRuntimeException) {
			this.findPatterns();
			this.makePredictions();	
		}
	}
	
	
	
	private void classify() throws TooManyDendritesException {
		final int MINPOINTS = 2;
 
		if (this.classiferEnabled) {
			//prepare the incoming data
			LinkedList<Double> dendriteValues = new LinkedList<Double>();			
			for (AbstractNode d : this.getDendrites()) {
				dendriteValues.add((double) d.getAxon());
			}		
			Vec frame = new DenseVector(dendriteValues);
			this.frames.add(new DataPoint(frame,null,null));
			DataSet dataset = new SimpleDataSet(this.frames);
			
			if (this.frames.size() > 2) {
				try {
					int[] designations = dbscan.cluster(dataset,MINPOINTS,(int[])null);
					this.cluster = DBSCAN.createClusterListFromAssignmentArray(designations, dataset);
					StringBuilder buffer = new StringBuilder();
					for (int designation : designations) {				
						buffer.append(designation); buffer.append(' ');
					}	
					this.data = buffer.toString();
					this.caughtRuntimeException = false;
				} catch (RuntimeException e) {
					//This seems to be a bug in the library. Just going to compensate for it
					System.out.println("Exception");
					this.caughtRuntimeException = true;
				}
			}
		} else {
			if (this.getDendrites().size() > 1) {
				throw new TooManyDendritesException();
			} else {
				this.data+=this.getDendrites().getFirst().getAxon()+" ";
			}
		}
	}
	
	
	private void updateOutput() {
		
		final float PREDICTION_FROM_ABOVE_MIN_CONFIDENCE_FOR_LIE = (float) 0.75;
		final float PREDICTION_MIN_MATCH_PERCENTAGE_FOR_LIE = (float) 0.5;
		
	   //set the nodes output to the name of a pattern which has satisfied a prediction
	   //determine if prediction has been satisfied 	   
	   if (!this.currentPredictions.isEmpty() && !anyPredictionMet()) {
		   for (Prediction prediction: this.currentPredictions) {
		 	   if (this.data.endsWith(prediction.getAssociatedPattern()+" ")) {
		 		  this.setAxon(prediction.getAssociatedPatternIndex());
		 		  prediction.hasBeenMet();
		 	 	  System.out.println("Prediction Successs: Node Output = "+this.getAxon());
		 		  break;
		 	   } else {
		 		   String regex;
		 		   Pattern p;
		 		   Matcher matcher;		
		 		   regex = "\\s(";		   
				   int i = 0;
				   while ( (i = prediction.getAssociatedPattern().indexOf(' ',++i)) != -1) {
					   regex+=prediction.getAssociatedPattern().substring(0,i).trim()+"|";
				   }
				   if (regex.endsWith("|")) {
					   regex = regex.substring(0,regex.length()-1);
				   }
				   regex += ")\\s$";
				   p = Pattern.compile(regex);        	        		
				   matcher = p.matcher(this.data);
				   if (!matcher.find()) {
					   //The prediction was wrong, we got something we didn't expect			   
					   prediction.hasFailed();
					   System.out.println("Prediction Failed");
				   }
				   this.setAxon(-1);
				   System.out.println("Node Output = "+this.getAxon());
		 	   }
		   }
		   
	 	   if (this.getAxon() == -1 && !allPredictionsFailed()) {
	 	 	   System.out.println("Waiting for Predictions to be met");	 	 	
	 	 	   for (Prediction prediction: this.currentPredictions) {
	 	 		   System.out.println("Current Prediction: "+prediction.getPrediction() +":  failed = "+prediction.isFailed());
	 	 	   }
	 	 	   //TODO: any time I get another data point, it's posisble a longer prediction could be found that has a higher strength. Might need to find a way to check for that rather thatn jsut waiting for this one to be met	   
	 	   }
	 	   	 	   
	 	  if (this.getAxon() == -1 && allPredictionsFailed()) {
	 		 //at this point, we know there aren't any perfect matches, but perhaps one of them is "close enough" and we can still claim it to have been met even though it technically failed
	 		  
	 		 float largestPartialMatchPercentage = -1; 
	 		 //determine how much each of the predictions matches those from above
	 		 for (Prediction prediction: this.currentPredictions) {	 			 			 
	 	     	for (Prediction predictionFromAbove : predictionsFromAbove) {	
	 	     		if (predictionFromAbove.getConfidence() >= PREDICTION_FROM_ABOVE_MIN_CONFIDENCE_FOR_LIE) {
	 					Matcher levelMatcher = elementPattern.matcher(prediction.getPrediction());
	 					Matcher aboveMatcher = elementPattern.matcher(predictionFromAbove.getPrediction());			
	 					int matchCount = 0;
	 					int misMatchCount = 0;
	 					while(levelMatcher.find() && aboveMatcher.find()) {
	 						if (levelMatcher.group(1).equals(aboveMatcher.group(1))) {
	 							matchCount++;
	 						} else {
	 							misMatchCount++;
	 						}
	 					}
	 					//select the highest
	 					float partialMatchPercentage = (float)matchCount/(matchCount+misMatchCount);
	 					if (partialMatchPercentage > prediction.getPartialMatchPercentage()) {	
	 						prediction.setPartialMatchPercentage(partialMatchPercentage);
	 					}
	 					if (partialMatchPercentage > largestPartialMatchPercentage) {
	 						largestPartialMatchPercentage = partialMatchPercentage;
	 					}
	 	     		}
	 	     	}
	 		 }
	 		 
	 		 if (largestPartialMatchPercentage >= PREDICTION_MIN_MATCH_PERCENTAGE_FOR_LIE) {
		 		 for (Prediction prediction: this.currentPredictions) {
		 			 if (prediction.getPartialMatchPercentage() == largestPartialMatchPercentage) {
				 		  this.setAxon(prediction.getAssociatedPatternIndex());
				 		  prediction.hasBeenMet();
				 	 	  System.out.println("Prediction Successs (By partial Match): Node Output = "+this.getAxon());
				 		  break;
		 			 }
		 		 }
	 		 }
	 	  }
	 	   	  	 	  
	   } else {
		   this.setAxon(-1);
		   System.out.println("Node Output = "+this.getAxon());
	   }
	}

	private void findPatterns() {
    	if (this.currentPredictions.isEmpty() || anyPredictionMet() || allPredictionsFailed()) {
    		//not monitoring any predictions, need to look for new patterns
			String s;
		    for (int i = 2; i < this.data.length()/2; i+=1){
			   /*
			   String regex = "((\\d+\\s){"+i+"}).*\\1";
		       Pattern p = Pattern.compile(regex);
		       Matcher matcher = p.matcher(this.data);
		       */
		       Matcher matcher = PatternSearchRegexCache.getInstance().getRegex(i).matcher(this.data);
		       int j = 0;
		       while (matcher.find(j)) {
		    	   if (!this.patterns.contains(s = matcher.group(1).trim())) {
		    		   this.patterns.add(s);
		    	   }
		    	   j = matcher.start()+1; //j one character past pattern
		    	   j = this.data.indexOf(' ',j); //j is moved to the following space, where the next pattern element is
		       }
		   }
		   //we're done, print all the patterns we found
		   int index = 0;
		   for(String pattern : this.patterns){
			   System.out.println("Pattern "+(index++)+": "+pattern);
		   }
    	}
	}
		
   private LinkedList<Prediction> makePredictions() { 	
    	if (currentPredictions.isEmpty() || anyPredictionMet() || allPredictionsFailed()) {
    		//not monitoring any predictions, search for new ones to monitor
    		
    		//get any information the above PRM has which might be useful
        	getPredictionsFromAbove();
        	
			//make a prediction as to what might come next	 
			String regex;
			Pattern p;
			Matcher matcher;
			LinkedList<Prediction> predictions = new LinkedList<Prediction>();
			for(String pattern : this.patterns) {
				regex = "\\s(";
				int i = 0;
				while ( (i = pattern.indexOf(' ',++i)) != -1) {
					regex+=pattern.substring(0,i).trim()+"|";
				}
				if (regex.endsWith("|")) {
					regex = regex.substring(0,regex.length()-1);
				}
				regex += ")\\s$";
				p = Pattern.compile(regex);        	        		
				matcher = p.matcher(this.data);
				if (matcher.find()) {
					//This pattern matches some part of the end of the input		
					//chop off the portion of the pattern that intersects with the end of the input, leaving just the prediction
					//TODO: allow patterns into the list if they are compatible with "predictions from above"
					
					
					int idx = pattern.length();
					while (!this.data.endsWith(pattern.substring(0, idx--)));

					Prediction prediction = new Prediction(pattern.substring(idx + 1), pattern, this.patterns.indexOf(pattern));					
					prediction.setConfidence(determinePredictionConfidence(this.data, pattern, prediction.getPrediction(), this.predictionsFromAbove));
					
					if (!hasRepeatedPrediction(predictions,prediction)) { //remove repeated predictions
			   	   		predictions.add(prediction);
			      	} else {
			      		//compare strengths and keep the largest	      		
			      		Prediction predictionInList = findPredictionWithValue(predictions,prediction);
			      		if (predictionInList.getConfidence() <  prediction.getConfidence()) {
			      			predictionInList.setConfidence(prediction.getConfidence());
			      		}		      		
			      	}
				}
			}
			
			float largestPredictionConfidence = -1;			
			if (predictions.size() == 0) {
				System.out.println("No predictions");
				this.currentPredictions = new LinkedList<Prediction>();
				return new LinkedList<Prediction>();
			} else {
				//int predictionIndex = 0;
			   	for(Prediction prediction : predictions){
			   		System.out.println("Prediction: "+prediction.getPrediction()+" = "+prediction.getConfidence()*100+"%"+" : " + prediction.getAssociatedPatternIndex());			 
			   		if (prediction.getConfidence() > largestPredictionConfidence) {
			   			largestPredictionConfidence = prediction.getConfidence();
			   		}
			   	}
			}
	
			//Select predictions		
			//TODO: currently sending multiple predictions only if the strengths tie perfectly. Might want to send predictions for close matches too.
			LinkedList<Prediction> selectedPredictions = new LinkedList<Prediction>();		
			for(Prediction prediction : predictions){
				if (prediction.getConfidence() == largestPredictionConfidence) {
					//Remove predictions which which have overlap (want just the longest version of each predicion)
					for (Prediction aPrediction : selectedPredictions) {
						if (prediction.getPrediction().startsWith(aPrediction.getPrediction()+" ")) {
							selectedPredictions.remove(aPrediction);
						}
					}
					selectedPredictions.add(prediction);	
				}	
		   	}
			
			for(Prediction prediction : selectedPredictions){
				System.out.println("Selected Prediction: "+prediction.getPrediction()+" = "+largestPredictionConfidence*100 + "%");
			}
			
			//save a new prediction to watch for
			this.currentPredictions = selectedPredictions;
		}			
		return currentPredictions;
	}
	
	public List<Prediction> getCurrentPredictions(AbstractNode caller) {
		if (this.classiferEnabled) {
			List<Prediction> predictionsToSend = new ArrayList<Prediction>(5);
			for (Prediction prediction : this.currentPredictions) {
				//Convert the prediction into an array of integers
				String[] predictionAsStringArray = prediction.getPrediction().split(" ");
				int[] predictionAsNumArray = new int[predictionAsStringArray.length];
				for (int i = 0; i < predictionAsStringArray.length; i++) {
					predictionAsNumArray[i] = Integer.parseInt(predictionAsStringArray[i]);
				}
				
				//make a string for each dendrite to store that dendrite's predictions
				List<StringBuilder> extractedPrediction = new ArrayList<StringBuilder>();
				for (int d = 0; d < this.getDendrites().size(); d++) {
					extractedPrediction.add(new StringBuilder());
				}
				
				//for every element of the original prediction, find the center point of the cluster. Break this vector into the predicitons for each individual dendrite.
				for (int element : predictionAsNumArray) {		
					//find the center by averaging the points of the corresponding cluster
					Vec center = DenseVector.zeros(this.getDendrites().size());
					for (int d = 0; d < this.getDendrites().size(); d++) {
						for (int i = 0; i < cluster.get(element).size(); i++) {
							center.set(d, center.get(d)+cluster.get(element).get(i).getNumericalValues().get(d));
						}
						center.set(d, center.get(d)/cluster.get(element).size());
					}					

					//break the vector apart into the individual predicitons
					for (int d = 0; d < this.getDendrites().size(); d++) {
						extractedPrediction.get(d).append((int)Math.round(center.get(d)));
						extractedPrediction.get(d).append(' ');
					}
				}
							
				//find which dendrite made the call
				for (int i = 0; i < this.getDendrites().size(); i++) {
					if (this.getDendrites().get(i) == caller) {
						//we found which who wanted the data, we can send back the appropriate set
						//assemble the prediction object to send back
						predictionsToSend.add(new Prediction(extractedPrediction.get(i).toString().trim(), prediction.getConfidence()));
						break;
					}
				}
			}
			return predictionsToSend;			
			//TODO: can perhaps add error bars if we include the std. dev. of the cluster in the prediciton rather than just the average			
		} else {
			return this.currentPredictions;

		}	
	}
	
	private void getPredictionsFromAbove() {
		this.predictionsFromAbove = new LinkedList<Prediction>();
				
		if (this.getReturns().size() > 0) {
			LinkedList<Prediction> unParsedPredictionsFromAbove = new LinkedList<Prediction>();
			for (AbstractNode aReturn : this.getReturns()) {
					unParsedPredictionsFromAbove.addAll(aReturn.getCurrentPredictions(this));
			}
			//sub in the patterns for the predicions				
			for (Prediction prediction : unParsedPredictionsFromAbove) {
				Matcher matcher = elementPattern.matcher(prediction.getPrediction());
				StringBuffer sb = new StringBuffer();
				while(matcher.find()) {
				    matcher.appendReplacement(sb, this.patterns.get(Integer.parseInt(matcher.group(1)))+' ');
				}
				matcher.appendTail(sb);
				System.out.println("Prediction: "+sb.toString()+" = "+prediction.getConfidence()*100+"% : From Above");
				this.predictionsFromAbove.add(new Prediction(sb.toString().trim(),prediction.getConfidence()));
				this.predictionsFromAbove.getLast().setFromAbove(true);
			}
		}
		
	}
	     
	private static float determinePredictionConfidence(String elem, String pattern, String prediction, LinkedList<Prediction> predictionsFromAbove) {
		
		final int PERFECT_MATCH_CURRENT_LEVEL_WEIGHT = 1;
		final int PERFECT_MATCH_FROM_ABOVE_WEIGHT = 2;
		
		final int SHIFT_MATCH_CURRENT_LEVEL_WEIGHT = 1;
		final float SHIFT_MATCH_FROM_ABOVE_WEIGHT = (float) 1.5;
		
    	//count number of times pattern appears
    	int patternCount = elem.split("(?="+pattern+")").length-1; //The lookahead allows the split to capture overlaps
    	
    	//count number of times prediction would have been wrong	 
     	int patternMissedCount = (elem.split("(?="+pattern.substring(0,pattern.length()-prediction.length()-1)+")").length-1)-patternCount; //The lookahead allows the split to capture overlaps
     	//subtract 1 from missed count if input string ends in the partial pattern (We can't miss a pattern we haven't fully seen yet)    	
     	if (elem.endsWith(pattern.substring(0,pattern.length()-prediction.length()-1)+" ")) {
     		patternMissedCount-=1;
     	}    	
    	float predictionConfidence = 1-((float)patternMissedCount/(patternCount+patternMissedCount));

    	
     	//Take into account any information provided by above predictions by adjusting the confidence accordingly  
     	for (Prediction predictionFromAbove : predictionsFromAbove) {
     		if (prediction.startsWith(predictionFromAbove.getPrediction()) || predictionFromAbove.getPrediction().startsWith(prediction)) {
     			//A weighted average between the confidece of this prediction and the confidence of the predictions from above with those from above having more weight
     			System.out.println("prediction "+prediction+" modified by perfect match from "+ predictionConfidence + " to "+ (PERFECT_MATCH_CURRENT_LEVEL_WEIGHT*predictionConfidence + PERFECT_MATCH_FROM_ABOVE_WEIGHT*predictionFromAbove.getConfidence())/(PERFECT_MATCH_CURRENT_LEVEL_WEIGHT+PERFECT_MATCH_FROM_ABOVE_WEIGHT));
     			predictionConfidence = (PERFECT_MATCH_CURRENT_LEVEL_WEIGHT*predictionConfidence + PERFECT_MATCH_FROM_ABOVE_WEIGHT*predictionFromAbove.getConfidence())/(PERFECT_MATCH_CURRENT_LEVEL_WEIGHT+PERFECT_MATCH_FROM_ABOVE_WEIGHT);
     		} else if (prediction.contains(predictionFromAbove.getPrediction()) || predictionFromAbove.getPrediction().contains(prediction)) {
     			System.out.println("prediction "+prediction+" modified by shift from " + predictionConfidence + " to " + (SHIFT_MATCH_CURRENT_LEVEL_WEIGHT*predictionConfidence + SHIFT_MATCH_FROM_ABOVE_WEIGHT*predictionFromAbove.getConfidence())/(SHIFT_MATCH_CURRENT_LEVEL_WEIGHT+SHIFT_MATCH_FROM_ABOVE_WEIGHT));
     			predictionConfidence = (SHIFT_MATCH_CURRENT_LEVEL_WEIGHT*predictionConfidence + SHIFT_MATCH_FROM_ABOVE_WEIGHT*predictionFromAbove.getConfidence())/(SHIFT_MATCH_CURRENT_LEVEL_WEIGHT+SHIFT_MATCH_FROM_ABOVE_WEIGHT);
     		} else {     						
				Matcher levelMatcher = elementPattern.matcher(prediction);
				Matcher aboveMatcher = elementPattern.matcher(predictionFromAbove.getPrediction());			
				int matchCount = 0;
				int misMatchCount = 0;
				while(levelMatcher.find() && aboveMatcher.find()) {
					if (levelMatcher.group(1).equals(aboveMatcher.group(1))) {
						matchCount++;
					} else {
						misMatchCount++;
					}
				}
				float matchPercentage = (float)matchCount/(matchCount+misMatchCount);
				if (predictionConfidence != (float) ((PERFECT_MATCH_CURRENT_LEVEL_WEIGHT*predictionConfidence + matchPercentage*PERFECT_MATCH_FROM_ABOVE_WEIGHT*predictionFromAbove.getConfidence())/(PERFECT_MATCH_CURRENT_LEVEL_WEIGHT+matchPercentage*PERFECT_MATCH_FROM_ABOVE_WEIGHT))) {
					System.out.println("prediction "+prediction+" modified by partial from "+ predictionConfidence + " to "+ (float) ((PERFECT_MATCH_CURRENT_LEVEL_WEIGHT*predictionConfidence + matchPercentage*PERFECT_MATCH_FROM_ABOVE_WEIGHT*predictionFromAbove.getConfidence())/(PERFECT_MATCH_CURRENT_LEVEL_WEIGHT+matchPercentage*PERFECT_MATCH_FROM_ABOVE_WEIGHT)));
				}
	     		predictionConfidence = (float) ((PERFECT_MATCH_CURRENT_LEVEL_WEIGHT*predictionConfidence + matchPercentage*PERFECT_MATCH_FROM_ABOVE_WEIGHT*predictionFromAbove.getConfidence())/(PERFECT_MATCH_CURRENT_LEVEL_WEIGHT+matchPercentage*PERFECT_MATCH_FROM_ABOVE_WEIGHT));
     		}
     	}    	
    	return predictionConfidence;
	}

	
	private Prediction findPredictionWithValue(LinkedList<Prediction> predictions, Prediction prediction) {
    	for (Prediction aPrediction: predictions) {
    		if (aPrediction.getPrediction().equals(prediction.getPrediction())) {
    			return aPrediction;
    		}
    	}
    	return null;
	}

	private boolean hasRepeatedPrediction(LinkedList<Prediction> predictions, Prediction prediction) {
    	for (Prediction aPrediction: predictions) {
    		if (aPrediction.getPrediction().equals(prediction.getPrediction())) {
    			return true;
    		}
    	}
    	return false;
    }
	
	private boolean allDendritesReady() {
		for (AbstractNode d: this.getDendrites()) {
			if (d.checkAxon() == -1) {
				return false;
			}
		}
		return true;
	}
	
    private boolean anyPredictionMet() {
    	for (Prediction prediction: this.currentPredictions) {
    		if (prediction.isMet()) {
    			return true;
    		}
    	}
    	return false;
	}
    
    private boolean allPredictionsFailed() {
    	for (Prediction prediction: this.currentPredictions) {
    		if (!prediction.isFailed()) {
    			return false;
    		}
    	}
    	return true;
	}
    
	public boolean isClassiferEnabled() {
		return classiferEnabled;
	}

	public void setClassiferEnabled(boolean classiferDisabled) {
		this.classiferEnabled = classiferDisabled;
	}
	
	public Paint getVisualizationColor() {
		return Color.blue;
	}
}
