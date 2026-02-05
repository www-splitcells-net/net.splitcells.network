/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing;

import lombok.val;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.dem.utils.MathUtils;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.MathUtils.modulus;

/**
 * <p>Creates names, that can be mapped unambiguously to ints.
 * These names are purely fictional and are not linked to real persons.</p>
 * <p>One could just generate first names for demonstration purposes,
 * but using the last and first name requires a lot less data to create many unique names.
 * Also, last name duplication can be used for presentation jokes.</p>
 */
public class IdentifiedNameGenerator {

    public static IdentifiedNameGenerator identifiedNameGenerator() {
        return new IdentifiedNameGenerator();
    }

    private int firstNameIndex = -1;
    private int lastNameIndex = 0;

    private IdentifiedNameGenerator() {

    }

    private static final int MAX_FIRST_NAME_INDEX = 30;
    private static final int MAX_LAST_NAME_INDEX = 30;

    public String nextName() {
        if (firstNameIndex == MAX_FIRST_NAME_INDEX && lastNameIndex < MAX_LAST_NAME_INDEX) {
            ++lastNameIndex;
        } else if (lastNameIndex == MAX_LAST_NAME_INDEX) {
            throw ExecutionException.execException(tree("Cannot generate more names.")
                    .withProperty("firstNameIndex", "" + firstNameIndex)
                    .withProperty("lastNameIndex", "" + lastNameIndex)
                    .withProperty("MAX_FIRST_NAME_INDEX", "" + MAX_FIRST_NAME_INDEX)
                    .withProperty("MAX_LAST_NAME_INDEX", "" + MAX_LAST_NAME_INDEX));
        }
        if (firstNameIndex < MAX_FIRST_NAME_INDEX) {
            ++firstNameIndex;
        } else {
            firstNameIndex = 0;
        }
        return FIRST_NAMES.get(firstNameIndex) + " " + LAST_NAMES.get(lastNameIndex);
    }

    /**
     *
     * @param id There are {@link #FIRST_NAMES} multiplied by  {@link #LAST_NAMES} many names.
     *           The minimum id is 0,
     *           where as the maximum id is ({@link #FIRST_NAMES} - 1) multiplied by ({@link #LAST_NAMES} - 1).
     * @return Returns a name, that corresponds to the given id.
     */
    public String name(int id) {
        return FIRST_NAMES.get(id / LAST_NAMES.size()) + " " + LAST_NAMES.get(modulus(id, LAST_NAMES.size()));
    }

    private static final ListView<String> FIRST_NAMES = list(
            "Ottfried",
            "Joshua",
            "Heike",
            "Magda",
            "Harald",
            "Kira",
            "Laura",
            "Stefanie",
            "Margarete",
            "Herbert",
            "Annika",
            "Alexandra",
            "Max",
            "Mario",
            "Milena",
            "Oliver",
            "Thomas",
            "Henrik",
            "Jens",
            "Leon",
            "Tobias",
            "Dominik",
            "Edgar",
            "Pia",
            "Guenter",
            "Eva",
            "Walter",
            "Christiane",
            "Marianne",
            "Jasmin",
            "Marlon");
    private static final ListView<String> LAST_NAMES = list(
            "Demmin",
            "Amberger",
            "Herschberger",
            "Felger",
            "Ringold",
            "Soller",
            "Nickolaus",
            "Plate",
            "Uthe",
            "Beversdorf",
            "Silberman",
            "Birner",
            "Schlinger",
            "Puffer",
            "Senft",
            "Westfall",
            "Friedlander",
            "Meinholz",
            "Heimann",
            "Benshoff",
            "Hauenstein",
            "Stehl",
            "Lingner",
            "Griff",
            "Manhardt",
            "Brueggen",
            "Huck",
            "Gessert",
            "Stickel",
            "Biehle",
            "Laufer"
    );
}
