package main;

import java.util.ArrayList;
import java.util.LinkedList;
import javafx.util.Pair;
import java.util.Map;

import net.automatalib.automata.simple.SimpleDeterministicAutomaton;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.words.Alphabet;
import utils.Seq;
import utils.TestNodeH;

public class H {

	public ArrayList<Node> HMethod(Graph g) {
		int extraStates = 0;
		ArrayList<Node> TS = new ArrayList<Node>();
		CompactMealy<String,String> fsm = g.getMachine();
		inputAlphabet = fsm.getInputAlphabet();

		Seq[] sepSeq = getSeparatingSequences(fsm);
		ArrayList<TestNodeH> coreNodes = new ArrayList<TestNodeH>();
		ArrayList<TestNodeH> extNodes = new ArrayList<TestNodeH>(); // stores SC, TC-SC respectively

		// 1. step
		createBasicTree(fsm, coreNodes, extNodes, extraStates);
		//printTStree(coreNodes[0]);

		// 2. step
		for (TestNodeH n1it : coreNodes) {
			for (TestNodeH n2it : coreNodes) {
				if (!n1it.equals(n2it)) {
					distinguish(n1it, n2it, fsm, sepSeq);
				}
			}
		}

		// 3. step
		for (TestNodeH node : extNodes) {
			distinguish(coreNodes, node, fsm, sepSeq, extraStates);
		}

		// 4. step
		if (extraStates > 0) {
			ArrayList<TestNodeH> tmp = new ArrayList<TestNodeH>();
			for (TestNodeH node : extNodes) {
				distinguish(tmp, node, fsm, sepSeq, extraStates, true);
			}
		}

		// obtain TS
		//printTStree(coreNodes[0]);
		TS = getSequences(coreNodes.get(0), fsm);
		return TS;
	}
	
