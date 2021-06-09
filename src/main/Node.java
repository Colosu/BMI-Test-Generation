package main;

import java.util.ArrayList;

public class Node {

	public Node() {
		next = null;
		pare = null;
	}
	
	public Node(String lab) {
		label = lab;
		next = null;
		pare = null;
	}
	
	public Node(String lab, Node pare) {
		label = lab;
		next = null;
		this.pare = pare;
	}
	
	public Node(Node node) {
		this.label = node.label;
		this.input = node.input;
		this.output = node.output;
		this.depth = node.depth;
		this.pare = null;
		if (node.next != null) {
			Node n = new Node();
			Node nOr = node.getNext();
			Node nOld = this;
			nOld.setNext(n);
			n.setLabel(nOr.getLabel());
			n.setInput(nOr.getInput());
			n.setOutput(nOr.getOutput());
			n.setDepth(nOr.getDepth());
			n.setPare(nOld);
			nOr = nOr.getNext();
			while(nOr != null) {
				nOld = n;
				n = new Node();
				nOld.setNext(n);
				n.setLabel(nOr.getLabel());
				n.setInput(nOr.getInput());
				n.setOutput(nOr.getOutput());
				n.setDepth(nOr.getDepth());
				n.setPare(nOld);
				nOr = nOr.getNext();
			}
		} else {
			this.next = null;
		}
	}
	
	public String getInput() {
		return input;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getOutput() {
		return output;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public Node getNext() {
		return next;
	}
	
	public void setNext(Node next) {
		this.next = next;
		if (next != null) {
			next.setPare(this);
			updateDepth(next.getDepth() + 1);
		} else {
			updateDepth(0);
		}
	}
	
	public Node getPare() {
		return pare;
	}
	
	public void setPare(Node pare) {
		this.pare = pare;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public void setVals(String vals) {
	
		output = "";
		input = "";
		boolean first = false;
		boolean second = false;
		String nextLabel = "";
		for (int i = 0; i < vals.length(); i++) {
			if (first && second) {
				nextLabel += Character.toString(vals.charAt(i));
			} else if (first && vals.charAt(i) == '\t') {
				second = true;
			} else if (first) {
				output += Character.toString(vals.charAt(i));
			} else if (vals.charAt(i) == '\t') {
				first = true;
			} else {
				input += Character.toString(vals.charAt(i));
			}
		}
	
		if (this.next == null) {
			next = new Node(nextLabel, this);
		}
		updateDepth(1);
	}

	public void copy(Node other) {
		this.label = other.label;
		this.input = other.input;
		this.output = other.output;
		this.depth = other.depth;
		this.pare = null;
		if (other.next != null) {
			Node n = new Node();
			Node nOr = other.getNext();
			Node nOld = this;
			nOld.setNext(n);
			n.setLabel(nOr.getLabel());
			n.setInput(nOr.getInput());
			n.setOutput(nOr.getOutput());
			n.setDepth(nOr.getDepth());
			n.setPare(nOld);
			nOr = nOr.getNext();
			while(nOr != null) {
				nOld = n;
				n = new Node();
				nOld.setNext(n);
				n.setLabel(nOr.getLabel());
				n.setInput(nOr.getInput());
				n.setOutput(nOr.getOutput());
				n.setDepth(nOr.getDepth());
				n.setPare(nOld);
				nOr = nOr.getNext();
			}
		} else {
			this.next = null;
		}
	}
	
	public boolean equals(Node other) {
		boolean result = false;
		Node n = this;
		Node nOr = other;
	
		while (!result && n != null && nOr != null &&
				n.getDepth() == nOr.getDepth() && 
				n.getLabel().equals(nOr.getLabel()) && 
				n.getInput().equals(nOr.getInput()) && 
				n.getOutput().equals(nOr.getOutput())) {
			if (n.getNext() != null && nOr.getNext() != null) {
				n = n.getNext();
				nOr = nOr.getNext();
			} else if (n.getNext() == null && nOr.getNext() == null) {
				result = true;
			}
		}
	
		return result;
	}
	
	public void updateDepth(int depth) {
		this.depth = depth;
		Node p = this.pare;
		int d = depth;
		while (p != null) {
			d = d + 1;
			p.setDepth(d);
			p = p.getPare();
		}
	}

	public ArrayList<IOPair> to_IOPairList() {
		
		ArrayList<IOPair> list = new ArrayList<IOPair>();
		IOPair pair = null;
		Node T = this;
		while (T != null && T.getDepth() > 0) {
			pair = new IOPair(T.getInput(), T.getOutput());
			list.add(pair);
			T = T.getNext();
		}
		
		return list;
	}
	
	private String input = "";
	private String output = "";
	private String label = "";
	private Node next;
	private Node pare;
	private int depth = 0;
}