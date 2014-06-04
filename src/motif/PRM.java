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
	private Prediction currentPrediction;

	public PRM() {
		super();
		this.data = "";
		this.patterns = new LinkedList<String>();
	}
	
	public Prediction step() {
		this.data+=this.getDendrites().getFirst().getAxon()+" "; //todo: for now only using the first input, heavy modifications will be needed to work with multiple inputs
		return this.mineSequentialPatterns();
	}
	
	private Prediction mineSequentialPatterns() {	
        System.out.println("------"+this.data+"------");
		return makePrediction(this.findPatterns());
	}
    
    private Prediction makePrediction(LinkedList<String> patterns) {
		//make a prediction as to what might come next	 
		String regex;
		Pattern p;
		Matcher matcher;
		LinkedList<Prediction> predictions = new LinkedList<Prediction>();		
		for(String pattern : patterns) {
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
				Prediction prediction = new Prediction(pattern.substring(idx + 1), pattern, patterns.indexOf(pattern));
				
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
			return new Prediction("-1","-1",-1);
		} else {
			//int predictionIndex = 0;
		   	for(Prediction prediction : predictions){
		   		System.out.println("Prediction: "+prediction.getPrediction()+" = "+prediction.getStrength()*100+"%"+" : "+prediction.getAssociatedPattern());
		   		if (prediction.getStrength() > largestPredictionStrength) {
		   			largestPredictionStrength = prediction.getStrength();
		   		}
		   	}
		}

		//Select a prediction (first, find prediction with highest accuracy. Break ties by selecting longest prediction. TODO: instead of breaking ties, set multiple possible predictions.
		int largestPredictionLength = -1;
		Prediction selectedPrediction = null;
		for(Prediction prediction : predictions){
			int predictionLength = countElements(prediction.getPrediction());
				if (prediction.getStrength() == largestPredictionStrength && predictionLength > largestPredictionLength) {
	   			selectedPrediction = prediction;
	   			largestPredictionLength = predictionLength;
	   		}
	   	}
		System.out.println("Selected Prediction: "+selectedPrediction.getPrediction()+" = "+largestPredictionStrength*100 + "%");
		if (currentPrediction == null || currentPrediction.isMet() || currentPrediction.isFailed()) {
			this.currentPrediction = selectedPrediction;
		}
		return selectedPrediction;
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

	private LinkedList<String> findPatterns() {

        for (int i = 2; i < this.data.length()/2; i+=1){
    	   String regex = "((\\d+\\s){"+i+"}).*\\1";
	       Pattern p = Pattern.compile(regex);
           Matcher matcher = p.matcher(this.data);
           if (matcher.find()) {
        	  if (!patterns.contains(matcher.group(1).trim())) {
        		  patterns.add(matcher.group(1).trim());
        	  }
        	  int j = 0;
        	  do {
	        	  j = this.data.indexOf(' ',++j);
        		  if (matcher.find(j) && !patterns.contains(matcher.group(1).trim())) {
   		       		  this.patterns.add(matcher.group(1).trim());
   		       	  }        	  
        	  } while (j < this.data.length()-i); //todo: this can probably end sooner
           } else {
        	  //we won't find anymore patterns, leave loop early
           	  break;
           }
       }
  	   //we're done, print all the patterns we found
 	   int index = 0;
 	   for(String pattern : patterns){
 		   System.out.println("Pattern "+(index++)+": "+pattern);
 	   }
 	   
 	   //set the nodes output to the name of a pattern which has satisfied a prediction

 	   //determine if prediction has been satisfied 
 	   
 	   
 	   if (currentPrediction != null && !currentPrediction.isMet()) {
	 	   if (this.data.endsWith(currentPrediction.getAssociatedPattern()+" ")) {
	 		  this.setAxon(currentPrediction.getAssociatedPatternIndex());
	 		  currentPrediction.hasBeenMet();
	 	   } else {
	 		   String regex;
	 		   Pattern p;
	 		   Matcher matcher;		
	 		   regex = "\\s(";		   
			   int i = 0;
			   while ( (i = currentPrediction.getAssociatedPattern().indexOf(' ',++i)) != -1) {
				   regex+=currentPrediction.getAssociatedPattern().substring(0,i).trim()+"|";
			   }
			   if (regex.endsWith("|")) {
				   regex = regex.substring(0,regex.length()-1);
			   }
			   regex += ")\\s$";
			   p = Pattern.compile(regex);        	        		
			   matcher = p.matcher(this.data);
			   if (!matcher.find()) {
				   //The prediction was wrong, we got something we didn't expect
				   currentPrediction.hasFailed();
				   System.out.println("Prediction Failed");
			   }
			   this.setAxon(-1);
	 	   }
 	   } else {
 		   this.setAxon(-1);
 	   }
 	   System.out.println("Prediction Successs: Node Output = "+this.getAxon());
 	   
 	   //need to add prediction fail test
 	   
 	   return patterns;
	}
   
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
