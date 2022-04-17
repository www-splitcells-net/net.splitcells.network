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
package net.splitcells.dem.environment.config;

import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.environment.config.framework.OptionI;

import java.util.Optional;

import static net.splitcells.dem.data.atom.Bools.untrue;

public class IsDeterministic extends OptionI<Optional<Bool>> {

    /**
     * Programs are not required to deterministic by default, because performance is more important by default.
     */
    public IsDeterministic() {
        super(() -> Optional.empty());
    }

}
