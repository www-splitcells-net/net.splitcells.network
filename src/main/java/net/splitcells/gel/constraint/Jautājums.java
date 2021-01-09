package net.splitcells.gel.constraint;

import java.util.Optional;

import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.vērtētājs.Vērtētājs;

public interface Jautājums {

	Jautājums priekšVisiem(Atribūts<?> args);

	Jautājums priekšVisiem(Vērtētājs vērtētājs);

	Jautājums priekšVisiem();

	Jautājums tad();

	Jautājums tad(Vērtētājs vērtētājs);

	Jautājums tad(Rating novērtējums);

	Jautājums priekšVisamKombinācijam(Atribūts<?>... args);

	Rating novērtējums();

	Ierobežojums ierobežojums();

	Optional<Ierobežojums> sakne();
}
