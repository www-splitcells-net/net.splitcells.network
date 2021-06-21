package net.splitcells.gel.problem;

import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.solution.Solution;

public interface Problem extends Allocations, ProblemView {
	Solution asSolution();
}
