package net.splitcells.gel.problem;

import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.allocation.AllocationsLiveView;
import net.splitcells.gel.problem.derived.DerivedSolution;
import net.splitcells.gel.rating.framework.MetaRating;

import java.util.function.Function;

public interface ProblemView extends AllocationsLiveView {

    Constraint constraint();

    Allocations allocations();

    DerivedSolution derived(Function<MetaRating, MetaRating> derivation);
}