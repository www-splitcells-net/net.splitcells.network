package net.splitcells.gel.constraint;

import java.util.Optional;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.rater.Rater;

public interface Query {

	Query forAll(Attribute<?> args);

	Query forAll(Rater classifier);

	Query forAll();

	Query then();

	Query then(Rater rater);

	Query then(Rating rating);

	Query forAllCombinationsOf(Attribute<?>... args);

	Rating rating();

	Constraint constraint();

	Optional<Constraint> root();
}
