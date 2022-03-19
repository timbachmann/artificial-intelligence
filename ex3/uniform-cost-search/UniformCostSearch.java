import java.util.*;

public class UniformCostSearch extends SearchAlgorithmBase {

    public static void main(String[] args) {
        new UniformCostSearch(args);
    }

    public UniformCostSearch(String[] args) {
        super(args);
    }

    @Override
    protected ArrayList<Action> run() {
        PriorityQueue<SearchNode> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));
        openList.add(makeRootNode());
        HashSet<State> closed = new HashSet<>();

        while(!openList.isEmpty()) {
            SearchNode searchNode = openList.peek();
            if (!closed.contains(searchNode.state)){
                closed.add(searchNode.state);
                if (stateSpace.isGoal(searchNode.state)) {
                    return extractPath(searchNode);
                }
                for (ActionStatePair actionStatePair : stateSpace.succ(searchNode.state)) {
                    SearchNode newSearchNode = new SearchNode(searchNode, actionStatePair.action, actionStatePair.state, stateSpace);
                    openList.add(newSearchNode);
                    expandedNodes++;
                }
            }
        }
        return null;
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

    static class SearchNode {
        SearchNode parent;
        Action action;
        State state;
        int cost;

        SearchNode(SearchNode parent, Action action, State state, StateSpace stateSpace) {
            this.parent = parent;
            this.action = action;
            this.state = state;
            this.cost = parent.cost + stateSpace.cost(action);
        }

        public SearchNode() {

        }
    }
}
