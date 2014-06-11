package motif;

import java.awt.Color;
import java.awt.Paint;
import java.util.LinkedList;


public class AbstractNode {

	private int axon; //todo: at the moment, only being used by PRM, move if not needed by others
	private LinkedList<AbstractNode> dendrites; //used to send outputs
	private LinkedList<AbstractNode> returns; //used to revieve incoming predictions
	
	public AbstractNode() {
		this.dendrites = new LinkedList<AbstractNode>();
		this.returns = new LinkedList<AbstractNode>();
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
		node.connectReturnTo(this);
	}
	
	public void connectReturnTo(AbstractNode abstractNode) {
		this.returns.add(abstractNode);
	}
	
	protected LinkedList<AbstractNode> getDendrites() {
		return this.dendrites;
	}
	
	protected LinkedList<AbstractNode> getReturns() {
		return this.returns;
	}

	protected int checkAxon() {
		return this.axon;
	}

	public void stepOne() throws TooManyDendritesException {
	}

	public void stepTwo() {
	}

	public LinkedList<Prediction> getCurrentPredictions(PRM prm) {
		return new LinkedList<Prediction>();
	}

	public Paint getVisualizationColor() {
		return Color.white;
	}
}
