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

	public PRM() {
		super();
		this.data = "";
	}
		
	/*
	 * Takes a single frame, returns a string of predictions
	 */
	public String step(int frame) {
		this.data+=frame+" ";
		return this.mineSequentialPatterns();
	}
	
	public String step() {
		this.data+=this.getDendrites().getFirst().getAxon()+" "; //todo: for now only using the first input, heavy modifications will be needed to work with multiple inputs
		return this.mineSequentialPatterns();
	}
	
	private String mineSequentialPatterns() {	
        System.out.println("------"+this.data+"------");
		return makePrediction(this.findPatterns());
	}
    
    private String makePrediction(LinkedList<String> patterns) {
		//make a prediction as to what might come next	 
		String regex;
		Pattern p;
		Matcher matcher;
		LinkedList<String> predictions = new LinkedList<String>();
		LinkedList<Float> predictionStrengths = new LinkedList<Float>();
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
				String prediction = pattern.substring(idx + 1);
	      	
		      	Float predictionStrength = determinePredictionStrength(this.data, pattern, prediction);
		      	
		      	if (!predictions.contains(prediction)) { //remove repeated predictions
		   	   		predictions.add(prediction);
		   	   		predictionStrengths.add(predictionStrength);//The prediction strength is equal to the strength of the pattern the prediction came from
		      	} else {
		      		//compare strengths and keep the largest
		      		int predictionIndex = predictions.indexOf(prediction);
		      		if (predictionStrengths.get(predictionIndex) <  predictionStrength) {
		      			predictionStrengths.set(predictionIndex, predictionStrength);
		      		}
		      	}
			}
		}
		float largestPredictionStrength = -1;
		
		if (predictions.size() == 0) {
			System.out.println("No predicions");
			return "-1";
		} else {
			int predictionIndex = 0;
		   	for(String prediction : predictions){
		   		System.out.println("Prediction: "+prediction+" = "+predictionStrengths.get(predictionIndex)*100+"%");
		   		if (predictionStrengths.get(predictionIndex) > largestPredictionStrength) {
		   			largestPredictionStrength = predictionStrengths.get(predictionIndex);
		   		}
		   		predictionIndex++;
		   	}
		}

		//Select a prediction (first, find prediction with highest accuracy. Break ties by selecting prediction with most this.dataents.
		int predictionIndex = 0;
		int largestPredictionLength = -1;
		String selectedPrediction = null;
		for(String prediction : predictions){
			int predictionLength = countElements(prediction);
	   		if (predictionStrengths.get(predictionIndex) == largestPredictionStrength && predictionLength > largestPredictionLength) {
	   			selectedPrediction = prediction;
	   			largestPredictionLength = predictionLength;
	   		}
	   		predictionIndex++;
	   	}
		System.out.println("Selected Prediction: "+selectedPrediction+" = "+largestPredictionStrength*100 + "%");
		return selectedPrediction;
	}

	private LinkedList<String> findPatterns() {
    	LinkedList<String> patterns = new LinkedList<String>();;
	
        for (int i = 2; i < this.data.length()/2; i+=1){
    	   String regex = "((\\d+\\s){"+i+"}).*\\1";
	       Pattern p = Pattern.compile(regex);
           Matcher matcher = p.matcher(this.data);
           if (matcher.find()) {
        	  patterns.add(matcher.group(1).trim());
        	  int j = 0;
        	  do {
	        	  j = this.data.indexOf(' ',++j);
        		  if (matcher.find(j) && !patterns.contains(matcher.group(1).trim())) {
   		       		  patterns.add(matcher.group(1).trim());
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
 	   
 	   //set the nodes output to the name of the index of the last pattern which matches
 	   //todo: problems will probably occur here when data is delivered to the node element by element as the output might fire more than necessary.

 	   if (patterns.size() > 0 && this.data.endsWith(patterns.getLast()+" ")) {  
 		   this.setAxon(index-1);
 	   } else {
 		   this.setAxon(-1);
 	   }
 	   
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
