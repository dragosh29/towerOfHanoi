package com.example.towersofhanoi;

import java.util.ArrayList;
import java.util.List;

public class TowerOfHanoiSolver {

    public static class Move {
        public int disk;
        public char fromRod;
        public char toRod;

        public Move(int disk, char fromRod, char toRod) {
            this.disk = disk;
            this.fromRod = fromRod;
            this.toRod = toRod;
        }
    }

    private List<Move> solutionSteps;

    public TowerOfHanoiSolver() {
        solutionSteps = new ArrayList<>();
    }

    public List<Move> getSolutionSteps(int numDisks) {
        solutionSteps.clear();
        solve(numDisks, 'A', 'C', 'B');
        return solutionSteps;
    }

    private void solve(int n, char fromRod, char toRod, char auxRod) {
        if (n == 1) {
            solutionSteps.add(new Move(1, fromRod, toRod));
            return;
        }
        solve(n - 1, fromRod, auxRod, toRod);
        solutionSteps.add(new Move(n, fromRod, toRod));
        solve(n - 1, auxRod, toRod, fromRod);
    }
}
