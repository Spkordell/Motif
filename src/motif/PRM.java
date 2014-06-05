/**
 * 
 */
package motif;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Steve Kordell
 *
 */
public class PRM extends AbstractNode {

	private String data;
	private LinkedList<String> patterns;
	private LinkedList<Prediction> currentPredictions;

	public PRM() {
		super();
		this.data = "";
		this.patterns = new LinkedList<String>();
	}
	
	public LinkedList<Prediction> step() {
		this.data+=this.getDendrites().getFirst().getAxon()+" "; //todo: for now only using the first input, heavy modifications will be needed to work with multiple inputs
		return this.mineSequentialPatterns();
	}
	
	private LinkedList<Prediction> mineSequentialPatterns() {	
        System.out.println("------"+this.data+"------");
        this.updateOutput();
        this.findPatterns();
		return makePrediction();
	}
    
    private LinkedList<Prediction> makePrediction() {
    	
    	if (currentPredictions == null || anyPredictionMet() || allPredictionsFailed()) {
    		//not monitoring any predictions, search for new ones to monitor
	    		
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
					int idx = pattern.length();
					while (!this.data.endsWith(pattern.substring(0, idx--)));
					Prediction prediction = new Prediction(pattern.substring(idx + 1), pattern, this.patterns.indexOf(pattern));
					
					prediction.setStrength(determinePredictionStrength(this.data, pattern, prediction.getPrediction()));
					
					if (!hasRepeatedPrediction(predictions,prediction)) { //remove repeated predictions
			   	   		predictions.add(prediction);
			      	} else {
			      		//compare strengths and keep the largest	      		
			      		Prediction predictionInList = findPredictionWithValue(predictions,prediction);
			      		if (predictionInList.getStrength() <  prediction.getStrength()) {
			      			predictionInList.setStrength(prediction.getStrength());
			      		}		      		
			      	}
				}
			}
			
			float largestPredictionStrength = -1;			
			if (predictions.size() == 0) {
				System.out.println("No predicions");
				return new LinkedList<Prediction>();
			} else {
				//int predictionIndex = 0;
			   	for(Prediction prediction : predictions){
			   		System.out.println("Prediction: "+prediction.getPrediction()+" = "+prediction.getStrength()*100+"%"+" : "+prediction.getAssociatedPatternIndex());
			   		if (prediction.getStrength() > largestPredictionStrength) {
			   			largestPredictionStrength = prediction.getStrength();
			   		}
			   	}
			}
	
			//Select predictions		
			//TODO: currently sending multiple predictions only if the strengths tie perfectly. Might want to send predictions for close matches too.
			LinkedList<Prediction> selectedPredictions = new LinkedList<Prediction>();		
			for(Prediction prediction : predictions){
				if (prediction.getStrength() == largestPredictionStrength) {
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
				System.out.println("Selected Prediction: "+prediction.getPrediction()+" = "+largestPredictionStrength*100 + "%");
			}
		
		
			//save a new prediction to watch for
			this.currentPredictions = selectedPredictions;
		}			
		return currentPredictions;
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

	
	private void updateOutput() {
	   //set the nodes output to the name of a pattern which has satisfied a prediction
	   //determine if prediction has been satisfied 	   
	   if (currentPredictions != null && !anyPredictionMet()) {
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
	 	   if (!allPredictionsFailed() && this.getAxon() == -1) {
	 	 	   System.out.println("Waiting for Predictions to be met");
	 	 	   
	 	 	   for (Prediction prediction: this.currentPredictions) {
	 	 		   System.out.println("Current Prediction: "+prediction.getPrediction() +":  failed? = "+prediction.isFailed());
	 	 	   }
	 	 	   //TODO: any time I get another data point, it's posisble a longer predcition could be found that has a higher strength. Might need to find a way to check for that rather thatn jsut waiting for this one to be met	   
	 	   }
	   } else {
		   this.setAxon(-1);
		   System.out.println("Node Output = "+this.getAxon());
	   }
	}
	
	
	private void findPatterns() {
    	if (currentPredictions == null || anyPredictionMet() || allPredictionsFailed()) {
    		//not monitoring any predictions, need to look for new patterns
			String s;
		    for (int i = 2; i < this.data.length()/2; i+=1){
			   String regex = "((\\d+\\s){"+i+"}).*\\1";
		       Pattern p = Pattern.compile(regex);
		       Matcher matcher = p.matcher(this.data);
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
   
	@SuppressWarnings("unused")
	private static int countElements(String string) {
		return string.split(" ").length;
	}
	
	private static float determinePredictionStrength(String elem, String pattern, String prediction) {
    	//count number of times pattern appears
    	int patternCount = elem.split("(?="+pattern+")").length-1; //The lookahead allows the split to capture overlaps
    	
    	//count number of times prediction would have been wrong	 
     	int patternMissedCount = (elem.split("(?="+pattern.substring(0,pattern.length()-prediction.length()-1)+")").length-1)-patternCount; //The lookahead allows the split to capture overlaps
     	//subtract 1 from missed count if input string ends in the partial pattern (We can't miss a pattern we haven't fully seen yet)    	
     	if (elem.endsWith(pattern.substring(0,pattern.length()-prediction.length()-1)+" ")) {
     		patternMissedCount-=1;
     	}
     	
     	float predictionStrength = 1-((float)patternMissedCount/(patternCount+patternMissedCount));
    	//System.out.println(pattern+": "+patternCount+", "+pattern.substring(0,pattern.length()-prediction.length()-1)+": "+patternMissedCount+" = "+predictionStrength*100+"%");
    	
    	return predictionStrength;
	}
	
}
