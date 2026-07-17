/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.random;

public interface RndSrcF {

    Randomness rnd(Long seed);

    Randomness rnd();

    RndSrcCrypt rndCrypt();
}
