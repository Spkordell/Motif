package motif;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Network {
	private List<PRM> prms;
	private GI firstGI;
	public Network() {
		prms = new ArrayList<PRM>();
		
		//Make an input node with some test data
		firstGI = new GI();
    	int[] testData = {1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1};
    	firstGI.addData(testData);
		
		//make some PRMs
		prms.add(new PRM());
		prms.add(new PRM());

		//connect the PRMs
    	prms.get(0).connectDendriteTo(firstGI);
    	prms.get(1).connectDendriteTo(prms.get(0));	
    	
    	prms.get(0).connectReturnTo(prms.get(1));
	}
	
	public void start() {
    	//send all the buffered data
		int index;
    	while (!firstGI.isEmpty()) {
    		index = 1;
    		for (PRM prm : prms) {
    			System.out.println("=====node " + (index++) + "====");
    			prm.stepOne();
    		}
    		Collections.reverse(prms); //TODO: find a better way that doesn't involve reversing the list
    		for (PRM prm : prms) {
    			System.out.println("====node " + (--index) + "====");
    			prm.stepTwo();
    		}
    		Collections.reverse(prms);
    	}
	}

}
