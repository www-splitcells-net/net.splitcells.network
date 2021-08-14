/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.constraint;

import java.util.Optional;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
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
