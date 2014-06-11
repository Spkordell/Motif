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
	}
	
	/*
	 * Adds a global input to the network. Also creates and attaches a PRM to the gi.
	 */
	public GI addInput(GI gi) {
		this.gis.add(gi);
		PRM prm = new PRM();
		this.prms.add(prm);
		prm.connectDendriteTo(gi);
		prm.setClassiferEnabled(false);
		return gi;
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
	
	public void expand() {
		prms.addAll(makePRMs(4));
		prms.get(3).connectDendriteTo(prms.get(0));
		prms.get(3).connectDendriteTo(prms.get(1));
		
		prms.get(4).connectDendriteTo(prms.get(1));
		prms.get(4).connectDendriteTo(prms.get(2));	
		
		prms.get(5).connectDendriteTo(prms.get(0));
		prms.get(5).connectDendriteTo(prms.get(2));
		
		prms.get(6).connectDendriteTo(prms.get(3));
		prms.get(6).connectDendriteTo(prms.get(4));
		prms.get(6).connectDendriteTo(prms.get(5));
		

	}
	
	
	private List<PRM> makePRMs(int numberToAdd) {
		List<PRM> newPRMS = new LinkedList<PRM>();
		for (int i = 0; i < numberToAdd; i++) {
			newPRMS.add(new PRM());
		}
		return newPRMS;
	}

	public void run() throws TooManyDendritesException {
		List<AbstractNode> executionList = buildNodeExecutionOrder();
    	Visualizer.getInstance().updateGraph(executionList);
		ListIterator<AbstractNode> executionIterator = executionList.listIterator();
		
		int index;
    	while (!gis.get(0).isEmpty()) {
    		index = 1;
    		while (executionIterator.hasNext()) {
    			System.out.println("====node " + (index++) + "====");
    			executionIterator.next().stepOne();
    		}    		
    		while (executionIterator.hasPrevious()) {
    			System.out.println("====node " + (--index) + "====");
    			executionIterator.previous().stepTwo();
    		}
    	}
    	//Print the predicion for each GI
    	int i = 0;
    	for (GI gi : this.gis) {
    		System.out.println("GI "+(i++)+": "+gi.getReturns().getFirst().getCurrentPredictions(gi));
    	}
	}

}
