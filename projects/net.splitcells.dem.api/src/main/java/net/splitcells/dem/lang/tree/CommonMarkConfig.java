/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.tree;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class CommonMarkConfig {
    public static CommonMarkConfig commonMarkConfig() {
        return new CommonMarkConfig();
    }

    private @Getter @Setter boolean useBlockQuotesForLongNames = true;

    private CommonMarkConfig() {

    }
}
