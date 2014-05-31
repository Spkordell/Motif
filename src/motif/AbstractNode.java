package motif;

import java.util.LinkedList;


public class AbstractNode {

	private int axon; //todo: at the moment, only being used by PRM, move if not needed by others
	private LinkedList<AbstractNode> dendrites;
	
	public AbstractNode() {
		this.dendrites = new LinkedList<AbstractNode>();
	}

	public int getAxon() {
		return this.axon;
	}
	
	protected void setAxon(int axon) {
		this.axon = axon;
	}
	
	public void connectDendriteTo(AbstractNode node) {
		this.dendrites.add(node);
	}
	
	public LinkedList<AbstractNode> getDendrites() {
		return this.dendrites;
	}
}