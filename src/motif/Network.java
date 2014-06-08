package motif;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Network {
	private List<PRM> prms;
	private GI firstGI;
	private GI secondGI;
	public Network() {
		prms = new ArrayList<PRM>();
		
		//Make an input node with some test data
		firstGI = new GI();
		secondGI = new GI();
		
    	//int[] testData = {1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1};
		int[] testData1 = {1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1};
		int[] testData2 = {1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1};
    	firstGI.addData(testData1);
    	secondGI.addData(testData2);
		
		//make a PRM
		prms.add(new PRM());
		prms.add(new PRM());
		prms.add(new PRM());

		//connect the PRMs
    	prms.get(0).connectDendriteTo(firstGI);
    	prms.get(1).connectDendriteTo(secondGI);
    	prms.get(2).connectDendriteTo(prms.get(0));
    	prms.get(2).connectDendriteTo(prms.get(1));
    	
    	prms.get(0).connectReturnTo(prms.get(2));
    	prms.get(1).connectReturnTo(prms.get(2));
    	
    	prms.get(0).setClassiferEnabled(false);
    	prms.get(1).setClassiferEnabled(false);
	}
	
	public void start() throws TooManyDendritesException {
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
