package main;

import java.util.ArrayList;
import java.util.LinkedList;
import javafx.util.Pair;

import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.words.Alphabet;
import utils.ConvergentNodeSPYH;
import utils.Seq;
import utils.TestNodeSPYH;
import utils.Triplet;

public class SPYH {

	
	ArrayList<Node> SPYHMethod(Graph g) {
		int extraStates = 0;
		ArrayList<Node> TS = new ArrayList<Node>();
		CompactMealy<String,String> fsm = g.getMachine();
		inputAlphabet = fsm.getInputAlphabet();
		
		if (fsm.getStates().size() == 1) {
			return getTraversalSet(fsm, extraStates);
		}
		Seq[] sepSeq = getSeparatingSequences(fsm);
		ConvergentNodeSPYH[] stateNodes = new ConvergentNodeSPYH[fsm.getStates().size()];
//		stateNodes.reserve(fsm->getNumberOfStates() + 2 * extraStates);
		
		int initState = fsm.getInitialState();
//		String outputState = fsm.getOutput(initState, "");
		String outputState = "";
		TestNodeSPYH root = new TestNodeSPYH(0, "", outputState, initState, fsm.numInputs());
		ConvergentNodeSPYH cn = new ConvergentNodeSPYH(root);
		ArrayList<Node> SC = getStateCover(fsm);
		for (Node seq : SC) {
			ConvergentNodeSPYH refCN = appendSequence(cn, root, seq, fsm);
			refCN.isReferenceNode = true;
			stateNodes[refCN.state] = refCN;
		}
		LinkedList<Triplet<ConvergentNodeSPYH, String, ConvergentNodeSPYH>> transitions = new LinkedList<Triplet<ConvergentNodeSPYH, String, ConvergentNodeSPYH>>();
		for (ConvergentNodeSPYH sn : stateNodes) {
			distinguish(sn, stateNodes, sepSeq, fsm);
			for (int i = 0; i < sn.next.length; i++) {
				String input = inputAlphabet.getSymbol(i);
				if ((sn.next[i] == null || sn.next[i].state < 0 || !sn.next[i].isReferenceNode) && (Integer)fsm.getSuccessor(sn.state, i) != null && (Integer)fsm.getSuccessor(sn.state, i) >= 0) {
					transitions.addLast(new Triplet<ConvergentNodeSPYH, String, ConvergentNodeSPYH>(sn, input, stateNodes[fsm.getSuccessor(sn.state, i)]));
				}
			}
		}
		// stateNodes are initialized divergence-preserving state cover
		
		transitions.sort((t1, t2) -> {
			return ((Integer)(t1.first.convergent.getFirst().depth + t1.third.convergent.getFirst().depth)).compareTo((Integer)(t2.first.convergent.getFirst().depth + t2.third.convergent.getFirst().depth));
		});

		// confirm all transitions -> convergence-preserving transition cover
		while (!transitions.isEmpty()) {
			ConvergentNodeSPYH startCN = transitions.getFirst().first;
			String input = transitions.getFirst().second;
			ConvergentNodeSPYH nextStateCN = transitions.getFirst().third;
			transitions.removeFirst();
			int in = inputAlphabet.getSymbolIndex(input);

			// identify next state
			ConvergentNodeSPYH ncn = startCN.next[in];
			if (ncn == null) {
				Node T = new Node();
				T.setInput(input);
				T.setOutput(fsm.getOutput(startCN.state, input)); //TODO: posible fuente de problemas
				ncn = appendSequence(startCN, startCN.convergent.getFirst(), T, fsm);
			}
			distinguishCNs(ncn, nextStateCN, stateNodes, extraStates, sepSeq, fsm);

			startCN.next[in] = nextStateCN;
			mergeCN(ncn, nextStateCN);
		}
		// obtain TS
		//printTStree(root);
		// clean so convergent nodes can be destroyed
		for (ConvergentNodeSPYH sn : stateNodes) {
			sn.next = new ConvergentNodeSPYH[0];
		}
		TS = getSequences(stateNodes[0].convergent.getFirst(), fsm);
		
		return TS;
	}
	


