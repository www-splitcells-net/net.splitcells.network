package net.splitcells.gel.solution.optimization.space;

import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

public class EnumerableOptimizationSpaceI implements EnumerableOptimizationSpace {
    
    public static EnumerableOptimizationSpace enumerableOptimizationSpace
            (Solution solution, OnlineOptimization optimization) {
        return new EnumerableOptimizationSpaceI();
    }
    
    private EnumerableOptimizationSpaceI() {
        
    }
    
    @Override
    public EnumerableOptimizationSpace child(int index) {
        return null;
    }

    @Override
    public int childrenCount() {
        return 0;
    }

    @Override
    public EnumerableOptimizationSpace parent() {
        return null;
    }

    @Override
    public SolutionView currentState() {
        return null;
    }

    @Override
    public Solution endDiscovery() {
        return null;
    }
}
