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
    public static Optional<License> parseLicense(String licenseData) {
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
        if (licenseId.isPresent()) {
            val license = License.license(licenseId.get());
            if (copyrightText.isPresent()) {
                license.setSpdxCopyrightText(copyrightText);
            }
            return Optional.of(license);
        }
        return Optional.empty();
    }
    public static License license(String argSpdxLicenseIdentifier) {
        return new License(argSpdxLicenseIdentifier);
    }

    @Getter private final String spdxLicenseIdentifier;
    @Getter @Setter private Optional<String> spdxCopyrightText;

    private License(String argSpdxLicenseIdentifier) {
        spdxLicenseIdentifier = argSpdxLicenseIdentifier;
    }
}
