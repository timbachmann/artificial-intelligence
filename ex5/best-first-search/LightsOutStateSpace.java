/*
  This file contains the state space definition for the lights-out problem.

  These are the only problem-specific (i.e., specific to the
  lights-out problem) parts of the code; everything else is generic
  and can be used without change for other search problems.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class LightsOutStateSpace implements StateSpace {
    /*
      We make lights-out states and actions private since the search code
      cannot and should not look into the state.
    */

    private static class LightsOutState implements State {
        /*
          A state is represented as a two-dimensional array of booleans, 
          representing the grid of lights, where "true" means the light is on
          and "false" that the light is off. 
        */
        public ArrayList<ArrayList<Boolean>> lights;

        public LightsOutState(ArrayList<ArrayList<Boolean>> lights) {
            this.lights = lights;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof LightsOutState)) {
                return false;
            }
            LightsOutState other = (LightsOutState)o;
            return lights.equals(other.lights);
        }

        @Override
        public int hashCode() {
            return lights.hashCode();
        }

        public String toString() {
            String result = "\n";
            for (int x = 0; x < lights.size(); x++) {
                for (int y = 0; y < lights.get(x).size(); y++) {
                    if (lights.get(x).get(y)) {
                        result += "1 ";
                    } else {
                        result += "0 ";
                    }
                }
              result += "\n";
            }
            return result;
        }
    }

    private static class LightsOutAction implements Action {
        /*
          Every action is defined by the number position of the toggled light
        */

        private int x;
        private int y;
        private int cost;

        public LightsOutAction(int x, int y, int cost) {
            this.x = x;
            this.y = y;
            this.cost = cost;
        }

        public int cost() {
            return cost;
        }

        public String toString() {
            return "Toggle (" + x + ", " + y + ")";
        }
    }

    /*
      A state space instance must store the initial state and the goal state. 
      Since the actions are the same for every state, we also store them
      (one action for each light).
    */
    private int dimx;
    private int dimy;
    private LightsOutState initialState;
    private LightsOutAction actions[];

    private LightsOutStateSpace(int dimx, int dimy, LightsOutState initialState) {
        this.dimx = dimx;
        this.dimy = dimy;
        this.initialState = initialState;
        System.out.println("Instantiating lights-out instance...");

        actions = new LightsOutAction[dimx*dimy];
        for (int x = 0; x < dimx; x++) {
            for (int y = 0; y < dimy; y++) {
                int cost = 5;
                if (x == 0 || x == dimy-1) {
                    cost--;
                }
                if (y == 0 || y == dimy-1) {
                    cost--;
                }
                actions[x*dimy+y] = new LightsOutAction(x, y, cost);
            }
        }
    }

    /*
      The following four methods define the interface of state spaces
      used by the search code.

      We use the method names "init", "isGoal", "succ" and "cost" to
      stay as close as possible to the names in the lecture slides.
      Without this constraint, it would be better to use more
      self-explanatory names like "getSuccessorStates" instead of
      "succ".

      All methods are const because the state space itself never
      changes.
    */

    public State init() {
        // Just return the initial state that we stored.
        return initialState;
    }

    public boolean isGoal(State s_) {
        LightsOutState s = (LightsOutState) s_;

        /*
          The (only) goal state of the lights-out problem is the one where
          all lights are off
        */

        for (int x = 0; x < dimx; x++) {
            for (int y = 0; y < dimy; y++) {
                if (s.lights.get(x).get(y)) {
                    return false;
                }
            }
        }

        return true;
    }

    public ArrayList<ActionStatePair> succ(State s_) {
        LightsOutState s = (LightsOutState) s_;

        ArrayList<ActionStatePair> result = new ArrayList<ActionStatePair>();

        for (LightsOutAction action : actions) {
            result.add(createSuccessor(s,action));
        }
        return result;
    }

    private ActionStatePair createSuccessor(LightsOutState s, LightsOutAction action) {
        int x = action.x;
        int y = action.y;
        int dimx = s.lights.size();
        int dimy = s.lights.get(0).size();
        
        ArrayList<ArrayList<Boolean>> newLights = new ArrayList<ArrayList<Boolean>>();
        for (int i = 0; i < dimx; i++) {
            newLights.add(new ArrayList<Boolean>());
            for (int j = 0; j < dimy; j++) {
                 /*
                   If if (i,j) is the cell (x,y) or a neighboring cell, set
                   its value to the negation of the corresponding cell of the
                   predecessor, otherwise copy the value.
                 */
                 if (Math.abs(i - action.x) + Math.abs(j - action.y) <= 1) {
                     newLights.get(i).add(!s.lights.get(i).get(j));
                 } else {
                     newLights.get(i).add(s.lights.get(i).get(j));
                 }
            }
        }

        LightsOutState succ = new LightsOutState(newLights);
        return new ActionStatePair(action,succ);
    }

    public int cost(Action a_) {
        LightsOutAction a = (LightsOutAction) a_;
        return a.cost();
    }

    @Override
    public int h(State s_) {
        LightsOutState s = (LightsOutState) s_;
        int heuristic = 0;
        for (int x = 0; x < dimx; x++) {
            for (int y = 0; y < dimy; y++) {
                if (s.lights.get(x).get(y)) {
                    heuristic++;
                }
            }
        }
        return heuristic;
    }

    /*
      The following method instantiates the state space by reading the
      problem description from a file specified on the command line.
      The lights-out state space is a *parameterized* one (i.e., the
      initial state depends on arguments specified by the user of the
      code).
    */
    public static StateSpace buildFromCmdline(ArrayList<String> args) {
        if (args.size() != 1) {
            Errors.usageError("Need one input file argument.");
        }

        String filename = args.get(0);
        System.out.println("Reading input from file " + filename + "...");
        Scanner scanner;
        try {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            Errors.fileError("Input file not found: " + filename);
            scanner = new Scanner(""); // unreachable; silences compiler
        }

        if(!scanner.hasNextLine()) {
            Errors.fileError("Input file is empty.");
        }
        Scanner lineScanner = new Scanner(scanner.nextLine());

        if(!lineScanner.hasNextInt()) {
            Errors.fileError("Number of rows not specified.");
        }
        int dimx = lineScanner.nextInt();
        if (dimx < 0) {
            Errors.fileError("Invalid x dimension: " + dimx);
        }
        
        if(!lineScanner.hasNextInt()) {
            Errors.fileError("Number of columns not specified.");
        }
        int dimy = lineScanner.nextInt();
        if (dimy < 0) {
            Errors.fileError("Invalid y dimension: " + dimy);
        }

        if(lineScanner.hasNext()) {
            Errors.fileError("First line contains more than number of rows and column.");
        }
        lineScanner.close();

        ArrayList<ArrayList<Boolean>> initialLights = new ArrayList<ArrayList<Boolean>>();
        for (int x = 0; x < dimx; x++) {
            if(!scanner.hasNextLine()) {
                Errors.fileError("Grid description has less than the specified "
            + dimx + " rows.");
            }
            lineScanner = new Scanner(scanner.nextLine());
            initialLights.add(new ArrayList<Boolean>());
            for (int y = 0; y < dimy; y++) {
                if(!lineScanner.hasNextInt()) {
                    Errors.fileError("Grid description wrong: row " + (x+1)
                    + " has less than the specified " + dimy + " columns.");
                }
                int val = lineScanner.nextInt();
                if (val == 1) {
                    initialLights.get(x).add(true);
                } else {
                    initialLights.get(x).add(false);
                }
            }
            if (lineScanner.hasNext()) {
                Errors.fileError("Grid description wrong: row " + (x+1)
                + " has more than the specified " + dimy + " columns.");
            }
            lineScanner.close();
        }
        if (scanner.hasNext()) {
            Errors.fileError("Grid description has more than the specified "
            + dimx + " rows.");
        }
        scanner.close();

        LightsOutState initialState = new LightsOutState(initialLights);
        return new LightsOutStateSpace(dimx, dimy, initialState);
    }
}
