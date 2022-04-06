import java.util.Comparator;
import java.util.Optional;

public class HillClimbingWithStagnation {
    protected CombinatorialOptimizationProblem cop;

    private class SearchResult {
        boolean success;
        int steps;

        public SearchResult(boolean success, int steps) {
            this.success = success;
            this.steps = steps;
        }
    }

    public HillClimbingWithStagnation(String args[]) {
        if (args.length == 0) {
            Errors.usageError("no combinatorial optimization problem given");
        }

        if (args[0].equals("8queens")) {
            cop = new EightQueensProblem();
        } else {
            Errors.usageError("unknown combinatorial optimization problem: " + args[0]);
        }
    }

    protected SearchResult search() {
        int steps = 0;
        Configuration current = cop.getInitialCandidate();
        while (steps < 100) {
            Optional<Configuration> next = cop.getNeighbors(current).stream().min(Comparator.comparing(conf -> cop.h(conf)));
            steps++;
            if (next.isEmpty()) {
                return new SearchResult(cop.isSolution(current), steps);
            } else {
                if (cop.isSolution(current)) {
                    return new SearchResult(cop.isSolution(current), steps);
                }
                current = next.get();
            }
        }
        return new SearchResult(cop.isSolution(current), steps);
    }

    public static void main(String args[]) {
        HillClimbingWithStagnation hc = new HillClimbingWithStagnation(args);
        int successfulRuns = 0;
        int steps = 0;
        int runs = 1000;
        for (int i = 0; i < runs; i++) {
            SearchResult result = hc.search();
            if (result.success) {
                successfulRuns++;
            }
            steps += result.steps;
        }
        System.out.println("Percentage of successful runs: "
                           + ((double) successfulRuns)/runs * 100+ "%");
        System.out.println("Average number of steps in successfull runs: "
                           + ((double) steps)/runs);
    }
}
