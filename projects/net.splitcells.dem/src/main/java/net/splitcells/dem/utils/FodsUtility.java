package net.splitcells.dem.utils;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import org.w3c.dom.Element;

import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TABLE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TEXT;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * This class provides helper functions in order to create FODS files,
 * which are table sheet files.
 */
@JavaLegacyArtifact
public class FodsUtility {
    private FodsUtility() {
        throw constructorIllegal();
    }

    public static Element tableCell(String cellContent) {
        final var tableCell = elementWithChildren(FODS_TABLE, "table-cell");
        final var cellContentElement = rElement(FODS_TEXT, "p");
        tableCell.appendChild(cellContentElement);
        cellContentElement.appendChild(textNode(cellContent));
        return tableCell;
    }
}
