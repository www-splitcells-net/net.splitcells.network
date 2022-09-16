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
package net.splitcells.dem.resource;

public enum ContentType {
    CSV("csv"), UTF_8("UTF-8");
    private final String codeName;

    ContentType(String codeName) {
        this.codeName = codeName;
    }

    public String codeName() {
        return codeName;
    }
}
