import java.io.*;
import java.util.ArrayList;

import wyautl.core.Automaton;
import wyautl.rw.InferenceRule;
import wyautl.rw.ReductionRule;
import wyautl.rw.SimpleRewriteStrategy;
import wyautl.rw.StrategyRewriter;

public class TestRunner {

    public static final int GRANULARITY = 1000;

	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(args[0]));

		ArrayList<String> lines = new ArrayList<String>();
		String line;
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}

		long count = 0;
		long time = System.currentTimeMillis();
		for(int i=0;i!=lines.size();++i) {
			line = lines.get(i);
			if((count % GRANULARITY) == 0) {
				long old = time;
				time = System.currentTimeMillis();
				old = time - old;
				System.out.print("\rCompleted " + i + " / " + lines.size() + " tests ("+ (old/GRANULARITY) + " ms/check)");
			}
			check(line);
			count ++;
		}
	}

	public static void check(String line) {
		boolean unsat = line.charAt(0) == 'u';
		Parser parser = new Parser(line.substring(2));
		Automaton automaton = new Automaton();
		int root = parser.parse(automaton);
		automaton.setRoot(0, root);

		StrategyRewriter.Strategy<InferenceRule> inferenceStrategy = new SimpleRewriteStrategy<InferenceRule>(
				automaton, Arithmetic.inferences);
		StrategyRewriter.Strategy<ReductionRule> reductionStrategy = new SimpleRewriteStrategy<ReductionRule>(
				automaton, Arithmetic.reductions);
		StrategyRewriter rw = new StrategyRewriter(automaton,
				inferenceStrategy, reductionStrategy, Arithmetic.SCHEMA);
		rw.apply(10000);

		boolean result = automaton.get(automaton.getRoot(0)).equals(Arithmetic.False);
		if(result != unsat) {
			System.out.println("\n\n*** TEST FAILED: " + line + "\n");
		}

		// else if(Arithmetic.numSteps >= Arithmetic.MAX_STEPS) {
		// 	System.out.println("\n\n*** TEST HUNG: " + line + "\n");
		// }
	}
}
