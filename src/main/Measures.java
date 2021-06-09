package main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.Deflater;

import org.apache.commons.lang.ArrayUtils;

import net.automatalib.automata.transducers.impl.compact.CompactMealy;

public class Measures {

	public static double InputTestSetDiameter(Graph g, ArrayList<Node> TS) {
		double FF = 0.0;
		Deflater zlib = new Deflater();
		String[] origs = to_St(TS, "input");
		int len = origs.length;
		byte[][] inputs = new byte[len][];
		byte[][] outputs = new byte[len][];
		double[] sizes = new double[len];
		String[] origsCut = new String[len];
		byte[][] inputsCut = new byte[len][];
		byte[][] outputsCut = new byte[len][];
		double[] sizesCut = new double[len];
		String orig = "";
		byte[] input = null;
		byte[] output = new byte[1024];
		double size = 0.0;
		double[] NCDs = new double[len];
		List<Double> list;
		List<Double> listCut;
		List<Double> listNCDs;
		double min;
		double max;
		int index;
		
		
		try {
			for (int i = 0; i < len; i++) {
				zlib.reset();
				inputs[i] = origs[i].getBytes("UTF-8");
				zlib.setInput(inputs[i]);
				zlib.finish();
				outputs[i] = new byte[1024];
				sizes[i] = zlib.deflate(outputs[i], 0, 1024, Deflater.FULL_FLUSH);
			}
			
			if (len == 1) {
				NCDs[0] = sizes[0];
			}
			
			int k = 0;
			while (k < len - 1) {
				orig = "";
				for (int i = 0; i < len; i++) {
					if (sizesCut[i] != -1) {
						orig += origs[i];
						origsCut[i] = "";
						for (int j = 0; j < len; j++) {
							if (i != j && sizesCut[j] != -1) {
								origsCut[i] += origs[j];
							}
						}
						zlib.reset();
						inputsCut[i] = origsCut[i].getBytes("UTF-8");
						zlib.setInput(inputsCut[i]);
						zlib.finish();
						outputsCut[i] = new byte[1024];
						sizesCut[i] = zlib.deflate(outputsCut[i], 0, 1024, Deflater.FULL_FLUSH);
					}
				}

				zlib.reset();
				input = orig.getBytes("UTF-8");
				zlib.setInput(input);
				zlib.finish();
				size = zlib.deflate(output, 0, 1024, Deflater.FULL_FLUSH);

				list = Arrays.asList(ArrayUtils.toObject(sizes));
				min = (double) Collections.min(list);
				listCut = Arrays.asList(ArrayUtils.toObject(sizesCut));
				max = (double) Collections.max(listCut);
				NCDs[k] = (size - min)/max;

				k++;
				if (k < len - 1) {
					index = listCut.indexOf(max); 
					sizes[index] = (double) Collections.max(list);
					sizesCut[index] = -1;
				}
			}
			
			zlib.end();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		listNCDs = Arrays.asList(ArrayUtils.toObject(NCDs));
		FF = (double) Collections.max(listNCDs);
		return FF;
	}
	
	public static double OutputTestSetDiameter(Graph g, ArrayList<Node> TS) {
		double FF = 0.0;
		Deflater zlib = new Deflater();
		String[] origs = to_St(TS, "output");
		int len = origs.length;
		byte[][] inputs = new byte[len][];
		byte[][] outputs = new byte[len][];
		double[] sizes = new double[len];
		String[] origsCut = new String[len];
		byte[][] inputsCut = new byte[len][];
		byte[][] outputsCut = new byte[len][];
		double[] sizesCut = new double[len];
		String orig = "";
		byte[] input = null;
		byte[] output = new byte[1024];
		double size = 0.0;
		double[] NCDs = new double[len];
		List<Double> list;
		List<Double> listCut;
		List<Double> listNCDs;
		double min;
		double max;
		int index;
		
		
		try {
			for (int i = 0; i < len; i++) {
				zlib.reset();
				inputs[i] = origs[i].getBytes("UTF-8");
				zlib.setInput(inputs[i]);
				zlib.finish();
				outputs[i] = new byte[1024];
				sizes[i] = zlib.deflate(outputs[i], 0, 1024, Deflater.FULL_FLUSH);
			}
			
			if (len == 1) {
				NCDs[0] = sizes[0];
			}
			
			int k = 0;
			while (k < len - 1) {
				orig = "";
				for (int i = 0; i < len; i++) {
					if (sizesCut[i] != -1) {
						orig += origs[i];
						origsCut[i] = "";
						for (int j = 0; j < len; j++) {
							if (i != j && sizesCut[j] != -1) {
								origsCut[i] += origs[j];
							}
						}
						zlib.reset();
						inputsCut[i] = origsCut[i].getBytes("UTF-8");
						zlib.setInput(inputsCut[i]);
						zlib.finish();
						outputsCut[i] = new byte[1024];
						sizesCut[i] = zlib.deflate(outputsCut[i], 0, 1024, Deflater.FULL_FLUSH);
					}
				}

				zlib.reset();
				input = orig.getBytes("UTF-8");
				zlib.setInput(input);
				zlib.finish();
				size = zlib.deflate(output, 0, 1024, Deflater.FULL_FLUSH);

				list = Arrays.asList(ArrayUtils.toObject(sizes));
				min = (double) Collections.min(list);
				listCut = Arrays.asList(ArrayUtils.toObject(sizesCut));
				max = (double) Collections.max(listCut);
				NCDs[k] = (size - min)/max;

				k++;
				if (k < len - 1) {
					index = listCut.indexOf(max); 
					sizes[index] = (double) Collections.max(list);
					sizesCut[index] = -1;
				}
			}
			
			zlib.end();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		listNCDs = Arrays.asList(ArrayUtils.toObject(NCDs));
		FF = (double) Collections.max(listNCDs);
		return FF;
	}
	
	public static double InputOutputTestSetDiameter(Graph g, ArrayList<Node> TS) {
		double FF = 0.0;
		Deflater zlib = new Deflater();
		String[] origs = to_St(TS, "both");
		int len = origs.length;
		byte[][] inputs = new byte[len][];
		byte[][] outputs = new byte[len][];
		double[] sizes = new double[len];
		String[] origsCut = new String[len];
		byte[][] inputsCut = new byte[len][];
		byte[][] outputsCut = new byte[len][];
		double[] sizesCut = new double[len];
		String orig = "";
		byte[] input = null;
		byte[] output = new byte[1024];
		double size = 0.0;
		double[] NCDs = new double[len];
		List<Double> list;
		List<Double> listCut;
		List<Double> listNCDs;
		double min;
		double max;
		int index;
		
		
		try {
			for (int i = 0; i < len; i++) {
				zlib.reset();
				inputs[i] = origs[i].getBytes("UTF-8");
				zlib.setInput(inputs[i]);
				zlib.finish();
				outputs[i] = new byte[1024];
				sizes[i] = zlib.deflate(outputs[i], 0, 1024, Deflater.FULL_FLUSH);
			}
			
			if (len == 1) {
				NCDs[0] = sizes[0];
			}
			
			int k = 0;
			while (k < len - 1) {
				orig = "";
				for (int i = 0; i < len; i++) {
					if (sizesCut[i] != -1) {
						orig += origs[i];
						origsCut[i] = "";
						for (int j = 0; j < len; j++) {
							if (i != j && sizesCut[j] != -1) {
								origsCut[i] += origs[j];
							}
						}
						zlib.reset();
						inputsCut[i] = origsCut[i].getBytes("UTF-8");
						zlib.setInput(inputsCut[i]);
						zlib.finish();
						outputsCut[i] = new byte[1024];
						sizesCut[i] = zlib.deflate(outputsCut[i], 0, 1024, Deflater.FULL_FLUSH);
					}
				}

				zlib.reset();
				input = orig.getBytes("UTF-8");
				zlib.setInput(input);
				zlib.finish();
				size = zlib.deflate(output, 0, 1024, Deflater.FULL_FLUSH);

				list = Arrays.asList(ArrayUtils.toObject(sizes));
				min = (double) Collections.min(list);
				listCut = Arrays.asList(ArrayUtils.toObject(sizesCut));
				max = (double) Collections.max(listCut);
				NCDs[k] = (size - min)/max;

				k++;
				if (k < len - 1) {
					index = listCut.indexOf(max); 
					sizes[index] = (double) Collections.max(list);
					sizesCut[index] = -1;
				}
			}
			
			zlib.end();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		listNCDs = Arrays.asList(ArrayUtils.toObject(NCDs));
		FF = (double) Collections.max(listNCDs);
		return FF;
	}
	
	public static double Coverage(Graph g, ArrayList<Node> TS) {
		
		CompactMealy<String, String> m = g.getMachine();
		double FF = 0.0;
		int numStates = m.getStates().size();
		ArrayList<Node> aux = new ArrayList<Node>(TS);
		Node T;
		int state = m.getInitialState();
		int r = -1;
		int k = 0;
		int numSymbols = m.getInputAlphabet().size();
		String in = "";
		Collection<String> inputs = null;
		Iterator<String> inIter = null;
		String input = "";

		int sizetrans = numStates * numSymbols;
		ArrayList<Boolean> trans = new ArrayList<Boolean>();
		for (int i = 0; i < sizetrans; i++) {
			trans.add(false);
		}
		ListIterator<Node> iter = aux.listIterator();

		while (iter.hasNext()) {
			T = iter.next();
			//size2 = T.size();
			state = g.getMachine().getInitialState();
			while (T.getNext() != null) {
				r = -1;
				k = 0;
				in = T.getInput();
				inputs = m.getLocalInputs(state);
				inIter = inputs.iterator();
				while (r == -1 && inIter.hasNext()) {
					input = inIter.next();
					if (input.equals(in)) {
						r = k;
					} else {
						k++;
					}
				}

				trans.set(state*numSymbols + r, true);
				T = T.getNext();
				state = m.getSuccessor(state, in);
			}
		}

		FF = (double)trans.stream().filter(x -> x == true).count();
		return FF;
	}
	
	public static double OwnFunction(Graph g, ArrayList<Node> TS) {
		//TODO: Implement your own fitness function.
		double FF = 0;
		return FF;
	}
	
	public static double MutualInformation(Graph g, ArrayList<Node> TS) {
		
		ArrayList<Node> aux = TS;
		double MI = 0.0;
		ListIterator<Node> iter1 = aux.listIterator();
		ListIterator<Node> iter2;
		Node nex;
	
		while (iter1.hasNext()) {
			iter2 = aux.listIterator(iter1.nextIndex());
			nex = iter1.next();
			while (iter2.hasNext()) {
				MI += MutualInformation(g.getIOmap(), nex.to_IOPairList(), iter2.next().to_IOPairList());
			}
		}
		return MI;
	}

	private static double MutualInformation(HashMap<IOPair, Integer> IOmap, ArrayList<IOPair> T1, ArrayList<IOPair> T2) {
	
		double MI = 0.0;
		ArrayList<IOPair> aux1 = T1;
		ArrayList<IOPair> aux2 = T2;
		Iterator<IOPair> iter1;
		Iterator<IOPair> iter2;
		IOPair aux;
		IOPair nex1;
		IOPair nex2;
		int c = 0;
		int c1 = 0;
		int c2 = 0;
		int size1 = aux1.size();
		int size2 = aux2.size();
		ArrayList<Integer> n1 = new ArrayList<Integer>();
		ArrayList<Integer> n2 = new ArrayList<Integer>();
		ArrayList<Integer> mx1 = new ArrayList<Integer>();
		ArrayList<Integer> mx2 = new ArrayList<Integer>();
		ArrayList<Integer> count1 = new ArrayList<Integer>();
		ArrayList<Integer> count2 = new ArrayList<Integer>();
		ArrayList<Integer> mx = new ArrayList<Integer>();
		Iterator<Integer> naux1;
		Iterator<Integer> naux2;
		Iterator<Integer> naux3;
	
		aux1.sort(new IOPairComp());
		aux2.sort(new IOPairComp());
		
		iter1 = aux1.iterator();
		iter2 = aux2.iterator();

		nex1 = iter1.next();
		nex2 = iter2.next();
		
		while (size1 > 0 && size2 > 0) {
			c = 0;
			
			if (nex2.less(nex1)) {
				aux = nex2.copy();
				mx2.add(IOmap.get(aux));
				while(nex2.equals(aux) && iter2.hasNext()) {
					c++;
					nex2 = iter2.next();
				}
				if (nex2.equals(aux)) {
					c++;
				}
				size2 -= c;
				n2.add(c);
			} else  if (nex1.less(nex2)) {
				aux = nex1.copy();
				mx1.add(IOmap.get(aux));
				while(nex1.equals(aux) && iter1.hasNext()) {
					c++;
					nex1 = iter1.next();
				}
				if (nex1.equals(aux)) {
					c++;
				}
				size1 -= c;
				n1.add(c);
			} else {
				aux = nex1.copy();
				mx.add(IOmap.get(aux));
				while(nex1.equals(aux) && nex2.equals(aux) && iter1.hasNext() && iter2.hasNext()) {
					c++;
					nex1 = iter1.next();
					nex2 = iter2.next();
				}
				c1 = c;
				c2 = c;
				while (nex1.equals(aux) && iter1.hasNext()) {
					c1++;
					nex1 = iter1.next();
				}
				if (nex1.equals(aux)) {
					c1++;
				}
				while (nex2.equals(aux) && iter2.hasNext()) {
					c2++;
					nex2 = iter2.next();
				}
				if (nex2.equals(aux)) {
					c2++;
				}
				size1 -= c1;
				size2 -= c2;
				count1.add(c1);
				count2.add(c2);
			}
		}
	
		while (size1 > 0) {
			c = 0;
			aux = nex1.copy();
			mx1.add(IOmap.get(aux));
			while(nex1.equals(aux) && iter1.hasNext()) {
				c++;
				nex1 = iter1.next();
			}
			if (nex1.equals(aux)) {
				c++;
			}
			size1 -= c;
			n1.add(c);
		}
	
		while (size2 > 0) {
			c = 0;
			aux = nex2.copy();
			mx2.add(IOmap.get(aux));
			while(nex2.equals(aux) && iter2.hasNext()) {
				c++;
				nex2 = iter2.next();
			}
			if (nex2.equals(aux)) {
				c++;
			}
			size2 -= c;
			n2.add(c);
		}
	
		naux1 = count1.iterator();
		naux2 = count2.iterator();
		naux3 = mx.iterator();
		MI = 0.0;
		while (naux1.hasNext() && naux2.hasNext() && naux3.hasNext()) {
			MI += MutualInformation(naux1.next(), naux2.next(), naux3.next(), T1.equals(T2));
		}
		return MI;
	}

	private static double MutualInformation(double n1, double n2, double mx, boolean eq) {
	
		if (eq) {
			return (n1*(n1-1)/2)*log2(mx+1)/mx;
		} else {
			return n1*n2*log2(mx+1)/mx;
		}
	}
	
	private static double log2(double val) {
		return Math.log(val)/Math.log(2.0);
	}


	private static String[] to_St(ArrayList<Node> TS, String kind) {
		String[] result = new String[TS.size()];
		ArrayList<Node> aux = TS;
		Iterator<Node> it = aux.iterator();
		for (int i = 0; it .hasNext(); i++) {
			result[i] = to_Str(it.next(), kind);
		}
		return result;
	}
	
	private static String to_Str(Node T, String kind) {
		String result = "";
		Node aux = T;
		while (aux != null) {
			if (kind == "input") {
				result += aux.getInput();
			} else if (kind == "output") {
				result += aux.getOutput();
			} else {
				result += aux.getInput();
				result += aux.getOutput();
			}
			aux = aux.getNext();
		}
		aux = null;
		return result;
	}
}
