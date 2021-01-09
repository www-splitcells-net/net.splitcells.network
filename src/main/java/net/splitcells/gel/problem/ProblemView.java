package net.splitcells.gel.problem;

import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.allocation.AllocationsLiveView;
import net.splitcells.gel.problem.derived.DerivedSolution;
import net.splitcells.gel.rating.structure.MetaRating;

import java.util.function.Function;

public interface ProblemView extends AllocationsLiveView {

    Ierobežojums ierobežojums();

    Allocations piešķiršanas();

    DerivedSolution atvasinājums(Function<MetaRating, MetaRating> konversija);
}