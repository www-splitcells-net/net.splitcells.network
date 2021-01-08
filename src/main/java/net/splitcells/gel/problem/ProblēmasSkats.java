package net.splitcells.gel.problem;

import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.data.piešķiršanas.PiešķiršanasTiešraidesSkats;
import net.splitcells.gel.problem.atvasināts.AtvasinātsOptimization;
import net.splitcells.gel.rating.struktūra.RefleksijaNovērtējums;

import java.util.function.Function;

public interface ProblēmasSkats extends PiešķiršanasTiešraidesSkats {

    Ierobežojums ierobežojums();

    Piešķiršanas piešķiršanas();

    AtvasinātsOptimization atvasinājums(Function<RefleksijaNovērtējums, RefleksijaNovērtējums> konversija);
}