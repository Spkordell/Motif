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
	}
	
	public void start() {
    	//send all the buffered data
    	while (!firstGI.isEmpty()) {
    		for (PRM prm : prms) {
    			prm.stepOne();
    		}
    		Collections.reverse(prms); //TODO: find a better way that doesn't involve reversing the list
    		for (PRM prm : prms) {
    			prm.stepTwo();
    		}
    		Collections.reverse(prms);
    	}
	}

}
