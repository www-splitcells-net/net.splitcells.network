/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.factory;

import net.splitcells.dem.environment.resource.ResourceOptionImpl;

import static net.splitcells.dem.data.set.factory.SetFactoryConfigured.setFactoryConfigured;

public class SetsFactory extends ResourceOptionImpl<SetFactory> {
    public SetsFactory() {
        super(() -> setFactoryConfigured());
    }
}
