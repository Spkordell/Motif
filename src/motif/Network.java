package motif;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

public class Network {
	private List<PRM> prms;
	private List<GI> gis;
	public Network() {
		gis = new ArrayList<GI>();
		prms = new ArrayList<PRM>();
		
		//Make an input node with some test data

		gis.add(new GI());
		gis.add(new GI());
		gis.add(new GI());
		
    	//int[] testData = {1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1};
		int[] testData1 = {1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3};
		int[] testData2 = {1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3, 1, 3};
		int[] testData3 = {1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1, 1, 2, 3, 4, 4, 3, 2, 1, 1, 2, 3, 4, 4, 3, 2, 1, 1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1, 1, 2, 3};
		
    	gis.get(0).addData(testData1);
    	gis.get(1).addData(testData2);
    	gis.get(2).addData(testData3);
    	
		//make some PRMs
		prms.add(new PRM());
		prms.add(new PRM());
		prms.add(new PRM());
		prms.add(new PRM());

		//connect the PRMs to the GIs
    	prms.get(0).connectDendriteTo(gis.get(0));
    	prms.get(1).connectDendriteTo(gis.get(1));
    	prms.get(2).connectDendriteTo(gis.get(2));
    	
    	//connect the PRMs to eachother
    	prms.get(3).connectDendriteTo(prms.get(0));
    	prms.get(3).connectDendriteTo(prms.get(1));
    	prms.get(3).connectDendriteTo(prms.get(2));
      	  	
    	//disable classifiers for cells with one integer input
    	prms.get(0).setClassiferEnabled(false);
    	prms.get(1).setClassiferEnabled(false);
    	prms.get(2).setClassiferEnabled(false);
	}
	

	private List<AbstractNode> buildNodeExecutionOrder() {
		Queue<AbstractNode> fringe = new LinkedList<AbstractNode>();
		List<AbstractNode> expandedNodes = new ArrayList<AbstractNode>();
		
		//Add all the GIs to the fringe
		fringe.addAll(gis);
		
		//traverse the graph, building the list of expanded nodes.
		while (!fringe.isEmpty()) {
			AbstractNode currentNode = fringe.poll();
			if (expandedNodes.contains(currentNode)) {
				continue;
			}
			expandedNodes.add(currentNode);
			for (AbstractNode node : currentNode.getReturns()) {
				if (!expandedNodes.contains(node)) {
					fringe.add(node);
				}
			}
		}
		return expandedNodes;
	}
	
	
	public void run() throws TooManyDendritesException {
		ListIterator<AbstractNode> executionIterator = buildNodeExecutionOrder().listIterator();
		
		int index;
    	while (!gis.get(0).isEmpty()) {
    		index = 1;
    		while (executionIterator.hasNext()) {
    			System.out.println("=====node " + (index++) + "====");
    			executionIterator.next().stepOne();
    		}    		
    		while (executionIterator.hasPrevious()) {
    			System.out.println("====node " + (--index) + "====");
    			executionIterator.previous().stepTwo();
    		}
    	}
	}

}
