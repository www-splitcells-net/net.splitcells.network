/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Optional;

@Accessors(chain = true)
public class License {
    public static License license(String argSpdxLicenseIdentifier) {
        return new License(argSpdxLicenseIdentifier);
    }

    @Getter private final String spdxLicenseIdentifier;
    @Getter @Setter private Optional<String> spdxCopyrightText;

    private License(String argSpdxLicenseIdentifier) {
        spdxLicenseIdentifier = argSpdxLicenseIdentifier;
    }
}
