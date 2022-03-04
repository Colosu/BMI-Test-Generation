package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Main {
	protected static int REP = 50;
	protected static int MUT = 1000;
	protected static int TESTS = 8;
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
    	double killed1;
    	double killed2;
    	double killed3;
    	double killed4;
    	double killed5;
    	double killed6;
    	double killed7;
    	double killed8;
    	double startTime;
    	double endTime;
    	double t1;
    	double t2;
    	double t3;
    	double t4;
    	double t5;
    	double t6;
    	double t7;
    	double t8;
    	double time1;
    	double time2;
    	double time3;
    	double time4;
    	double time5;
    	double time6;
    	double time7;
    	double time8;
    	double size;
		
        for (int IT = 0; IT < 1; IT++) {
			try {
				Ofile = "SizeResults_" + IT + ".txt";
				OFile = new FileWriter(Ofile);
				OFile.write(
						"| #Test | Percentage of killed mutants by BMI | Percentage of killed mutants by Random | Percentage of killed mutants by H | Percentage of killed mutants by TT | Percentage of killed mutants by ITSDm | Percentage of killed mutants by OTSDm | Percentage of killed mutants by IOTSDm | Percentage of killed mutants by Coverage | Average computation time of BMI | Average computation time of Random | Average computation time of H | Average computation time of TT | Average computation time of ITSDm | Average computation time of OTSDm | Average computation time of IOTSDm | Average computation time of Coverage |\n");
				OFile.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			TS = new ArrayList[TESTS];
			GM = new Graph[MUT];
			detected = new boolean[TESTS][MUT];
			count = new double[TESTS];

			folder = new File("./SuperBenchmark");
//					for (int J = INI; J < INI + EXP; J++) {
			for (File Ifile : folder.listFiles()) {
//						Ifile = new File("./models.Mealy/Mealy/principle/BenchmarkCoffeeMachine/coffeemachine.dot");
				System.out.println(Ifile);
				
				killed1 = 0;
				killed2 = 0;
				killed3 = 0;
				killed4 = 0;
				killed5 = 0;
				killed6 = 0;
				killed7 = 0;
				killed8 = 0;
				time1 = 0;
				time2 = 0;
				time3 = 0;
				time4 = 0;
				time5 = 0;
				time6 = 0;
				time7 = 0;
				time8 = 0;
				size = 0;

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

				size = G.getMachine().getStates().size();
				// Compute Test Suites length
				len = LEN*(IT+1);

				for (int k = 0; k < REP; k++) {

					t1 = 0;
					startTime = System.nanoTime() /(double)1000000000;
					TS[0] = Ops.GenerateGeneticTestSuite(G, len, false, "BMI");
					endTime = System.nanoTime() /(double)1000000000;
					t1 = endTime - startTime;
					
					t2 = 0;
					startTime = System.nanoTime() /(double)1000000000;
					TS[1] = Ops.GenerateRandomTestSuite(G, len, false);
					endTime = System.nanoTime() /(double)1000000000;
					t2 = endTime - startTime;

					t3 = 0;
					startTime = System.nanoTime() /(double)1000000000;
					TS[2] = Ops.HMethod(G);
					endTime = System.nanoTime() /(double)1000000000;
					t3 = endTime - startTime;

					t4 = 0;
					startTime = System.nanoTime() /(double)1000000000;
					TS[3] = Ops.TransitionTour(G);
					endTime = System.nanoTime() /(double)1000000000;
					t4 = endTime - startTime;

					t5 = 0;
					startTime = System.nanoTime() /(double)1000000000;
					TS[4] = Ops.GenerateGeneticTestSuite(G, len, true, "ITSDm");
					endTime = System.nanoTime() /(double)1000000000;
					t5 = endTime - startTime;

					t6 = 0;
					startTime = System.nanoTime() /(double)1000000000;
					TS[5] = Ops.GenerateGeneticTestSuite(G, len, true, "OTSDm");
					endTime = System.nanoTime() /(double)1000000000;
					t6 = endTime - startTime;

					t7 = 0;
					startTime = System.nanoTime() /(double)1000000000;
					TS[6] = Ops.GenerateGeneticTestSuite(G, len, true, "IOTSDm");
					endTime = System.nanoTime() /(double)1000000000;
					t7 = endTime - startTime;

					t8 = 0;
					startTime = System.nanoTime() /(double)1000000000;
					TS[7] = Ops.GenerateGeneticTestSuite(G, len, true, "Coverage");
					endTime = System.nanoTime() /(double)1000000000;
					t8 = endTime - startTime;
					
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
					killed1 += (double) count[0] / (double)MUT;
					killed2 += (double) count[1] / (double)MUT;
					killed3 += (double) count[2] / (double)MUT;
					killed4 += (double) count[3] / (double)MUT;
					killed5 += (double) count[4] / (double)MUT;
					killed6 += (double) count[5] / (double)MUT;
					killed7 += (double) count[6] / (double)MUT;
					killed8 += (double) count[7] / (double)MUT;
					time1 += t1;
					time2 += t2;
					time3 += t3;
					time4 += t4;
					time5 += t5;
					time6 += t6;
					time7 += t7;
					time8 += t8;

					System.out.println(
							"run " + String.valueOf(size) + " --> "
									+ String.format("%.4f",(double) killed1).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) killed2).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) killed3).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) killed4).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) killed5).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) killed6).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) killed7).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) killed8).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) time1).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) time2).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) time3).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) time4).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) time5).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) time6).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) time7).replace(',', '.') + " --> "
									+ String.format("%.4f",(double) time8).replace(',', '.'));
					System.out.flush();
				}

				try {
					OFile.write(String.valueOf(size) + " & "
							+ String.format("%.4f",(double) killed1 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) killed2 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) killed3 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) killed4 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) killed5 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) killed6 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) killed7 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) killed8 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) time1 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) time2 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) time3 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) time4 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) time5 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) time6 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) time7 / REP).replace(',', '.') + " & "
							+ String.format("%.4f",(double) time8 / REP).replace(',', '.') + " \\\\\n");
					OFile.write("\\hline\n");
					OFile.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}
}
