/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.cin.deprecated.raters.deprecated;

import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.rater.framework.Rater;

import static net.splitcells.cin.deprecated.raters.deprecated.PlayerValuePersistenceClassifier.playerValuePersistenceClassifier;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Dies {
    public static Rater dies(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return playerValuePersistenceClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate,
                (startPlayerValues, endPlayerValues)
                        -> startPlayerValues.anyMatch(s -> s.value(playerAttribute) == playerValue)
                        && endPlayerValues.noneMatch(e -> e.value(playerAttribute) == playerValue)
                , "dies");
    }

    private Dies() {
        throw constructorIllegal();
    }
}