	ArrayList<Node> getTraversalSet(CompactMealy<String,String> fsm, int depth) {
		ArrayList<Node> traversalSet = new ArrayList<Node>();
		if (depth <= 0) {
			return traversalSet;
		}
		LinkedList<Node> fifo = new LinkedList<Node>();
		fifo.addLast(new Node());
		int state = fsm.getInitialState();
		while (!fifo.isEmpty()) {
			Node seq = fifo.getFirst();
			fifo.removeFirst();
			for (int in = 0; in < fsm.numInputs(); in++) {
				String input = inputAlphabet.getSymbol(in);
				if (fsm.getSuccessor(state, input) == state) {
					Node extSeq = new Node(seq);
					extSeq.setNext(new Node());
					extSeq.getNext().setPare(extSeq);
					extSeq = extSeq.getNext();
					extSeq.setInput(input);
					extSeq.setOutput(fsm.getOutput(state, input));
					if (extSeq.getDepth() < depth) {
						fifo.addLast(extSeq);
					}
					traversalSet.add(extSeq);
				}
			}
		}
		return traversalSet;
	}
	
	@SuppressWarnings("unchecked")
	Seq[] getSeparatingSequences(CompactMealy<String,String> fsm) {
		int N = fsm.getStates().size();
		int M = ((N - 1) * N) / 2;
		LinkedList<Integer> unchecked = new LinkedList<Integer>();
		ArrayList<Pair<Integer, String>>[] link = (ArrayList<Pair<Integer, String>>[]) new ArrayList[M];

		// init seq
		Seq[] seq = new Seq[M];
		for (int i = 0; i < M; i++) {
			seq[i] = new Seq();
			seq[i].next = new Integer[fsm.numInputs()];
			link[i] = new ArrayList<Pair<Integer, String>>();
		}
		for (int j = 1; j < N; j++) {
			for (int i = 0; i < j; i++) {
				int idx = getStatePairIdx(i, j);
				for (int in = 0; in < fsm.numInputs(); in++) {
					String input = inputAlphabet.getSymbol(in);
					String outputI = (((Integer) fsm.getSuccessor(i, input)) == null || ((Integer) fsm.getSuccessor(i, input)) < 0) ? null : fsm.getOutput(i, input);
					String outputJ = (((Integer) fsm.getSuccessor(j, input)) == null || ((Integer) fsm.getSuccessor(j, input)) < 0) ? null : fsm.getOutput(j, input);
					if ((outputI != null && !outputI.equals(outputJ)) || (outputI == null && outputJ != null)) {
						seq[idx].next[in] = idx;
						if (seq[idx].minLen == 0) {
							seq[idx].minLen = 1;
							unchecked.add(idx);
						}
					}
					else {
						int nextStateI = fsm.getSuccessor(i, input);
						int nextStateJ = fsm.getSuccessor(j, input);
						// there are no transitions -> same next state = NULL_STATE
						// only one next state cannot be NULL_STATE due to distinguishing be outputs (WRONG_OUTPUT)
						if (nextStateI >= 0 && nextStateJ >= 0 && nextStateI != nextStateJ) {
							int nextIdx = getStatePairIdx(nextStateI, nextStateJ);
							if (nextIdx != idx) {
								seq[idx].next[in] = nextIdx;
								link[nextIdx].add(new Pair<Integer,String>(idx, input));
							}
						}
					}
				}
			}
		}
		// fill all undistinguished pair gradually using links
		while (!unchecked.isEmpty()) {
			int nextIdx = unchecked.getFirst();
			unchecked.removeFirst();
			for (int k = 0; k < link[nextIdx].size(); k++) {
				int idx = link[nextIdx].get(k).getKey();
				if (seq[idx].minLen == 0) {
					seq[idx].minLen = seq[nextIdx].minLen + 1;
					unchecked.add(idx);
				}
			}
			link[nextIdx].clear();
		}
		return seq;
	}
	
