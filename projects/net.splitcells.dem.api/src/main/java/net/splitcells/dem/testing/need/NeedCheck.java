/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing.need;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.ExecutionException;

import java.util.Optional;

import static net.splitcells.dem.testing.need.NeedException.needException;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * <p>Where possible, error checking should be done implicitly.
 * For example, when you insert an element into a {@link Set},
 * use {@link Set#add(Object)} instead of combining {@link Set#has(Object)} and {@link Set#ensureContains(Object)}.
 * </p>
 * <p>This is assertion framework provides an API to assert constraints in code,
 * while minimizing the impact of the assertion code to the legibility of the main code.
 * This is done by splitting the code vertically, where the left side contains the code's main logic and
 * the right side contains the assertion:</p>
 * <pre><code>
 *     createCardDeck()
 *      .stream(isNotEmpty())
 *      .filter(card -> card.hasNumber(), cardDescriptionIsNotEmpty())
 *      .reduce(0, (a,b) -> a.getNumber() + b.getNumber(), numberIsBiggerThanZero())
 *      .orElseThrow(sumIsNotBiggerThan(364));
 * </code></pre>
 * <p>For a function to support the {@link Need} API,
 * one has to provide 1 or more parameters of {@link Need} instances,
 * that are checked via {@link #checkNeed(Need, Object)},
 * where the subject is this.</p>
 * <p>This can also be used to create user facing errors.
 * Keep in mind, that user facing error providers have to ensure,
 * the users only see info, that they are allowed to see.
 * This can be ensured, by only returning or throwing error info in security relevant objects,
 * for which the users have the rights to see them.
 * So, if there is an authentication error, no error info of any user is allowed to be returned or thrown.
 * In other words, only security sensitive code,
 * has to closely monitor, what is allowed to be returned or thrown as a message.</p>
 */
public class NeedCheck {
    private NeedCheck() {
        throw constructorIllegal();
    }

    public static <T> void checkNeed(Need<T> need, T subject) {
        final var defiance = need.checkCompliance(subject);
        if (defiance.isEmpty()) {
            return;
        }
        throw needException(defiance.get());
    }

    public static Optional<NeedException> runWithCheckedNeeds(Runnable run) {
        try {
            run.run();
        } catch (NeedException e) {
            return Optional.of(e);
        }
        return Optional.empty();
    }
}
