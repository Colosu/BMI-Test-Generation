package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;

import net.automatalib.automata.simple.SimpleDeterministicAutomaton;
import net.automatalib.automata.simple.SimpleDeterministicAutomaton.IntAbstraction;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.util.automata.conformance.WMethodTestsIterator;
import net.automatalib.util.automata.conformance.WpMethodTestsIterator;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.util.automata.cover.Covers;

public class Operations {

	public Operations (Random r) {
		rand = r;
	}
	
	public ArrayList<Node> GenerateRandomTestSuite(Graph g, int size, boolean repTests) {
	
		ArrayList<Node> TS = new ArrayList<Node>();
		int length = 0;
		int tam  = 0;
		if (size < 1) {
			size = 1;
		}
		Node T;
		while (length < size) {
			do {
				T = new Node();
				tam = GenerateRandomTest(g, size - length, T);
			} while (!repTests && repeated(T, TS));
			TS.add(new Node(T));
			length += tam;
		}
		return TS;
	}

	public void GenerateTestSuite(ArrayList<Node> TS, int ver) {
		Node T;
		Node Tinit;
		if (ver == 0) {
			T = new Node();
			T.setInput("a");
			T.setOutput("x");
			T.setNext(new Node());
			Tinit = T;
			T = T.getNext();
			T.setInput("b");
			T.setOutput("z");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("c");
			T.setOutput("w");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("d");
			T.setOutput("y");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("e");
			T.setOutput("z");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("f");
			T.setOutput("z");
			TS.add(new Node(Tinit));
		} else if (ver == 1) {
			T = new Node();
			T.setInput("a");
			T.setOutput("x");
			T.setNext(new Node());
			Tinit = T;
			T = T.getNext();
			T.setInput("b");
			T.setOutput("z");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("i");
			T.setOutput("z");
			TS.add(new Node(Tinit));
			T = new Node();
			T.setInput("a");
			T.setOutput("x");
			T.setNext(new Node());
			Tinit = T;
			T = T.getNext();
			T.setInput("j");
			T.setOutput("u");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("i");
			T.setOutput("z");
			TS.add(new Node(Tinit));
		} else if (ver == 2) {
			T = new Node();
			T.setInput("a");
			T.setOutput("x");
			T.setNext(new Node());
			Tinit = T;
			T = T.getNext();
			T.setInput("b");
			T.setOutput("z");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("i");
			T.setOutput("z");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("a");
			T.setOutput("x");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("j");
			T.setOutput("u");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("i");
			T.setOutput("z");
			TS.add(new Node(Tinit));
		} else if (ver == 3) {
			T = new Node();
			T.setInput("a");
			T.setOutput("x");
			T.setNext(new Node());
			Tinit = T;
			T = T.getNext();
			T.setInput("b");
			T.setOutput("z");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("b");
			T.setOutput("z");
			TS.add(new Node(Tinit));
			T = new Node();
			T.setInput("a");
			T.setOutput("x");
			T.setNext(new Node());
			Tinit = T;
			T = T.getNext();
			T.setInput("j");
			T.setOutput("u");
			T.setNext(new Node());
			T = T.getNext();
			T.setInput("i");
			T.setOutput("z");
			TS.add(new Node(Tinit));
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public ArrayList<Node> GenerateGeneticTestSuite(Graph g, int size, boolean max, String ff) { //Tests

		ArrayList<Node> TS = new ArrayList<Node>();
		int genSize = 100;
		ArrayList<Node> TSG[] = new ArrayList[genSize];
		ArrayList<Node> TSD[] = new ArrayList[genSize];
		double FF[] = new double[genSize];
		double FFD[] = new double[genSize];
		Integer pos[] = new Integer[genSize];
		int posD = 0;
		int posFD = 0;
		double prev = 0;
		double newer = 0;
		int epochs = 0;
		double FFBest = 0;
		int count = 0;
		ArrayList<Node> TSI;
		ArrayList<Node> TSM;
		Node T = null;
		HashMap<String, ArrayList<String>> grammar;

		// Loop variables
		int best = 0;

		if (!max) {
			FFBest = Double.MAX_VALUE;
		}

		grammar = GenerateGrammar(g);

		for (int i = 0; i < genSize; i++) {
			//Initialize population
			TSG[i] = new ArrayList<Node>();
			TSG[i] = GenerateRandomGrammarTestSuite(grammar, size, false, Integer.toString(g.getMachine().getInitialState()));
			//Evaluate population
			FF[i] = FitnessFunction(g, TSG[i], ff);
			FFD[i] = FFBest;
		}

		//Main loop
		while (!StopCriterion(epochs, count)) {

			prev = newer;

			//Select next generation
			if (epochs != 0) {
				Selection(TSG, TSD, genSize, max, FF, FFD, prev);
			}
			posD = 0;
			posFD = 0;
			
			for (int i = 0; i < genSize; i++) {
				TSD[i] = null;
				if (!max) {
					FFD[i] = Double.MAX_VALUE;
				} else {
					FFD[i] = 0.0;
				}
			}

			//Perform Crossover
			for (int i = 0; i < genSize; i++) {
				pos[i] = i;
			}
			List<Integer> l = Arrays.asList(pos);
			Collections.shuffle(l);
//			pos = (Integer[]) l.toArray();

			for (int i = 0; i < genSize-1; i+=2) {
				if (rand.nextDouble() < Main.CROSSOVER) {
					TSD[posD] = new ArrayList<Node>();
					for (Node n : TSG[pos[i]]) {
						TSD[posD].add(new Node(n));
					}
					FFD[posFD] = FF[pos[i]];
					posD++;
					posFD++;
					TSD[posD] = new ArrayList<Node>();
					for (Node n : TSG[pos[i+1]]) {
						TSD[posD].add(new Node(n));
					}
					FFD[posFD] = FF[pos[i+1]];
					posD++;
					posFD++;
					CrossoverExtend(grammar, size, TSG[pos[i]], TSG[pos[i+1]]);
					if (TSG[pos[i]] == null || TSG[pos[i+1]] == null) {
						System.out.println("null in Crossover");
					}
				} else {
					TSD[posD] = new ArrayList<Node>();
					for (Node n : TSG[pos[i]]) {
						TSD[posD].add(new Node(n));
					}
					FFD[posFD] = FF[pos[i]];
					posD++;
					posFD++;
					TSD[posD] = new ArrayList<Node>();
					for (Node n : TSG[pos[i+1]]) {
						TSD[posD].add(new Node(n));
					}
					FFD[posFD] = FF[pos[i+1]];
					posD++;
					posFD++;
				}
			}

			//Perform Mutation
			for (int i = 0; i < genSize; i++) {
				TSI = TSG[i];
				TSM = new ArrayList<Node>();

				for (Iterator<Node> it = TSI.iterator(); it.hasNext(); ) {
					Node nod = it.next();
					if (rand.nextDouble() < Main.MUTATION) {
						T = Mutation(grammar, nod.getDepth(), Integer.toString(g.getMachine().getInitialState()));
						TSM.add(new Node(T));
						T = null;
					} else {
						TSM.add(new Node(nod));
					}
				}
				TSG[i] = TSM;
				if (TSG[i] == null) {
					System.out.println("null in Mutation");
				}
			}

			//Evaluate population
			for (int i = 0; i < genSize; i++) {
				FF[i] = FitnessFunction(g, TSG[i], ff);
				if (TSG[i] == null) {
					System.out.println("null in fitness");
				}
			}

			if (max) {
				List<Double> list = Arrays.asList(ArrayUtils.toObject(FF));
				newer = (double) Collections.max(list);
				epochs++;
				if (prev == newer) {
					count++;
				} else {
					count = 0;
				}

				if (newer > FFBest) {
					best = 0;
					while(FF[best] < newer) {
						best++;
					}
					TS = TSG[best];
					FFBest = FF[best];
				}
			} else {
				List<Double> list = Arrays.asList(ArrayUtils.toObject(FF));
				newer = (double) Collections.min(list);
				epochs++;
				if (prev == newer) {
					count++;
				} else {
					count = 0;
				}

				if (newer < FFBest) {
					best = 0;
					while(FF[best] > newer) {
						best++;
					}
					TS = TSG[best];
					FFBest = FF[best];
				}
			}
		}
		return TS;
	}
	
	public ArrayList<Node> HMethod(Graph g) {
		ArrayList<Node> TS = new ArrayList<Node>();
		H h = new H();
		TS = h.HMethod(g);
		return TS;
	}
	
	public ArrayList<Node> TransitionTour(Graph g) {
		ArrayList<Node> TS = new ArrayList<Node>();
		CompactMealy<String,String> fsm = g.getMachine();
		Alphabet<String> inputs = fsm.getInputAlphabet();
		String input;
		Node T = null;
		int state = fsm.getInitialState();
		for (Iterator<Word<String>> iter = Covers.transitionCoverIterator(fsm, inputs); iter.hasNext(); ) {
			input = iter.next().firstSymbol();
//			iter.remove();
			if (fsm.getSuccessor(state, input) != SimpleDeterministicAutomaton.IntAbstraction.INVALID_STATE) {
				if (T == null) {
					T = new Node();
					TS.add(T);
				}
			} else {
				state = fsm.getInitialState();
				T = new Node();
				TS.add(T);
			}
			T.setInput(input);
			T.setOutput(fsm.getOutput(state,input));
			T.setNext(new Node());
			T = T.getNext();
			state = fsm.getSuccessor(state, input);	
		}
		return TS;
	}
	
	public ArrayList<Node> WMethod(Graph g, int size) {
		ArrayList<Node> TS = new ArrayList<Node>();
		WMethodTestsIterator<String> w = new WMethodTestsIterator<String>(g.getMachine(), g.getMachine().getInputAlphabet(), size);
		while (w.hasNext()) {
			TS.add(to_node(g.getMachine(), w.next()));
		}
		return TS;
	}
	
	public ArrayList<Node> WpMethod(Graph g, int size) {
		ArrayList<Node> TS = new ArrayList<Node>();
		WpMethodTestsIterator<String> wp = new WpMethodTestsIterator<String>(g.getMachine(), g.getMachine().getInputAlphabet(), size);
		while (wp.hasNext()) {
			TS.add(to_node(g.getMachine(), wp.next()));
		}
		return TS;
	}

	private int GenerateRandomTest(Graph g, int size, Node Ti) {
	
		Node T = Ti;
		CompactMealy<String,String> mm = g.getMachine();
		int length = 0;
		int state = mm.getInitialState();
		int input;
		Iterator<String> initer;
		String in;
		String out;
		int succ = 0;
		for (String a : mm.getInputAlphabet()) {
			if (mm.getSuccessor(state, a) != IntAbstraction.INVALID_STATE) {
				succ++;
			}
		}
		while (length < size && succ > 0) {
			input = rand.nextInt(mm.getInputAlphabet().size());
			initer = mm.getInputAlphabet().iterator();
			for (int i = 0; i < input; i++) {
				initer.next();
			}
			in = initer.next();
			out = mm.getOutput(state, in);
			
			while (out == null) {
				input = rand.nextInt(mm.getInputAlphabet().size());
				initer = mm.getInputAlphabet().iterator();
				for (int i = 0; i < input; i++) {
					initer.next();
				}
				in = initer.next();
				out = mm.getOutput(state, in);
			}
			
			T.setInput(in);
			T.setOutput(out);
			T.setLabel("");
			T.setNext(new Node("", T));
			T = T.getNext();
			length++;
			state = mm.getTransition(state, in).getSuccId();
			succ = 0;
			for (String a : mm.getInputAlphabet()) {
				if (mm.getSuccessor(state, a) != IntAbstraction.INVALID_STATE) {
					succ++;
				}
			}
		}
		return length;
	}
	
	private ArrayList<Node> GenerateRandomGrammarTestSuite(HashMap<String, ArrayList<String>> grammar, int size, boolean repTests, String start) {
	
		ArrayList<Node> TS = new ArrayList<Node>();
		int length = 0;
		int tam  = 0;
		Node T;
		while (length < size) {
			do {
				T = new Node(start);
				tam = GenerateRandomTest(grammar, size - length, T);
			} while (!repTests && repeated(T, TS));
			TS.add(new Node(T));
			length += tam;
		}
		return TS;
	}
	
	private int GenerateRandomTest(HashMap<String, ArrayList<String>> grammar, int size, Node Ti) {
	
		Node T = Ti;
		ArrayList<String> words;
		Iterator<String> iter;
		int length = 0;
		int lim = 0;
		int i = 0;
		boolean dead = false;
		while (length < size && !dead) {
			words = new ArrayList<String>(grammar.get(T.getLabel()));
			iter = words.iterator();
			if (iter.hasNext()) {
				lim = rand.nextInt(words.size());
				i = 0;
				while (i < lim) {
					iter.next();
					i++;
				}
				T.setVals(iter.next());
				length++;
				T = T.getNext();
			} else {
				dead = true;
			}
		}
		return length;
	}

	public HashMap<String, ArrayList<String>> GenerateGrammar(Graph g) {
	
		HashMap<String, ArrayList<String>> grammar = new HashMap<String, ArrayList<String>>();
		CompactMealy<String,String> mm = g.getMachine();
		int state = mm.getInitialState();
		String input;
		String output;
		String next;
		while (state < mm.getStates().size()) {
			grammar.put(Integer.toString(state), new ArrayList<String>());
			for (Iterator<String> al = mm.getInputAlphabet().iterator(); al.hasNext();) {
				input = al.next();
				output = mm.getOutput(state, input);
				if (output != null) {
					next = Integer.toString(mm.getTransition(state, input).getSuccId());
					grammar.get(Integer.toString(state)).add(input + '\t' + output + '\t' + next);
				}
			}
			state++;
		}
		return grammar;
	}
	
	public ArrayList<Node> percentage(ArrayList<Node> TS, double per) {
		
		ArrayList<Node> TSper = new ArrayList<Node>();
		int len = 0;
		int depth = 0;
		Node node = new Node();
		for (Node n : TS) {
			len = n.getDepth()*(int)per;
			if (len < 1) {
				len = 1;
			}
			node = n;
			depth = 0;
			while(depth < len) {
				node = node.getNext();
				depth++;
			}
//			node.updateDepth(0);
			node.setNext(null);
			TSper.add(n);
		}
		return TSper;
	}

	private double FitnessFunction(Graph g, ArrayList<Node> TS, String ff) throws RuntimeException {
	
		double FF = 0.0;
		if (ff == "BMI") {
			FF = Measures.MutualInformation(g, TS);
		} else if (ff == "ITSDm") {
			FF = Measures.InputTestSetDiameter(g, TS);
		} else if (ff == "OTSDm") {
			FF = Measures.OutputTestSetDiameter(g, TS);
		} else if (ff == "IOTSDm") {
			FF = Measures.InputOutputTestSetDiameter(g, TS);
		} else if (ff == "Coverage") {
			FF = Measures.Coverage(g, TS);
		} else if (ff == "Own") {
			FF = Measures.OwnFunction(g, TS);
		} else {
			throw new RuntimeException("Non-valid fitness function.");
		}
		return FF;
	}


	private boolean StopCriterion(int epochs, int count) {
		boolean stop = false;
	
		if ((count > epochs*0.2 && epochs > 20) || epochs > Main.EPOCHS) {
			stop = true;
		}
	
		return stop;
	}
	
	@SuppressWarnings("unchecked")
	private void Selection(ArrayList<Node> TS[], ArrayList<Node> TSD[], int genSize, boolean max, double FF[], double FFD[], double prev) {
	
		ArrayList<Node> TSP[] = new ArrayList[genSize];
		int pos[] = new int[genSize];
		int posD[] = new int[genSize];
		double mean = Arrays.stream(FF).average().orElse(Double.NaN);
		boolean valid = false;
		boolean posFlag = false;
		
		if(mean == Double.NaN) {
			System.err.println("Problems!!!");
		}
		
		for (int i = 0; i < genSize; i++) {
			TSP[i] = TS[i];
		}
	
		if (max) {
			for (int i = 0; i < genSize; i++) {
				pos[i] = FF[i] >= mean - rand.nextDouble()*(Math.abs(prev-mean)+1) ? i : -1; //pos selection;
				posD[i] = FFD[i] >= mean - rand.nextDouble()*(Math.abs(prev-mean)+1) ? i : -1; //pos selection;
				if (pos[i] != -1) {
					posFlag = true;
				}
				if (posD[i] != -1) {
					valid = true;
				}
			}
		} else {
			for (int i = 0; i < genSize; i++) {
				pos[i] = FF[i] <= mean + rand.nextDouble()*(Math.abs(mean-prev)+1) ? i : -1; //pos selection;
				posD[i] = FFD[i] <= mean + rand.nextDouble()*(Math.abs(mean-prev)+1) ? i : -1; //pos selection;
				if (pos[i] != -1) {
					posFlag = true;
				}
				if (posD[i] != -1) {
					valid = true;
				}
			}
		}
		
		if (!posFlag) {
			System.out.println("Problems!!!");
		}
		
		if (valid) {
			for (int i = 0; i < genSize; i++) {
				while (posD[i] < 0 || posD[i] >= genSize) {
					posD[i] = posD[rand.nextInt(genSize)];
				}
			}
			for (int i = 0; i < genSize; i++) {
				if (pos[i] >= 0) {
					TS[i] = new ArrayList<Node>();
					for (Node n : TSP[pos[i]]) {
						TS[i].add(new Node(n));
					}
				} else {
					TS[i] = new ArrayList<Node>();
					for (Node n : TSD[posD[i]]) {
						TS[i].add(new Node(n));
					}
				}
			}
			for (int i = 0; i < genSize; i++) {
				if (TS[i] == null) {
					System.out.println("Null!!!");
				}
			}
		} else {
			for (int i = 0; i < genSize; i++) {
				while (pos[i] < 0 || pos[i] >= genSize) {
					pos[i] = pos[rand.nextInt(genSize)];
				}
			}
			for (int i = 0; i < genSize; i++) {
				TS[i] = new ArrayList<Node>();
				for (Node n : TSP[pos[i]]) {
					TS[i].add(new Node(n));
				}
			}
			for (int i = 0; i < genSize; i++) {
				if (TS[i] == null) {
					System.out.println("Null!!!");
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void Crossover(HashMap<String, ArrayList<String>> grammar, int size, ArrayList<Node> TS1, ArrayList<Node> TS2) {
	
		Node T1 = null;
		Node T2 = null;
		Node Taux = null;
		String NT1[] = new String[size];
		int NT1depth[] = new int[size];
		String NT2[] = new String[size];
		int NT2depth[] = new int[size];
		int I = 0;
		int Nnode = 0;
		int depth = 0;
		String node;
		boolean valid[] = new boolean[size];
		int maxIter = size;
		int iter = 0;
		boolean match = false;
		int possize = 0;
		int pos[] = new int[size];
		int Nselected = 0;
		int actDepth = 0;
	
		//Get a list of all grammar symbols.
		I = 0;
		for (Iterator<Node> it = TS1.iterator(); it.hasNext(); ){
			T1 = it.next();
			while(T1.getDepth() != 0) {
				NT1[I] = T1.getLabel();
				NT1depth[I] = T1.getDepth();
				T1 = T1.getNext();
				I++;
			}
		}
		T1 = null;
		I = 0;
		for (Iterator<Node> it = TS2.iterator(); it.hasNext(); ){
			T2 = it.next();
			while(T2.getDepth() != 0) {
				NT2[I] = T2.getLabel();
				NT2depth[I] = T2.getDepth();
				T2 = T2.getNext();
				I++;
			}
		}
		T2 = null;
	
		//Get a feasible substitution
		iter = 0;
		while (!match && iter < maxIter) {
			//Get a random node (different from a start node) to interchange
			Nnode = rand.nextInt(size);
			node = NT1[Nnode];
			while(node == NT1[0]){
				Nnode = rand.nextInt(size);
				node = NT1[Nnode];
			}
			depth = NT1depth[Nnode];
	
			//Search for the candidates with the same length in the second test suite that can have as a child the initial node
			for (int i = 0; i < size; i++) {
				valid[i] = false;
			}
			for (int i = 0; i < size; i++) {
				if (NT2[i] == node && NT2depth[i] == depth) {
					valid[i] = true;
				}
			}
	
			//Check if there is at least one candidate
			for (int i = 0; i < size; i++) {
				match = match || valid[i];
			}
			iter++;
		}
	
		if (iter < maxIter) {
			//Select a random valid candidate
			possize = 0;
			for (int i = 0; i < size; i++) {
				if (valid[i]) {
					pos[possize] = i;
					possize++;
				}
			}
	
			Nselected = pos[rand.nextInt(possize)];
	
			//Perform the crossover
			actDepth = 0;
			Iterator<Node> it1 = TS1.iterator();
			T1 = it1.next();
			while (actDepth + T1.getDepth() < Nnode+1){
				actDepth += T1.getDepth();
				T1 = it1.next();
			}
			while(T1.getDepth() > depth+1) {
				T1 = T1.getNext();
			}
	
			actDepth = 0;
			Iterator<Node> it2 = TS2.iterator();
			T2 = it2.next();
			while (actDepth + T2.getDepth() < Nselected+1){
				actDepth += T2.getDepth();
				T2 = it2.next();
			}
			while(T2.getDepth() > depth+1) {
				T2 = T2.getNext();
			}
	
			Taux = T1.getNext();
			T1.setNext(T2.getNext());
			T2.setNext(Taux);
			T1 = null;
			T2 = null;
			
			//Compare the performance
			
			
		}
	}
	
private void CrossoverExtend(HashMap<String, ArrayList<String>> grammar, int size, ArrayList<Node> TS1, ArrayList<Node> TS2) {
		
		Node T = null;
		Node T1 = null;
		Node T2 = null;
		Node Taux1 = null;
		Node Taux2 = null;
		String NT1[] = new String[size];
		int NT1depth[] = new int[size];
		String NT2[] = new String[size];
		int NT2depth[] = new int[size];
		int I = 0;
		int index1 = 0, index2 = 0;
		String node1;
		int maxIter = size/10;
		int iter = 0;
		int actDepth = 0;
		int depth1 = 0;
		int depth2 = 0;
		int tam = 0;
		int length = 0;
	
		//Get a list of all grammar symbols.
		I = 0;
		for (Iterator<Node> it = TS1.iterator(); it.hasNext(); ){
			T1 = it.next();
			while(T1.getDepth() != 0) {
				NT1[I] = T1.getLabel();
				NT1depth[I] = T1.getDepth();
				T1 = T1.getNext();
				I++;
			}
		}
		T1 = null;
		I = 0;
		for (Iterator<Node> it = TS2.iterator(); it.hasNext(); ){
			T2 = it.next();
			while(T2.getDepth() != 0) {
				NT2[I] = T2.getLabel();
				NT2depth[I] = T2.getDepth();
				T2 = T2.getNext();
				I++;
			}
		}
		T2 = null;
		
		List<Integer> p1 = new ArrayList<>();
		
		// Two point crossover
		iter = 0;
		for(iter = 0; iter < maxIter && p1.isEmpty(); iter++) {
			index1 = rand.nextInt(size - 1) + 1;
			node1 = NT1[index1];
			depth1 = NT1depth[index1];
			
			p1.clear();
			
			for (int i = 0; i < size; i++) {
				if (NT2[i].equals(node1)) {
					p1.add(i);
				}
			}
			if (!p1.isEmpty()) {
				index2 = p1.get(rand.nextInt(p1.size()));
				depth2 = NT2depth[index2];
			}
		}
		
		//Perform the crossover
		if (index2 > 0) {
			actDepth = 0;
			Iterator<Node> it1 = TS1.iterator();
			T1 = it1.next();
			while (actDepth + T1.getDepth() < index1+1){
				actDepth += T1.getDepth();
				T1 = it1.next();
			}
			while(T1.getDepth() > depth1+1) {
				T1 = T1.getNext();
			}
	
			actDepth = 0;
			Iterator<Node> it2 = TS2.iterator();
			T2 = it2.next();
			while (actDepth + T2.getDepth() < index2+1){
				actDepth += T2.getDepth();
				T2 = it2.next();
			}
			while(T2.getDepth() > depth2+1) {
				T2 = T2.getNext();
			}
	
			Taux1 = T1.getNext();
			Taux2 = T2.getNext();
			T1.setNext(Taux2);
			T2.setNext(Taux1);
	
			actDepth = 0;
			if (T1.getDepth() < depth1+1) {
				while(T1.getNext() != null) {
					actDepth += 1;
					T1 = T1.getNext();
				}
				T1 = T1.getPare();
				T1.getNext().setPare(null);
				T1.setNext(null);
				tam = GenerateRandomTest(grammar, depth1+1-actDepth+1, T1);
				length = tam;
				while (length < depth1+1-actDepth+1) {
					do {
						T = new Node(NT1[0]);
						tam = GenerateRandomTest(grammar, depth1+1-actDepth+1 - length, T);
					} while (repeated(T, TS1));
					TS1.add(new Node(T));
					length += tam;
				}
			} else if (T1.getDepth() > depth1+1) {
				while(actDepth < depth1) {
					actDepth += 1;
					T1 = T1.getNext();
				}
				T1.getNext().setPare(null);
				T1.setNext(new Node("", T1));
			}
	
			actDepth = 0;
			if (T2.getDepth() < depth2+1) {
				while(T2.getNext() != null) {
					actDepth += 1;
					T2 = T2.getNext();
				}
				T2 = T2.getPare();
				T2.getNext().setPare(null);
				T2.setNext(null);
				tam = GenerateRandomTest(grammar, depth2+1-actDepth+1, T2);
				length = tam;
				while (length < depth2+1-actDepth+1) {
					do {
						T = new Node(NT2[0]);
						tam = GenerateRandomTest(grammar, depth2+1-actDepth+1 - length, T);
					} while (repeated(T, TS2));
					TS2.add(new Node(T));
					length += tam;
				}
			} else if (T2.getDepth() > depth2+1) {
				while(actDepth < depth2) {
					actDepth += 1;
					T2 = T2.getNext();
				}
				T2.getNext().setPare(null);
				T2.setNext(new Node("", T2));
			}
		}
	}
	
	private Node Mutation(HashMap<String, ArrayList<String>> grammar, int size, String start) {
	
		Node T = null;
		int tam = 0;
		while(tam != size) {
			T = new Node(start);
			tam = GenerateRandomTest(grammar, size, T);
		}
		return T;
	}

	private boolean repeated(Node T, ArrayList<Node> TS) {
	
		boolean repeat = false;
		Iterator<Node> iter = TS.iterator();
		while(!repeat && iter.hasNext()) {
			if(iter.next().equals(T)) {
				repeat = true;
			}
		}
		return repeat;
	}
	
	private Node to_node(CompactMealy<String,String> mm, Word<String> orig) {
		
		Integer state = mm.getIntInitialState();
		String out = "";
		String in = "";
		Node T = new Node();
		Node node = T;
		for (String st : orig) {
			if (state == null) {
				System.err.println("Not valid state");
				break;
			}
			in = st;
			out = mm.getOutput(state, in);
			node.setInput(in);
			node.setOutput(out);
			node.setNext(new Node());
			node = node.getNext();
			state = mm.getSuccessor(state, in);
		}
		return T;
	}
	
	private Random rand;
}
