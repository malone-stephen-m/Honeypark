
public class State {
	private NodePlace node;
	private State parent;
	private int cost;
	public State(NodePlace anode, State aparent, int acost) {
		this.node = anode;
		this.parent = aparent;
		this.cost = acost;
	}
	
	public NodePlace getNode() {
		return node;
	}

	public State getParent() {
		return parent;
	}
	
	public int getCost() {
		return cost;
	}

}
