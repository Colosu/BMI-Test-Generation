package utils;

import java.util.LinkedList ;

public class ConvergentNodeSPYH {
	public LinkedList<TestNodeSPYH> convergent;
	public ConvergentNodeSPYH[] next;
	public int state;
	public String distinguishingInput;
	public boolean isReferenceNode = false;

	public ConvergentNodeSPYH(TestNodeSPYH node) {
		state = node.state;
		convergent = new LinkedList<TestNodeSPYH>();
		convergent.addFirst(node);
		next = new ConvergentNodeSPYH[node.next.length];
	}

}
