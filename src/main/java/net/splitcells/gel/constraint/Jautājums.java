package net.splitcells.gel.constraint;

import java.util.Optional;

import net.splitcells.gel.data.table.atribūts.Atribūts;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.rater.Rater;

public interface Jautājums {

	Jautājums priekšVisiem(Atribūts<?> args);

	Jautājums priekšVisiem(Rater vērtētājs);

	Jautājums priekšVisiem();

	Jautājums tad();

	Jautājums tad(Rater vērtētājs);

	Jautājums tad(Rating novērtējums);

	Jautājums priekšVisamKombinācijam(Atribūts<?>... args);

	Rating novērtējums();

	Ierobežojums ierobežojums();

	Optional<Ierobežojums> sakne();
}
