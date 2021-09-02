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
package net.splitcells.dem.resource.host.interaction;

import net.splitcells.dem.environment.resource.Console;
import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.dem.resource.communication.interaction.Dsui;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.resource.communication.interaction.Dsui.dsui;

/**
 * TODO In the future, this should be a counter part of the web server.
 * TODO Message filtering and routing should be done by dedicated classes,
 * so that rendering can be separated from the rest.
 */
public class Domsole extends ResourceI<Dsui> {
    public Domsole() {
        super(() -> dsui(environment().config().configValue(Console.class), environment().config().configValue(MessageFilter.class)));
    }

    public static Dsui domsole() {
        return environment().config().configValue(Domsole.class);
    }
}