	@SuppressWarnings("unchecked")
	private Seq[] getSeparatingSequences(CompactMealy<String,String> fsm) {
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

	private void createBasicTree(CompactMealy<String,String> fsm, ArrayList<TestNodeH> coreNodes, ArrayList<TestNodeH> extNodes, int extraStates) {
		String outputState, outputTransition;
		Boolean[] covered = new Boolean[fsm.getStates().size()];
		for (int i = 0; i < fsm.getStates().size(); i++) {
			covered[i] = false;
		}
		// root
		outputState = "";
		coreNodes.add(new TestNodeH(0, outputState, ""));
		covered[0] = true;
		for (int idx = 0; idx != coreNodes.size(); idx++) {
			for (int in = 0; in < fsm.numInputs(); in++) {
				String input = inputAlphabet.getSymbol(in);
				Integer state = fsm.getSuccessor(coreNodes.get(idx).state, input);
				if (state == SimpleDeterministicAutomaton.IntAbstraction.INVALID_STATE) {
//					coreNodes.get(idx).next.put(input, new TestNodeH(state, null, null)); //TODO: fix this.
					continue;
				}
				outputState = "";
				outputTransition = fsm.getOutput(coreNodes.get(idx).state, input);
				TestNodeH node = new TestNodeH(state, outputState, outputTransition);
				coreNodes.get(idx).next.put(input, node);
				if (covered[state]) {
					extNodes.add(node);
				}
				else {
					coreNodes.add(node);
					covered[state] = true;
				}
			}
		}
//		// extNodes now contains TC-SC, coreNodes = SC
//		if (extraStates > 0) {
//			stack<shared_ptr<TestNodeH>> lifo;
//			for (const auto& n : extNodes) {
//				n->distinguishingInput = extraStates; // distinguishingInput is used here as a counter
//				lifo.emplace(n);
//				while (!lifo.empty()) {
//					auto actNode = move(lifo.top());
//					lifo.pop();
//					for (input_t input = 0; input < fsm->getNumberOfInputs(); input++) {
//						auto state = fsm->getNextState(actNode->state, input);
//						if (state == NULL_STATE) {
//							actNode->next[input] = make_shared<TestNodeH>(state, WRONG_OUTPUT, WRONG_OUTPUT);
//							continue;
//						}
//						outputState = (fsm->isOutputState()) ? fsm->getOutput(state, STOUT_INPUT) : DEFAULT_OUTPUT;
//						outputTransition = (fsm->isOutputTransition()) ? fsm->getOutput(actNode->state, input) : DEFAULT_OUTPUT;
//						auto node = make_shared<TestNodeH>(state, outputState, outputTransition);
//						if (actNode->distinguishingInput > 1) {
//							node->distinguishingInput = actNode->distinguishingInput - 1;
//							lifo.emplace(node);
//						}
//						actNode->next[input] = move(node);
//					}
//				}
//			}
//		}
	}

	private void appendSeparatingSequence(TestNodeH n1, TestNodeH n2, CompactMealy<String,String> fsm, Seq[] sepSeq) {
		int idx = getStatePairIdx(n1.state, n2.state);
		int in = -1;
		for (int i = 0; i < fsm.numInputs(); i++) {
			String input = inputAlphabet.getSymbol(i);
			Integer nextIdx = sepSeq[idx].next[i];
			if (nextIdx == null) {
				continue;
			}
			if ((nextIdx.equals(idx)) || (sepSeq[nextIdx].minLen == sepSeq[idx].minLen - 1)) {
				if (n1.next.get(input) != null) {
					in = i;
					break;
				}
				if (in == -1) {
					in = i;
				}
			}
		}

		String input = inputAlphabet.getSymbol(in);
		String outputState1 = "";
		String outputState2 = "";
		String outputTransition1 = "";
		String outputTransition2 = "";
		Integer state = null;
		TestNodeH it = n1.next.get(input);
		if (it == null) {
			state = fsm.getSuccessor(n1.state, input);
			if (state == SimpleDeterministicAutomaton.IntAbstraction.INVALID_STATE) {
				outputState1 = null;
				outputTransition1 = null;
			} else {
				outputState1 = "";
				outputTransition1 = fsm.getOutput(n1.state, input);
				n1.next.put(input, new TestNodeH(state, outputState1, outputTransition1));
			}
		}
		else {
			outputState1 = it.stateOutput;
			outputTransition1 = it.incomingOutput;
		}
		it = n2.next.get(input);
		if (it == null) {
			state = fsm.getSuccessor(n2.state, input);
			if (state == SimpleDeterministicAutomaton.IntAbstraction.INVALID_STATE) {
				outputState1 = null;
				outputTransition1 = null;
			} else {
				outputState2 = "";
				outputTransition2 = fsm.getOutput(n2.state, input);
				n2.next.put(input, new TestNodeH(state, outputState2, outputTransition2));
			}
		}
		else {
			outputState2 = it.stateOutput;
			outputTransition2 = it.incomingOutput;
		}
		if (outputState1 != null && outputTransition1 != null && (outputState1.equals(outputState2)) && (outputTransition1.equals(outputTransition2))) {
			appendSeparatingSequence(n1.next.get(input), n2.next.get(input), fsm, sepSeq);
			//  } else {
			//        printTStree(coreNodes[0]);
		}
	}

	private void distinguish(TestNodeH n1, TestNodeH n2, CompactMealy<String,String> fsm, Seq[] sepSeq) {
		if ((n1.state == n2.state) || !(n1.stateOutput.equals(n2.stateOutput)) || (getMinLenToDistinguish(n1, n2, sepSeq, fsm.getStates().size(), fsm.numInputs()) == 0)) {
			return;
		}
		addSeparatingSequence(n1, n2, fsm, sepSeq);
	}

	private void distinguish(ArrayList<TestNodeH> nodes, TestNodeH currNode, CompactMealy<String,String> fsm, Seq[] sepSeq , int depth) {
		for (TestNodeH n : nodes) {
			distinguish(n, currNode, fsm, sepSeq);
		}
		if (depth > 0) {
			for (TestNodeH pNext : currNode.next.values()) {
				distinguish(nodes, pNext, fsm, sepSeq, depth - 1);
			}
		}
		
	}

	private void distinguish(ArrayList<TestNodeH> nodes, TestNodeH currNode, CompactMealy<String,String> fsm, Seq[] sepSeq , int depth, boolean extend) {
		for (TestNodeH n : nodes) {
			distinguish(n, currNode, fsm, sepSeq);
		}
		if (depth > 0) {
			if (extend) {
				nodes.add(currNode);
			}
			for (TestNodeH pNext : currNode.next.values()) {
				distinguish(nodes, pNext, fsm, sepSeq, depth - 1, extend);
			}
			if (extend) {
				nodes.remove(nodes.size() - 1);
			}
		}
		
	}

	private ArrayList<Node> getSequences(TestNodeH node, CompactMealy<String,String> fsm) {
		ArrayList<Node> TS = new ArrayList<Node>();
		Node T = new Node();
		Node S = new Node();
		LinkedList<Pair<TestNodeH, Node>> lifo = new LinkedList<Pair<TestNodeH, Node>>();
		Node seq = T;
		Pair<TestNodeH, Node> p;
		lifo.addFirst(new Pair<TestNodeH, Node>(node, null));
		while (!lifo.isEmpty()) {
			p = lifo.getFirst();
			lifo.removeFirst();
			if (p.getKey().next.isEmpty()) {
				T = p.getValue();
				if (T != null) {
					while (T.getPare() != null) {
						T = T.getPare();
					}
					TS.add(new Node(T));
				}
			}
			else {
				for (Map.Entry<String, TestNodeH> pNext : p.getKey().next.entrySet()) {
					seq = p.getValue();
					if (seq != null) {
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
						seq.setInput(pNext.getKey());
						seq.setOutput(fsm.getOutput(pNext.getValue().state, pNext.getKey()));
						lifo.addLast(new Pair<TestNodeH, Node>(pNext.getValue(), new Node(seq)));
					} else {
						seq = new Node();
						TS.add(seq);
						seq.setInput(pNext.getKey());
						seq.setOutput(fsm.getOutput(pNext.getValue().state, pNext.getKey()));
						lifo.addLast(new Pair<TestNodeH, Node>(pNext.getValue(), new Node(seq)));
					}
				}
			}
		}
		return TS;
	}

	private int getMinLenToDistinguish(TestNodeH n1, TestNodeH n2, Seq[] sepSeq, int N, int P) {
		int minVal = N;
		int in = 0;
		for (int i = 0; i < P; i++) {
			String input = inputAlphabet.getSymbol(i);
			TestNodeH sIt1 = n1.next.get(input);
			TestNodeH sIt2 = n2.next.get(input);
			if (sIt1 != null) {
				if (sIt2 != null) {
					if (!(sIt1.incomingOutput.equals(sIt2.incomingOutput)) || !(sIt1.stateOutput.equals(sIt2.stateOutput))) {
						return 0; //distinguished
					}
					if (sIt1.state == sIt2.state) {
						continue;
					}
					int est = getMinLenToDistinguish(sIt1, sIt2, sepSeq, N, P);
					if (est == 0) {
						return 0; //distinguished
					}
					if (minVal >= est) {
						minVal = est;
						in = i;
					}
				}
				else {
					int est = getEstimate(n1, n2, i, sepSeq);
					if (minVal > est) {
						minVal = est;
						in = i;
					}
				}
			}
			else {
				if (sIt2 != null) {
					int est = getEstimate(n2, n1, i, sepSeq);
					if (minVal > est) {
						minVal = est;
						in = i;
					}
				}
				else {
					int est = getEstimate(n2, n1, i, sepSeq);
					if (minVal - 1 > est) {
						minVal = est + 1;
						in = i;
					}
				}
			}
		}
		String input = inputAlphabet.getSymbol(in);
		n1.distinguishingInput = input;
		return minVal;
	}

	private void addSeparatingSequence(TestNodeH n1, TestNodeH n2, CompactMealy<String,String> fsm, Seq[] sepSeq) {
		TestNodeH fIt = n1.next.get(n1.distinguishingInput);
		if (fIt == null) {
			appendSeparatingSequence(n2, n1, fsm, sepSeq);
			return;
		}
		TestNodeH sIt = n2.next.get(n1.distinguishingInput); //TODO: maybe wrong?
		if (sIt == null) {
			appendSeparatingSequence(n1, n2, fsm, sepSeq);
			return;
		}
		addSeparatingSequence(fIt, sIt, fsm, sepSeq);
	}
	
	private int getStatePairIdx(int s1, int s2) {
		return (s1 < s2) ? ((s2 * (s2 - 1)) / 2 + s1) : ((s1 * (s1 - 1)) / 2 + s2);
	}

	private int getEstimate(TestNodeH n1, TestNodeH n2, int input, Seq[] sepSeq) {
		int idx = getStatePairIdx(n1.state, n2.state);
		Integer nextIdx = sepSeq[idx].next[input];
		if (nextIdx == null) return -1;
		if (nextIdx.equals(idx)) return 1;
		return 2 * sepSeq[nextIdx].minLen + 1;
	}
	
	Alphabet<String> inputAlphabet;
}
