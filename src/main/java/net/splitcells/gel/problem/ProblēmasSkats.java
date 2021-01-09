package net.splitcells.gel.problem;

import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.data.piešķiršanas.PiešķiršanasTiešraidesSkats;
import net.splitcells.gel.problem.atvasināts.AtvasinātsSolution;
import net.splitcells.gel.rating.structure.MetaRating;

import java.util.function.Function;

public interface ProblēmasSkats extends PiešķiršanasTiešraidesSkats {

    Ierobežojums ierobežojums();

    Piešķiršanas piešķiršanas();

    AtvasinātsSolution atvasinājums(Function<MetaRating, MetaRating> konversija);
}