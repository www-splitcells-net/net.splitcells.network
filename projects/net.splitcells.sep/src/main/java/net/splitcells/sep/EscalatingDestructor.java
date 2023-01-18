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
package net.splitcells.sep;

import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * This optimizer processes a given
 */
public class EscalatingDestructor {

    public static EscalatingDestructor escalatingDestructor() {
        return new EscalatingDestructor();
    }

    private EscalatingDestructor() {
    }

    public void optimize(SolutionConnection dependentToOriginal) {
        throw notImplementedYet();
    }
}