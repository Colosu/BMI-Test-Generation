package main;

import java.util.ArrayList;
import java.util.Iterator;

import net.automatalib.automata.simple.SimpleDeterministicAutomaton.IntAbstraction;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.automata.transducers.impl.compact.CompactMealyTransition;
import net.automatalib.util.automata.equivalence.NearLinearEquivalenceTest;
import net.automatalib.words.Alphabet;

public class Checkups {
	
	public Checkups() {
		
	}

	public boolean is_valid(CompactMealy<String,String> mm) {
		int state = mm.getInitialState();
		Alphabet<String> alph = mm.getInputAlphabet();
		int succ = 0;
		for (String a : alph) {
			if (mm.getSuccessor(state, a) != IntAbstraction.INVALID_STATE) {
				succ++;
			}
		}
		if (succ > 0) {
//			if (g->getTransducer()->Properties(kAccessible, true) == kAccessible) {
//				if (g->getTransducer()->Properties(kCoAccessible, true) == kCoAccessible) {
					return true;
//				}
//			}
		}
		return false;
	}
	
	public boolean is_validMutation(CompactMealy<String,String> mmm, CompactMealy<String,String> mm) {

//		if (g->getTransducer()->Properties(kIDeterministic, true) == kIDeterministic) {
//			if (g->getTransducer()->Properties(kAccessible, true) == kAccessible) {
//				if (g->getTransducer()->Properties(kCoAccessible, true) == kCoAccessible) {
//		return DeterministicEquivalenceTest.findSeparatingWord(mm, mmm, mm.getInputAlphabet()) != null;
		return NearLinearEquivalenceTest.findSeparatingWord(mm, mmm, mm.getInputAlphabet()) != null;
//				}
//			}
//		}
//		return false;
	}

	public boolean checkMutation(Graph g, ArrayList<Node> TS) {
	
		boolean detected = false;
		Iterator<Node> iter = TS.iterator();
		while (!detected && iter.hasNext()) {
			detected = detected || checkMutations(g, iter.next());
		}
		return detected;
	}
	
	public boolean checkMutations(Graph g, Node T) {
		boolean detected = false;
		CompactMealy<String,String> mm = g.getMachine();
		int state = mm.getInitialState();
		Node iter = T;
		String in = iter.getInput();
		String out = iter.getOutput();
		String newout;
		CompactMealyTransition<String> tr = mm.getTransition(state, in);
		while (!detected && !(tr == null) && iter.getDepth() > 1) {
			newout = tr.getOutput();
			if (!newout.equals(out)) {
				detected = true;
			} else {
				state = tr.getSuccId();
				iter = iter.getNext();
				in = iter.getInput();
				out = iter.getOutput();
				tr = mm.getTransition(state, in);
			}
		}
		
		if (!detected && tr == null && iter.getDepth() > 1) {
			detected = true;
		} else if (!detected && !(tr == null)) {
			newout = tr.getOutput();
			if (!newout.equals(out)) {
				detected = true;
			}
		}
		
		return detected;
	}
}