	ArrayList<Node> getStateCover(CompactMealy<String,String> fsm) {
		ArrayList<Node> stateCover = new ArrayList<Node>();
		ArrayList<Boolean> covered = new ArrayList<Boolean>();
		for (int i = 0; i < fsm.getStates().size(); i++) {
			covered.add(false);
		}
		LinkedList<Pair<Integer, Node>> fifo = new LinkedList<Pair<Integer, Node>>();
		// empty sequence
		stateCover.add(new Node());
		int state = fsm.getInitialState();
		covered.set(state, true);
		fifo.add(new Pair<Integer,Node>(state, null));
		while (!fifo.isEmpty()) {
			Pair<Integer, Node> current = fifo.getFirst();
			fifo.removeFirst();
			state = current.getKey();
			for (int in = 0; in < fsm.numInputs(); in++) {
				String input = inputAlphabet.getSymbol(in);
				Integer nextState = fsm.getSuccessor(state, input);
				Node newPath = null;
				if ((nextState != null && nextState >= 0) && !covered.get(nextState)) {
					covered.set(nextState, true);
					if (current.getValue() != null) {
//						if (current.getValue().getNext() != null) {
							Node T = current.getValue();
							while (T.getPare() != null) {
								T = T.getPare();
							}
							Node S = new Node(T);
							stateCover.add(S);
							while (S.getNext() != null && !S.equals(current.getValue())) {
								S = S.getNext();
							}
							newPath = new Node("", S);
							S.setNext(newPath);
//						} else {
//							newPath = new Node("", current.getValue());
//							newPath.getPare().setNext(newPath);
//						}
					} else {
						newPath = new Node();
						stateCover.add(newPath);
					}
					newPath.setInput(input);
					newPath.setOutput(fsm.getOutput(state, input));
					fifo.add(new Pair<Integer,Node>(nextState, newPath));
				}
			}
		}
		return stateCover;
	}
	
	ConvergentNodeSPYH appendSequence(ConvergentNodeSPYH cn, TestNodeSPYH node, Node seq, CompactMealy<String,String> fsm) {
		Node it = seq;
		while (it.getPare() != null) {
			it = it.getPare();
		}
		for (; it != null; it = it.getNext()) {
			if (it.getInput() == null || it.getInput().equals("")) continue;
			int in = inputAlphabet.getSymbolIndex(it.getInput());
			if (node.next[in] != null) {
				node = node.next[in];
				cn = cn.next[in];
			}
			else break;
		}
//		it = seq;
//		while (it.getPare() != null) {
//			it = it.getPare();
//		}
		for (; it != null; it = it.getNext()) {
			if (it.getInput() == null || it.getInput().equals("")) continue;
			String input = it.getInput();
			int in = inputAlphabet.getSymbolIndex(it.getInput());
			Integer state = fsm.getSuccessor(node.state, input);
			String outputState = (state == null || state < 0) ? null : "";
			String outputTransition = (state == null || state < 0) ? null : fsm.getOutput(node.state, input);
			TestNodeSPYH nextNode = new TestNodeSPYH(node.depth + 1, outputTransition, outputState, state, fsm.numInputs());
			node.next[in] = nextNode;
			node = nextNode;
			if (cn.next[in] == null) {
				cn.next[in] = new ConvergentNodeSPYH(node);
			}
			else {
				LinkedList<TestNodeSPYH> ncn = cn.next[in].convergent;
				if (node.depth < ncn.peek().depth) {
					ncn.addFirst(node);
				}
				else {
					ncn.addLast(node);
				}
			}
			cn = cn.next[in];
		}
		return cn;
	}

