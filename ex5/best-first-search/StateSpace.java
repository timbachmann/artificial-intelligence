import java.util.ArrayList;


public interface StateSpace {
    public State init();
    public boolean isGoal(State s);
    public ArrayList<ActionStatePair> succ(State state);
    public int cost(Action a);
    /* Integer.MAX_VALUE is used to represent a heuristic value of infinity. */
    public int h(State s);
}
