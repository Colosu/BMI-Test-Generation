package utils;

public class TestNodeSPYH {
	public int depth;
	public String incomingOutput;
	public String stateOutput;
	public int state;
	public TestNodeSPYH[] next;

	public TestNodeSPYH(int accessSeqLen, String inOutput, String stateOutput, int state, int numInputs) {
		depth = accessSeqLen;
		incomingOutput = inOutput;
		this.stateOutput = stateOutput;
		this.state = state;
		next = new TestNodeSPYH[numInputs];
	}
}
