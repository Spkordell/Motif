package ipsum;
import games.pong.Pong;
import ipsum.exceptions.notEnoughPRMsException;
import ipsum.gifunctions.GIPongFunction;
import ipsum.gifunctions.GITestFunctionRandom;
import ipsum.gofunctions.GOPongFunction;
import ipsum.interfaces.GIFunction;
import ipsum.interfaces.GOFunction;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.LinkedList;

import javax.swing.*;


/*TODO list
 *   
 *   
 * train towards a directive
 * Add live parameter control panel to allow user to adjust parameters on the fly
 * 
 * Fix exceptions in PRM (uncomment the stack-trace print in the catch to see what I mean), same problem occurs in the optimization function
 */

public class Main {
	private static JFrame frame;
	private static JPanel mainPanel;
	private static LinkedList<Component> cList;
	private static JLabel stepsPerSecondLabel;
	private static double stepsPerSecond;
	private static int stepCount;
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
    	cList = new LinkedList<Component>();
    	mainPanel = new JPanel(new GridLayout(1, 1));    	
        //Create and set up the window.
        frame = new JFrame("Ipsum");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(mainPanel,BorderLayout.CENTER);      
        stepsPerSecondLabel = new JLabel("sps: "+stepsPerSecond);
        stepsPerSecondLabel.setSize(new Dimension(100,10));        
        frame.add(stepsPerSecondLabel,BorderLayout.SOUTH);
        stepCount = 0;
        
        
        LinkedList<GIFunction> giPongFunctions = new LinkedList<GIFunction>();
        giPongFunctions.add(new GIPongFunction(1));
        giPongFunctions.add(new GIPongFunction(2));  
        giPongFunctions.add(new GIPongFunction(3));
        giPongFunctions.add(new GIPongFunction(4)); 
        giPongFunctions.add(new GIPongFunction(5));
        LinkedList<GOFunction> goPongFunctions = new LinkedList<GOFunction>();        
        goPongFunctions.add(new GOPongFunction(1));
        goPongFunctions.add(new GOPongFunction(2));
        
        //GOGIRepeaterFunction gogiRepeaterFunction = new GOGIRepeaterFunction();        
        Network network = new Network();
        try {
			//network.buildNetwork(1,1,1,new GITestFunctionRandom());
        	network.buildNetwork(giPongFunctions,5,goPongFunctions);
        	//network.buildNetwork(1,0,0,new GITestFunctionRandom());
        	//network.buildNetwork(1,30,1,gogiRepeaterFunction, gogiRepeaterFunction);
		} catch (notEnoughPRMsException e) {
			e.printStackTrace();
			System.exit(1);
		}
        network.drawGraph();
        (new Thread(network)).start();
        
        new Acme.MainFrame(new Pong(), null, 400, 400);

        //ClusterTest clusterTest = new ClusterTest();
        
        //Display the window.
        frame.setPreferredSize(new Dimension(700,700));
        frame.pack();
        frame.setVisible(true);
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
	
	public static void updateStepsPerSecond(double sps) {
		stepsPerSecond = sps;
		try {
			stepsPerSecondLabel.setText("steps: "+stepCount+"     sps: "+Double.parseDouble(new DecimalFormat("#.#").format(stepsPerSecond)));
		} catch(NumberFormatException e) {
		}
		
	}

	public static void incrementStepCount() {
		stepCount++;
	}
	
	public static double getStepsPerSecond() {
		return stepsPerSecond;
	}
}