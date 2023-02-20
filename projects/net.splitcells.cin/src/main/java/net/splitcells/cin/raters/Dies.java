package net.splitcells.cin.raters;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;

import static net.splitcells.cin.raters.PlayerValuePersistenceClassifier.playerValuePersistenceClassifier;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Dies {
    public static Rater dies(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return playerValuePersistenceClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate,
                (startPlayerValue, endPlayerValue) -> startPlayerValue != endPlayerValue);
    }

    private Dies() {
        throw constructorIllegal();
    }
}
