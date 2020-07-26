package net.splitcells.gel.kodols.problēma;

import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.kodols.dati.piešķiršanas.PiešķiršanasTiešraidesSkats;
import net.splitcells.gel.kodols.problēma.atvasināts.AtvasinātsAtrisinājums;
import net.splitcells.gel.kodols.novērtējums.struktūra.RefleksijaNovērtējums;

import java.util.function.Function;

public interface ProblēmasSkats extends PiešķiršanasTiešraidesSkats {

    Ierobežojums ierobežojums();

    Piešķiršanas piešķiršanas();

    AtvasinātsAtrisinājums atvasinājums(Function<RefleksijaNovērtējums, RefleksijaNovērtējums> konversija);
}