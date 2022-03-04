package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Main {
	protected static int REP = 50;
	protected static int MUT = 1000;
	protected static int TESTS = 2;
	protected static int EXP = 100;
	protected static int INI = 0;
	protected static double PROB_OUTPUT = 0;
	protected static int MAX = 6;
	public static int EPOCHS = 100;
	public static double CROSSOVER = 0.75;
	public static double MUTATION = 0.1;
	public static int LEN = 100;

	@SuppressWarnings({ "resource", "unchecked" })
	public static void main(String[] args) {

		//Initialization
		Random rand = new Random();
		IOHandler IOH = new IOHandler();
		Mutations Mutator = new Mutations(rand);
		Checkups Checker = new Checkups();
		Operations Ops = new Operations(rand);
		
		File folder;
		String Ofile;
		FileWriter OFile;
        Graph G;
		ArrayList<Node> TS[];
		int len = 0;
//		ArrayList<Node> TSWp;
//		int lenWp = 0;
//		double tWp = 0;
    	Graph GM[];
    	boolean detected[][];
    	double count[];
    	double wins;
    	double killed1;
    	double killed2;
    	double valid;
    	double mean;
    	double meanKilled1;
    	double meanKilled2;
    	double total;
    	double startTime;
    	double endTime;
    	double t1;
    	double t2;
    	double time1;
    	double time2;
    	double meanTime1;
    	double meanTime2;
    	double len1;
    	double len2;
    	double meanLen1;
    	double meanLen2;
    	double minLen;
    	double maxLen;
    	String measure = "H"; //"Random", "W", "Wp", "ITSDm", "OTSDm", "IOTSDm", "Coverage", "H", "TT"
		
        for (int IT = 0; IT < 5; IT++) {
			try {
				Ofile = measure + "Results_" + IT + ".txt";
				OFile = new FileWriter(Ofile);
				OFile.write(
						"| #Test | Percentage of success BMI | Percentage of success " + measure + " | Percentage of killed mutants by BMI | Percentage of killed mutants by " + measure + " | Average computation time of BMI | Average computation time of " + measure + " | Average test suite length of BMI | Average test suite length of " + measure + " | Min test suite length of " + measure + " | Max test suite length of " + measure + " |\n");
				OFile.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			TS = new ArrayList[TESTS];
			GM = new Graph[MUT];
			detected = new boolean[TESTS][MUT];
			count = new double[TESTS];
			mean = 0;
			meanKilled1 = 0;
			meanKilled2 = 0;
			meanTime1 = 0;
			meanTime2 = 0;
			meanLen1 = 0;
			meanLen2 = 0;
			minLen = 100000;
			maxLen = 0;
			total = 0;

			for (int I = 0; I < REP; I++) {
//					folder = new File("./Benchmarks/BenchmarkCircuits");
				folder = new File("./SuperBenchmark");
				int J = 0;
				wins = 0;
				killed1 = 0;
				killed2 = 0;
				time1 = 0;
				time2 = 0;
				len1 = 0;
				len2 = 0;
				valid = 0;
//					for (int J = INI; J < INI + EXP; J++) {
				for (File Ifile : folder.listFiles()) {
//						Ifile = new File("./models.Mealy/Mealy/principle/BenchmarkCoffeeMachine/coffeemachine.dot");
					System.out.println(Ifile);

					G = IOH.readGraph(Ifile.toString());
//						G = IOH.buildCoffeeMachine();
//						G = IOH.buildPhone();

					if (G == null) {
						System.err.println(Ifile.toString() + ": Failled to load the automaton.");
						return;
					}

					if (!Checker.is_valid(G.getMachine())) {
						System.err.println(Ifile.toString() + ": Non-valid graph.");
						return;
					}

					
					// Compute Test Suites length
//					startTime = System.nanoTime() /(double)1000000000;
//					TSWp = Ops.WpMethod(G, 0);
//					endTime = System.nanoTime() /(double)1000000000;
//					tWp = endTime - startTime;
//					
//					lenWp = 0;
//					for (Node n : TSWp) {
//						lenWp += n.getDepth();
//					}
					
					len = LEN*(IT+1);
//					len = Math.max((int)(lenWp * 0.1),2);
//						len = G.getMachine().size() * G.getMachine().numInputs() * A / 5;
					

					// Generate Test Suites
//						for (int i = 0; i < TESTS; i++) {
//							Ops.GenerateTestSuite(TS[i], i+2);
//						}
					t1 = 0;
					startTime = System.nanoTime() /(double)1000000000;
					TS[0] = Ops.GenerateGeneticTestSuite(G, len, false, "BMI");
					endTime = System.nanoTime() /(double)1000000000;
					t1 = endTime - startTime;
					
					t2 = 0;
					switch (measure) {
					case "Random":
						startTime = System.nanoTime() /(double)1000000000;
						TS[1] = Ops.GenerateRandomTestSuite(G, len, false);
						endTime = System.nanoTime() /(double)1000000000;
						t2 = endTime - startTime;
						break;
					case "H":
						startTime = System.nanoTime() /(double)1000000000;
						TS[1] = Ops.HMethod(G);
						endTime = System.nanoTime() /(double)1000000000;
						t2 = endTime - startTime;
						break;
					case "TT":
						startTime = System.nanoTime() /(double)1000000000;
						TS[1] = Ops.TransitionTour(G);
						endTime = System.nanoTime() /(double)1000000000;
						t2 = endTime - startTime;
						break;
//					case "W":
//						startTime = System.nanoTime() /(double)1000000000;
//						TS[1] = Ops.percentage(Ops.WMethod(G, 0), 1/5);
//						endTime = System.nanoTime() /(double)1000000000;
//						t2 = endTime - startTime;
//						break;
//					case "Wp":
//						TS[1] = Ops.percentage(TSWp, 1/5);
//						t2 = tWp;
//						break;
					default:
						startTime = System.nanoTime() /(double)1000000000;
						TS[1] = Ops.GenerateGeneticTestSuite(G, len, true, measure);
						endTime = System.nanoTime() /(double)1000000000;
						t2 = endTime - startTime;
						break;
					}
					
					// Generate Mutants
					for (int i = 0; i < MUT; i++) {
						GM[i] = Mutator.mutateState(G, PROB_OUTPUT);
						while (!Checker.is_validMutation(GM[i].getMachine(), G.getMachine())) {
							GM[i] = Mutator.mutateState(G, PROB_OUTPUT);
						}
					}

					// Check Fail Detection
					for (int i = 0; i < TESTS; i++) {
						for (int j = 0; j < MUT; j++) {
							detected[i][j] = Checker.checkMutation(GM[j], TS[i]);
						}
					}

					// Count fail detection
					for (int i = 0; i < TESTS; i++) {
						count[i] = 0;
					}
					for (int i = 0; i < TESTS; i++) {
						for (int j = 0; j < MUT; j++) {
							if (detected[i][j]) {
								count[i]++;
							}
						}
					}
					

					// Check if our measure detected the best test suite
					if (count[0] > count[1]) {
						wins++;
					}
					if (count[0] != count[1]) {
						valid++;
						killed1 += (double) count[0] / (double)MUT;
						killed2 += (double) count[1] / (double)MUT;
						time1 += t1;
						time2 += t2;
						len1 += len;
						double auxLen = 0;
						for (Node T : TS[1]) {
							auxLen += T.getDepth();
						}
						len2 += auxLen;
						if (minLen > auxLen) {
							minLen = auxLen;
						}
						if (maxLen < auxLen) {
							maxLen = auxLen;
						}
					}
					J++;

					System.out.println(
							"run " + String.valueOf(J) + " --> " + String.format("%.4f", (double) wins / (double) valid).replace(',', '.')
									+ " --> " + String.format("%.4f", 1 - ((double) wins / (double) valid)).replace(',', '.') + " --> "
									+ String.format("%.4f", (double) killed1 / (double) valid).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) killed2 / (double) valid).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) time1 / (double) valid).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) time2 / (double) valid).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) len1 / (double) valid).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) len2 / (double) valid).replace(',', '.') + " --> "
									+ String.format("%.4f",minLen).replace(',', '.') + " --> "
									+ String.format("%.4f",maxLen).replace(',', '.'));
					System.out.flush();
				}

				try {
					OFile.write(String.valueOf(I + 1) + " & " + String.format("%.4f",(double) wins / (double) valid).replace(',', '.')
							+ " & " + String.format("%.4f",1 - ((double) wins / (double) valid)).replace(',', '.') + " & "
							+ String.format("%.4f",(double) killed1 / (double) valid).replace(',', '.') + " & "
							+ String.format("%.4f",(double) killed2 / (double) valid).replace(',', '.') + " & "
							+ String.format("%.4f",(double) time1 / (double) valid).replace(',', '.') + " & "
							+ String.format("%.4f",(double) time2 / (double) valid).replace(',', '.') + " & "
							+ String.format("%.4f",(double) len1 / (double) valid).replace(',', '.') + " & "
							+ String.format("%.4f",(double) len2 / (double) valid).replace(',', '.') + " & "
							+ String.format("%.4f",minLen).replace(',', '.') + " & "
							+ String.format("%.4f",maxLen).replace(',', '.') + " \\\\\n");
					OFile.write("\\hline\n");
					OFile.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mean += wins;
				meanKilled1 += killed1;
				meanKilled2 += killed2;
				meanTime1 += time1;
				meanTime2 += time2;
				meanLen1 += len1;
				meanLen2 += len2;
				total += valid;

				System.out.println(
						"test " + String.valueOf(I + 1) + " --> " + String.format("%.4f",(double) mean / (double) total).replace(',', '.')
								+ " --> " + String.format("%.4f",1 - ((double) mean / (double) total)).replace(',', '.') + " --> "
								+ String.format("%.4f",(double) meanKilled1 / (double) total).replace(',', '.') + " --> "
								+ String.format("%.4f",(double) meanKilled2 / (double) total).replace(',', '.') + " --> "
								+ String.format("%.4f",(double) meanTime1 / (double) total).replace(',', '.') + " --> "
								+ String.format("%.4f",(double) meanTime2 / (double) total).replace(',', '.') + " --> "
								+ String.format("%.4f",(double) meanLen1 / (double) total).replace(',', '.') + " --> "
								+ String.format("%.4f",(double) meanLen2 / (double) total).replace(',', '.') + " --> "
								+ String.format("%.4f",minLen).replace(',', '.') + " --> "
								+ String.format("%.4f",maxLen).replace(',', '.'));
				System.out.flush();
			}
			try {
				OFile.write("Mean & " + String.format("%.4f",(double) mean / (double) total).replace(',', '.') + " & "
						+ String.format("%.4f",1 - ((double) mean / (double) total)).replace(',', '.') + " & "
						+ String.format("%.4f",(double) meanKilled1 / (double) total).replace(',', '.') + " & "
						+ String.format("%.4f",(double) meanKilled2 / (double) total).replace(',', '.') + " & "
						+ String.format("%.4f",(double) meanTime1 / (double) total).replace(',', '.') + " & "
						+ String.format("%.4f",(double) meanTime2 / (double) total).replace(',', '.') + " & "
						+ String.format("%.4f",(double) meanLen1 / (double) total).replace(',', '.') + " & "
						+ String.format("%.4f",(double) meanLen2 / (double) total).replace(',', '.') + " & "
						+ String.format("%.4f",minLen).replace(',', '.') + " & "
						+ String.format("%.4f",maxLen).replace(',', '.') + " \\\\\n");
				OFile.write("\\hline\n");
				OFile.flush();
				OFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return;
	}
}
