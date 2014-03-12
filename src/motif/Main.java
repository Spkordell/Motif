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
        
        
        
        /*        
        String arr[] = {"12341234abc", "1234foo1234", "12121212"};       
        String regex = "(\\d+?)\\1";
        Pattern p = Pattern.compile(regex);
        for (String elem : arr) {
           Matcher matcher = p.matcher(elem);
           if (matcher.find())
              System.out.println(elem + " got repeated: " + matcher.group(1));
           else
              System.out.println(elem + " has no repeation");
        }
        */
    	
    	//String regex = "([\\d\\s]+?)\\1";
    	//String regex = "([\\d\\s]+?).{"+i+"}\\s\\1";
/*    	
        String arr[] = {"1 2 3 4 1 2 3 4 ", "1 2 1 2 1 2 1 2 ", "1 1 2 1 1 3 1 1 2 1 1 2 "};    
        for (String elem : arr) {
	        System.out.println("------"+elem+"------");
        	for (int i = 2; i < elem.length(); i+=2){
		       String regex = "([\\d\\s]{"+i+"}?).*\\1";
		       Pattern p = Pattern.compile(regex);
	           Matcher matcher = p.matcher(elem);
	           if (matcher.find()) {
	              System.out.println("Pattern " + i/2 +": " + matcher.group(1));
	           	  //We found one match, let's look for others
	              for (int j = 2; j < elem.length(); j+=2) {
	            	  regex = ".{"+j+"}([\\d\\s]{"+i+"}?).*\\1";
	   		       	  p = Pattern.compile(regex);
	   		       	  matcher = p.matcher(elem);
	   		       	  if (matcher.find()) {
	   		       		  System.out.println("Pattern+ " + i/2 +": " + matcher.group(1));
	   		       	  } else {
	   		       		  break;
	   		       	  }
	              }
	           } else {
	              System.out.println("No more patterns");
	           	  break;
	           }
	        }
        }
        */
    
    	
        String arr[] = {"1 2 3 4 1 2 3 4 ", "1 2 1 2 1 2 1 2 ", " 1 1 2 1 1 3 1 1 2 1 1 2 "};
        LinkedList<String> patterns;
        for (String elem : arr) {
        	patterns = new LinkedList<String>();
	        System.out.println("------"+elem+"------");
        	for (int i = 2; i < elem.length(); i+=2){
		       String regex = "([\\d\\s]{"+i+"}?).*\\1";
		       Pattern p = Pattern.compile(regex);
	           Matcher matcher = p.matcher(elem);
	           if (matcher.find()) {
	        	  patterns.add(matcher.group(1));
	              //System.out.println("Pattern " + i/2 +": " + matcher.group(1));
	           	  //We found one match, let's look for others
	              for (int j = 2; j < elem.length(); j+=2) {
	            	  regex = ".{"+j+"}([\\d\\s]{"+i+"}?).*\\1";
	   		       	  p = Pattern.compile(regex);
	   		       	  matcher = p.matcher(elem);
	   		       	  if (matcher.find() && !patterns.contains(matcher.group(1))) {
	   		       		  patterns.add(matcher.group(1));
	   		       		  //System.out.println("Pattern+ " + i/2 +": " + matcher.group(1));
	   		       	  } else {
	   		       		  break;
	   		       	  }
	              }
	           } else {
	              //System.out.println("No more patterns");
	        	   
	        	  //print all the patterns we found
	        	   for(String pattern : patterns){
	        		   System.out.println("Pattern: "+pattern);
	        	   }
	           	  break;
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