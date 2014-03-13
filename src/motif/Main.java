package motif;

import java.awt.*;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;


public class Main {
	private static JFrame frame;
	private static JPanel mainPanel;
	private static LinkedList<Component> cList;
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
    	cList = new LinkedList<Component>();
    	//mainPanel = new JPanel(new GridLayout(1, 1));    	
        //Create and set up the window.
       // frame = new JFrame("Ipsum");
       // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // frame.setLayout(new BorderLayout());
       // frame.add(mainPanel,BorderLayout.CENTER);      
           
        
        //Display the window.
      //  frame.setPreferredSize(new Dimension(700,700));
       // frame.pack();
       // frame.setVisible(true);
        
	
        String arr[] = {"1 2 3 4 1 2 3 4 1 2 3 4 ", "1 2 1 2 1 2 1 2 ", "1 1 2 1 1 3 1 1 2 1 1 3 1 1 2 ", "111 110 2 112 111 110 2 112 "};
        //String arr[] = {"1 2 3 4 1 2 3 4 1 "};
        LinkedList<String> patterns;
        for (String elem : arr) {
        	patterns = new LinkedList<String>();
	        System.out.println("------"+elem+"------");
        	//for (int i = 4; i < elem.length(); i+=2){
	        for (int i = 2; i < elem.length()/2; i+=1){
		       //String regex = "([\\d\\s]{"+i+"}?).*\\1";
        	   String regex = "((\\d+\\s){"+i+"}).*\\1";
		       Pattern p = Pattern.compile(regex);
	           Matcher matcher = p.matcher(elem);
	           if (matcher.find()) {
	        	  patterns.add(matcher.group(1).trim());
	        	  int j = 0;
	        	  do {
		        	  j = elem.indexOf(' ',++j);
	        		  if (matcher.find(j) && !patterns.contains(matcher.group(1).trim())) {
	   		       		  patterns.add(matcher.group(1).trim());
	   		       	  }        	  
	        	  } while (j < elem.length()-i); //todo: this can probably end sooner
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
     	   
        	//make a prediction as to what might come next
     	    //There is probably a better way to do this without using regular expressions
        	//need to iterate over every pattern, and see if the end of the string fits
     
     	   /*
     	  LinkedList<String> predictions = new LinkedList<String>();
     	  for(String pattern : patterns) {
     		  for(int i = 2; i < pattern.length(); i+=2){
     			  if (elem.endsWith(pattern.substring(0,pattern.length()-i))) {
     				  predictions.add(pattern);
     				  break;
     			  }
     		   }
     	  }
     	  */
     	        	 
        	String regex;
        	Pattern p;
        	Matcher matcher;
        	LinkedList<String> predictions = new LinkedList<String>();
        	for(String pattern : patterns) {
        		regex = "(";
        		for(int i = 2; i < pattern.length(); i+=2){
        			regex+=pattern.substring(0,pattern.length()-i).trim()+"|";
        		}
        		if (regex.endsWith("|")) {
        			regex = regex.substring(0,regex.length()-1);
        		}
        		regex += ")\\s$";
        		System.out.println(regex);
        		p = Pattern.compile(regex);        	        		
        		matcher = p.matcher(elem);
        		if (matcher.find()) {
        			//This pattern matches some part of the end of the input

        			//chop off the portion of the pattern that intersects with the end of the input, leaving just the prediction
        			int idx = pattern.length();
                 	while (!elem.endsWith(pattern.substring(0, idx--)));
                 	String prediction = pattern.substring(idx + 1);
                 	if (!predictions.contains(prediction)) { //remove repeated predictions
              	   		predictions.add(prediction);
                 	}
        		}
        	}
        	
        	if (predictions.size() == 0) {
        		System.out.println("No predicions");
        	} else {
	      	   	for(String pattern : predictions){
	      	   		System.out.println("Prediction: "+pattern);
	      	   	}
        	}
        }
        
    	
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

	public static JPanel getMainPanel() {
		return mainPanel;
	}
	
	public static void add(Component component) {
		cList.add(component);
		getMainPanel().setLayout(new GridLayout(cList.size(), 1));
		for(Component comp : cList) {
			getMainPanel().add(comp);
		}
	}
}