/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import java.util.Optional;

@Accessors(chain = true)
public class License {
    public static License parseLicense(String licenseData) {
        Optional<String> licenseId = Optional.empty();
        Optional<String> copyrightText = Optional.empty();
        for (val line : licenseData.split("\\R")) {
            val lineSplit = line.split("=");
            if (lineSplit.length > 1) {
                if ("SPDX-License-Identifier".equals(lineSplit[0])) {
                    licenseId = Optional.of(lineSplit[1]);
                } else if ("SPDX-FileCopyrightText".equals(lineSplit[0])) {
                    copyrightText = Optional.of(lineSplit[1]);
                }
            }
        }
        val license = license();
        if (licenseId.isPresent()) {
            license.setSpdxLicenseIdentifier(licenseId);
        }
        if (copyrightText.isPresent()) {
            license.setSpdxCopyrightText(copyrightText);
        }
        return license;
    }
    public static License license() {
        return new License();
    }

    @Getter @Setter private Optional<String> spdxLicenseIdentifier = Optional.empty();
    @Getter @Setter private Optional<String> spdxCopyrightText = Optional.empty();

    private License() {
        // Nothing as to be done here.
    }
}
