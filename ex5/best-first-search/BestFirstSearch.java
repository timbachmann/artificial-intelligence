import java.util.*;

public class BestFirstSearch extends SearchAlgorithmBase {
    private final int gMultiplier;
    private final int hMultiplier;

    public BestFirstSearch(String[] args) {
        super(args);
        if (args.length < 3) {
            Errors.usageError("no g value multiplier given.");
        }
        if (args.length < 4) {
            Errors.usageError("no h value multiplier given.");
        }
        gMultiplier = Integer.parseInt(args[2]);
        hMultiplier = Integer.parseInt(args[3]);
    }

    @Override
    protected ArrayList<Action> run() {
        PriorityQueue<SearchNode> openList = new PriorityQueue<>(Comparator.comparingInt(node -> (gMultiplier * node.cost) + (hMultiplier * node.h)));
        openList.add(makeRootNode());
        HashSet<State> closed = new HashSet<>();

        while(!openList.isEmpty()) {
            SearchNode searchNode = openList.poll();
            if (!closed.contains(searchNode.state)){
                closed.add(searchNode.state);
                expandedNodes++;
                if (stateSpace.isGoal(searchNode.state)) {
                    return extractPath(searchNode);
                }
                for (ActionStatePair actionStatePair : stateSpace.succ(searchNode.state)) {
                    SearchNode newSearchNode = new SearchNode(searchNode, actionStatePair.action, actionStatePair.state, stateSpace);
                    openList.add(newSearchNode);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        BestFirstSearch search = new BestFirstSearch(args);
        search.runSearchAlgorithm();
    }

    public SearchNode makeRootNode() {
        SearchNode root = new SearchNode();
        root.parent = null;
        root.action = null;
        root.state = stateSpace.init();
        root.cost = 0;
        return root;
    }

    public ArrayList<Action> extractPath(SearchNode node) {
        ArrayList<Action> path = new ArrayList<>();
        while (node.parent != null) {
            path.add(node.action);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    class SearchNode {
        SearchNode parent;
        Action action;
        State state;
        int cost;
        int h;

        SearchNode(SearchNode parent, Action action, State state, StateSpace stateSpace) {
            this.parent = parent;
            this.action = action;
            this.state = state;
            this.cost = parent.cost + stateSpace.cost(action);
            this.h = stateSpace.h(state);
        }

        public SearchNode() {

        }
    }
}
