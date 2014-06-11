package motif;

import java.awt.*;

import javax.swing.*;

public class Main {
	private static JFrame frame;
	private static JPanel mainPanel;
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
    	mainPanel = new JPanel(new GridLayout(1, 1));    	
    	//Create and set up the window.
        frame = new JFrame("Ipsum");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(mainPanel,BorderLayout.CENTER);      

        //Display the window.
        frame.setPreferredSize(new Dimension(500,500));
        frame.pack();
        frame.setVisible(true);
    	  	
    	mainPanel.add(Visualizer.getInstance().drawGraph());
    }
    
	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        
    	Network network = new Network();
    	
    	try {
    		network.run();
    	} catch (TooManyDendritesException e) {
    		e.printStackTrace();
    	}
    }

	/*
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
	*/
}
