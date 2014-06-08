package motif;

import java.util.LinkedList;


public class AbstractNode {

	private int axon; //todo: at the moment, only being used by PRM, move if not needed by others
	private LinkedList<AbstractNode> dendrites; //used to send outputs
	private LinkedList<PRM> returns; //used to revieve incoming predictions
	
	public AbstractNode() {
		this.dendrites = new LinkedList<AbstractNode>();
		this.returns = new LinkedList<PRM>();
		this.axon = -1;
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
	
	public void connectReturnTo(PRM node) {
		this.returns.add(node);
	}
	
	protected LinkedList<AbstractNode> getDendrites() {
		return this.dendrites;
	}
	
	protected LinkedList<PRM> getReturns() {
		return this.returns;
	}

	protected int checkAxon() {
		return this.axon;
	}
}
