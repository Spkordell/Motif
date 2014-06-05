/**
 * 
 */
package motif;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Global Input Class (Feeds data into nodes)
 * @author Steve Kordell
 *
 */
public class GI extends AbstractNode {

	private Queue<Integer> data;
	public GI() {
		super();
		this.data = new LinkedList<Integer>();
	}

	public void addData(int[] data) {
		for (int item : data) {
			this.data.add(item);
		}
	}
	
	public int getAxon() {
		return this.data.poll();
	}

	public boolean isEmpty() {
		return this.data.isEmpty();
	}
	
}