package net.splitcells.gel.constraint;

import java.util.Optional;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.rater.Rater;

public interface Query {

	Query for_each(Attribute<?> args);

	Query for_each(Rater vērtētājs);

	Query for_all();

	Query then();

	Query then(Rater vērtētājs);

	Query then(Rating novērtējums);

	Query for_all_combinations_of(Attribute<?>... args);

	Rating rating();

	Constraint constraint();

	Optional<Constraint> root();
}