	void distinguish(ConvergentNodeSPYH cn, ConvergentNodeSPYH[] nodes, Seq[] sepSeq, CompactMealy<String,String> fsm) {
		for (ConvergentNodeSPYH n : nodes) {
			if ((!n.equals(cn)) && (n.state != cn.state) && (n.convergent.getFirst().stateOutput.equals(cn.convergent.getFirst().stateOutput)) && (getMinLenToDistinguish(cn, n, sepSeq) > 0)) {
				ConvergentNodeSPYH cn1 = cn;
				ConvergentNodeSPYH cn2 = n;
				Node seq = new Node();
				while (cn1.distinguishingInput != null) {
					String input = cn1.distinguishingInput;
					int in = inputAlphabet.getSymbolIndex(input);
					seq.setInput(input);
					if (cn1.next[in] == null) {
						LinkedList<TestNodeSPYH> convergent = new LinkedList<TestNodeSPYH>(cn2.convergent);
						TestNodeSPYH it2 = convergent.getFirst();
						while (it2.next[in] == null) {
							convergent.removeFirst();
							it2 = convergent.getFirst();
						}
						if (fsm.getOutput(cn1.state, input).equals(it2.next[in].incomingOutput)) {
							seq.setNext(getShortestSepSeq(fsm.getSuccessor(cn1.state, input) >= 0 ? fsm.getSuccessor(cn1.state, input) : null, cn2.next[in].state, sepSeq, fsm));
//							seq.getNext().setPare(seq);
						}
						break;
					}
					if (cn2.next[in] == null) {
						LinkedList<TestNodeSPYH> convergent = new LinkedList<TestNodeSPYH>(cn1.convergent);
						TestNodeSPYH it1 = convergent.getFirst();
						while (it1.next[in] == null) {
							convergent.removeFirst();
							it1 = convergent.getFirst();
						}
						if (it1.next[in].incomingOutput.equals(fsm.getOutput(cn2.state, input))) {
							seq.setNext(getShortestSepSeq(cn1.next[in].state, fsm.getSuccessor(cn2.state, input) >= 0 ? fsm.getSuccessor(cn2.state, input) : null, sepSeq, fsm));
//							seq.getNext().setPare(seq);
							while (seq.getNext() != null) {
								seq = seq.getNext();
							}
						}
						break;
					}
					cn2 = cn2.next[inputAlphabet.getSymbolIndex(cn1.distinguishingInput)];
					cn1 = cn1.next[inputAlphabet.getSymbolIndex(cn1.distinguishingInput)];
				}
				if (cn1.distinguishingInput == null) {
					seq.setNext(getShortestSepSeq(cn1.state, cn2.state, sepSeq, fsm));
//					seq.getNext().setPare(seq);
					while (seq.getNext() != null) {
						seq = seq.getNext();
					}
				}
				try { //TODO: aquí hay una fuente de errores.
					cn = addSequence(cn, seq, fsm);
					n = addSequence(n, seq, fsm);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	void distinguishCNs(ConvergentNodeSPYH cn, ConvergentNodeSPYH refCN, ConvergentNodeSPYH[] nodes, int depth, Seq[] sepSeq, CompactMealy<String,String> fsm) {
		
		distinguish(cn, nodes, sepSeq, fsm);
		if (!refCN.isReferenceNode) {
			distinguish(refCN, nodes, sepSeq, fsm);
		}
//		if (depth > 0) {
//			nodes.add(cn);
//			if (!refCN.isReferenceNode) {
//				nodes.add(refCN);
//			}
//			for (int i = 0; i < fsm.numInputs(); i++) {
//				if (cn.next[i] != null) {
//					Node T = new Node();
//					T.setInput(inputAlphabet.getSymbol(i));
//					T.setOutput(fsm.getOutput(cn.state, inputAlphabet.getSymbol(i)));
//					addSequence(cn, T, fsm);
//				}
//				if (refCN.next[i] != null) {
//					Node T = new Node();
//					T.setInput(inputAlphabet.getSymbol(i));
//					T.setOutput(fsm.getOutput(refCN.state, inputAlphabet.getSymbol(i)));
//					addSequence(refCN, T, fsm);
//				}
//				distinguishCNs(cn.next[i], refCN.next[i], nodes, depth - 1, sepSeq, fsm);
//			}
//			if (!refCN.isReferenceNode) {
//				nodes.remove(nodes.size());
//			}
//			nodes.remove(nodes.size());
//		}
	}

	void mergeCN(ConvergentNodeSPYH fromCN, ConvergentNodeSPYH toCN) {
		if (fromCN.convergent.getFirst().depth < toCN.convergent.getFirst().depth) {
			toCN.convergent.addFirst(fromCN.convergent.getFirst());
			fromCN.convergent.removeFirst();
		}
		for (TestNodeSPYH n : fromCN.convergent) {
			toCN.convergent.addLast(n);
		}
		fromCN.convergent.clear();
		for (int i = 0; i < fromCN.next.length; i++) {
			if (fromCN.next[i] != null) {
				if (toCN.next[i] != null) {
					mergeCN(fromCN.next[i], toCN.next[i]);
				} else {
					toCN.next[i] = fromCN.next[i];
				}
			}
		}
	}

	ArrayList<Node> getSequences(TestNodeSPYH node, CompactMealy<String,String> fsm) {
		ArrayList<Node> TS = new ArrayList<Node>();
		Node T = new Node();
		Node S = new Node();
		LinkedList<Pair<TestNodeSPYH, Node>> lifo = new LinkedList<Pair<TestNodeSPYH, Node>>();
		Node seq = T;
		Pair<TestNodeSPYH, Node> p;
		boolean hasSucc;
		lifo.addFirst(new Pair<TestNodeSPYH, Node>(node, null));
		int state = fsm.getInitialState();
		while (!lifo.isEmpty()) {
			p = lifo.getFirst();
			lifo.removeFirst();
			hasSucc = false;
			for (int i = 0; i < p.getKey().next.length; i++) {
				if (p.getKey().next[i] != null && p.getKey().next[i].state >= 0) {
					hasSucc = true;
					seq = p.getValue();
					if (seq == null) {
						seq = new Node();
						TS.add(seq);
					} else if (seq.getNext() == null) {
						seq.setNext(new Node());
//						seq.getNext().setPare(seq);
						seq = seq.getNext();
					} else {
						T = seq;
						while (T.getPare() != null) {
							T = T.getPare();
						}
						S = new Node(T);
						TS.add(S);
						while (S.getNext() != null && !S.equals(seq)) {
							S = S.getNext();
						}
						seq = S;
						seq.setNext(new Node());
//						seq.getNext().setPare(seq);
						seq = seq.getNext();
					}
					state = p.getKey().state;
					seq.setInput(inputAlphabet.getSymbol(i));
					seq.setOutput(fsm.getOutput(state, inputAlphabet.getSymbol(i)));
					lifo.addFirst(new Pair<TestNodeSPYH, Node>(p.getKey().next[i], seq));
				}
			}
			if (!hasSucc) {
				seq = p.getValue();
				if (seq == null) {
					System.out.println("Error, no sequences generated.");
				}
			}
		}
		return TS;
	}

	int getMinLenToDistinguish(ConvergentNodeSPYH cn1, ConvergentNodeSPYH cn2, Seq[] sepSeq) {
		int spIdx = getStatePairIdx(cn1.state, cn2.state);
		int minVal = 2 * sepSeq[spIdx].minLen;
		boolean hasLeaf1 = hasLeaf(cn1);
		boolean hasLeaf2 = hasLeaf(cn2);
		if (!hasLeaf1) {
			minVal += cn1.convergent.getFirst().depth;
		}
		if (!hasLeaf2) {
			minVal += cn2.convergent.getFirst().depth;
		}
		for (int i = 0; i < cn1.next.length; i++) {
			if (cn1.next[i] != null) {
				if (cn2.next[i] != null) {
					if ((spIdx == sepSeq[spIdx].next[i]) || (cn1.next[i].convergent.getFirst().stateOutput != cn2.next[i].convergent.getFirst().stateOutput)) {
						return 0; //distinguished
					}
					if (cn1.next[i].state == cn2.next[i].state) continue;
					int est = getMinLenToDistinguish(cn1.next[i], cn2.next[i], sepSeq);
					if (est == 0) return 0; //distinguished
					if (minVal >= est) {
						minVal = est;
					}
				}
				else {
					int est = getEstimate(spIdx, i, sepSeq);
					if (est != -1) {
						if (est != 1) {
							if (hasLeaf1) {
								est++;
							} else if (!hasLeaf(cn1.next[i])) {
								est += cn1.next[i].convergent.getFirst().depth;
							}
						}
						if (!hasLeaf2) {
							est += cn2.convergent.getFirst().depth;
						}
						if (minVal > est) {
							minVal = est;
						}
					}
				}
			}
			else if (cn2.next[i] != null) {
				int est = getEstimate(spIdx, i, sepSeq);
				if (est != -1) {
					if (est != 1) {
						if (hasLeaf2) {
							est++;
						} else if (!hasLeaf(cn2.next[i])) {
							est += cn2.next[i].convergent.getFirst().depth;
						}
					}
					if (!hasLeaf1) {
						est += cn1.convergent.getFirst().depth;
					}
					if (minVal > est) {
						minVal = est;
					}
				}
			}
		}
		return minVal;
	}

	Node getShortestSepSeq(int s1, int s2, Seq[] sepSeq, CompactMealy<String,String> fsm) {
		Node seq = new Node();
		Node sseq = seq;
		int P = fsm.numInputs();
		int statePairIdx = getStatePairIdx(s1, s2);
		Node T = new Node();
		while (true) {
//			seq.setInput("");
//			if (fsm.getOutput(s1, "") != fsm.getOutput(s2, "")) {
//				return sseq;
//			}
			for (int i = 0; i < P; i++) {
				Integer nextIdx = sepSeq[statePairIdx].next[i];
				if (nextIdx == null || nextIdx < 0) continue;
				if (nextIdx.equals(statePairIdx)) {
					T = new Node();
					T.setInput(inputAlphabet.getSymbol(i));
					T.setOutput(fsm.getOutput(s1,inputAlphabet.getSymbol(i)));
					seq.setNext(T);
					seq = seq.getNext();
					return sseq;
				}
				if (sepSeq[nextIdx].minLen == sepSeq[statePairIdx].minLen - 1) {
					T = new Node();
					T.setInput(inputAlphabet.getSymbol(i));
					T.setOutput(fsm.getOutput(s1,inputAlphabet.getSymbol(i))); //TODO; posible fuente de problemas.
					seq.setNext(T);
					seq = seq.getNext();
					statePairIdx = nextIdx;
					s1 = fsm.getSuccessor(s1, i);
					s2 = fsm.getSuccessor(s2, i);
					break;
				}
			}
			seq.setNext(new Node());
			seq = seq.getNext();
		}
	}

	ConvergentNodeSPYH addSequence(ConvergentNodeSPYH cn, Node seq, CompactMealy<String,String> fsm) throws Exception {
		Node S = seq;
		while (S.getPare() != null) {
			S = S.getPare();
		}
		int cost = getLenCost(cn, S);
		Node T = S;
		int size = 1;
		while (T.getNext() != null) {
			T = T.getNext();
			size++;
		}
		T = S;
		if (cost > size) {
			cn = appendSequence(cn, cn.convergent.getFirst(), T, fsm);
		}
		else if (cost > 0) {
			ConvergentNodeSPYH currCN = cn;
			while (cost != size) {
				if (T.getInput() != null && !T.getInput().equals("")) {
					currCN = currCN.next[inputAlphabet.getSymbolIndex(T.getInput())];
				}
				size--;
				T = T.getNext();
			}
			for (TestNodeSPYH node : currCN.convergent) {
				if (isLeaf(node)) {
					currCN = appendSequence(currCN, node, T, fsm);
					currCN = null;// for test purposes
					break;
				}
			}
			if (currCN != null) {
				throw new Exception("");
			}
		}
		return cn;
	}

	int getLenCost(ConvergentNodeSPYH cn, Node seq) {
		Node T = seq;
		int size = 1;
		while (T.getNext() != null) {
			T = T.getNext();
			size++;
		}
		int minCost = size + cn.convergent.getFirst().depth + 1;
		int cost = size + 1;
		ConvergentNodeSPYH currCN = cn;
		T = seq;
		while (T != null) {
			String input = T.getInput();
			cost--;
			T = T.getNext();
			if (input.equals("")) continue;
			int in = inputAlphabet.getSymbolIndex(input);
			for (TestNodeSPYH node : currCN.convergent) {
				if (isLeaf(node)) {
					minCost = cost;
					break;
				}
			}
			if (currCN.next[in] != null) {
				currCN = currCN.next[in];
			}
			else {
				currCN = null;
				break;
			}
		}
		if (currCN != null) {
			minCost = 0;
		}
		return minCost;
	}

	int getStatePairIdx(int s1, int s2, int N) {
		return (s1 < s2) ? (s1 * N + s2 - 1 - (s1 * (s1 + 3)) / 2) : (s2 * N + s1 - 1 - (s2 * (s2 + 3)) / 2);
	}
	
	int getStatePairIdx(int s1, int s2) {
		return (s1 < s2) ? ((s2 * (s2 - 1)) / 2 + s1) : ((s1 * (s1 - 1)) / 2 + s2);
	}

	boolean isLeaf(TestNodeSPYH node) {
		for (TestNodeSPYH nn : node.next) {
			if (nn != null) {
				return false;
			}
		}
		return true;
	}

	boolean hasLeaf(ConvergentNodeSPYH cn) {
		for (TestNodeSPYH n : cn.convergent) {
			if (isLeaf(n)) {
				return true;
			}
		}
		return false;
	}

	int getEstimate(int idx, int input, Seq[] sepSeq) {
		Integer nextIdx = sepSeq[idx].next[input];
		if (nextIdx == null) return -1;
		if (nextIdx.equals(idx)) return 1;
		return 2 * sepSeq[nextIdx].minLen + 1;
	}
	
	Alphabet<String> inputAlphabet;
	ConvergentNodeSPYH aux;
}
