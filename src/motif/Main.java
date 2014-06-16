package motif;

import games.pong.Pong;

public class Main {
	//private static JFrame frame;
	//private static JPanel mainPanel;
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
    	/*
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
    	*/
    	new acme.MainFrame(new Pong(), null, 400, 400);
    }
    
    private static Network createNetwork() { 
    	/*
    	int[] testData1 = {1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1};
    	int[] testData2 = {6, 0, 4, 0, 7, 8, 2, 3, 1, 0, 6, 0, 8, 7, 3, 9, 8, 0, 6, 0, 1, 4, 8, 9, 9, 0, 1, 0, 6, 7, 4, 3, 2, 0, 6, 0, 9, 9, 4, 3, 6, 0}; 
    	//int[] testData3 = {5, 3, 5, 7, 6, 0, 4, 0, 7, 8, 2, 3, 1, 0, 6, 0, 8, 7, 3, 9, 2, 0, 6, 0, 1, 4, 8, 9, 2, 0, 1, 0, 6, 7, 4, 3, 2, 0, 6, 0, 9, 9, 4, 3, 6, 0, 3};
	    	
       	Network.getInstance().addInput(new GI()).addData(testData1);
       	Network.getInstance().addInput(new GI()).addData(testData2);
       	//network.addInput(new GI()).addData(testData3); 	
		*/
    	
       	Network.getInstance().addInput(new GI());
       	Network.getInstance().addInput(new GI());
		
       	Network.getInstance().expand();
       	
		return Network.getInstance();
    }
    
	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        
        //Create and start the network
    	try {
    		createNetwork().run();
    	} catch (TooManyDendritesException e) {
    		e.printStackTrace();
    	}
    }
}
