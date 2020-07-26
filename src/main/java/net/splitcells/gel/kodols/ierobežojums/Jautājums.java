package net.splitcells.gel.kodols.ierobežojums;

import java.util.Optional;

import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.kodols.novērtējums.struktūra.Novērtējums;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.Vērtētājs;

public interface Jautājums {

	Jautājums priekšVisiem(Atribūts<?> args);

	Jautājums priekšVisiem(Vērtētājs vērtētājs);

	Jautājums priekšVisiem();

	Jautājums tad();

	Jautājums tad(Vērtētājs vērtētājs);

	Jautājums tad(Novērtējums novērtējums);

	Jautājums priekšVisamKombinācijam(Atribūts<?>... args);

	Novērtējums novērtējums();

	Ierobežojums ierobežojums();

	Optional<Ierobežojums> sakne();
}
