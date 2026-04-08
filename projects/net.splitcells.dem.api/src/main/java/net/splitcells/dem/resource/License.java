/**SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class License {
    public static License license(String argSpdxIdentifier) {
        return new License(argSpdxIdentifier);
    }

    @Getter private final String spdxIdentifier;
    private License(String argSpdxIdentifier) {
        spdxIdentifier = argSpdxIdentifier;
    }
}
