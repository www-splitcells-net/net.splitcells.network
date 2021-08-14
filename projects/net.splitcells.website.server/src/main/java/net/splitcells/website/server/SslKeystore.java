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
package net.splitcells.website.server;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class SslKeystore extends OptionI<Path> {
    public SslKeystore() {
        super(() -> Paths.get("target/keystore.p12"));
    }
}
