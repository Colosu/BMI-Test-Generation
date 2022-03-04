package utils;

import java.util.HashMap;

public class TestNodeH {
	public String incomingOutput;
	public String stateOutput;
	public int state;
	public String distinguishingInput = "";
	public HashMap<String, TestNodeH> next;

	public TestNodeH(int state, String stateOutput, String inOutput) {
		this.incomingOutput = inOutput;
		this.stateOutput = stateOutput;
		this.state = state;
		next = new HashMap<String, TestNodeH>();
	}
}
