import java.util.ArrayList;

public interface CombinatorialOptimizationProblem {
    public Configuration getInitialCandidate();
    public ArrayList<Configuration> getNeighbors(Configuration conf);
    public int h(Configuration conf);
    public boolean isSolution(Configuration conf);
};
