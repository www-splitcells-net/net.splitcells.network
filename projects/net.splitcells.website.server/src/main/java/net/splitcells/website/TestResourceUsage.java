/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website;

import static net.splitcells.dem.utils.ExecutionException.execException;

public class TestResourceUsage {
    public static void main(String... args) {
        try {
            System.out.println(TestResourceUsage.class.getResourceAsStream("/net/splitcells/website/server/index.xml")
                    .readAllBytes());
        } catch (Throwable th) {
            throw execException(th);
        }
    }
}
