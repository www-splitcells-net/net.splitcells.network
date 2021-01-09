package net.splitcells.gel.constraint;

import java.util.Optional;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.rater.Rater;

public interface Query {

	Query priekšVisiem(Attribute<?> args);

	Query priekšVisiem(Rater vērtētājs);

	Query priekšVisiem();

	Query tad();

	Query tad(Rater vērtētājs);

	Query tad(Rating novērtējums);

	Query priekšVisamKombinācijam(Attribute<?>... args);

	Rating novērtējums();

	Constraint ierobežojums();

	Optional<Constraint> sakne();
}
