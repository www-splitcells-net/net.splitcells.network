package net.splitcells.gel.problēma;

import net.splitcells.gel.ierobežojums.Ierobežojums;
import net.splitcells.gel.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.dati.piešķiršanas.PiešķiršanasTiešraidesSkats;
import net.splitcells.gel.problēma.atvasināts.AtvasinātsAtrisinājums;
import net.splitcells.gel.novērtējums.struktūra.RefleksijaNovērtējums;

import java.util.function.Function;

public interface ProblēmasSkats extends PiešķiršanasTiešraidesSkats {

    Ierobežojums ierobežojums();

    Piešķiršanas piešķiršanas();

    AtvasinātsAtrisinājums atvasinājums(Function<RefleksijaNovērtējums, RefleksijaNovērtējums> konversija);
}