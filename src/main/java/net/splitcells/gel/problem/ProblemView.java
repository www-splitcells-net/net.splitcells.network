package net.splitcells.gel.problem;

import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.data.piešķiršanas.PiešķiršanasTiešraidesSkats;
import net.splitcells.gel.problem.derived.DerivedSolution;
import net.splitcells.gel.rating.structure.MetaRating;

import java.util.function.Function;

public interface ProblemView extends PiešķiršanasTiešraidesSkats {

    Ierobežojums ierobežojums();

    Piešķiršanas piešķiršanas();

    DerivedSolution atvasinājums(Function<MetaRating, MetaRating> konversija);
}