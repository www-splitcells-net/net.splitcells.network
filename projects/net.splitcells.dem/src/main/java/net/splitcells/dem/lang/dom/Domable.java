package net.splitcells.dem.lang.dom;

import org.w3c.dom.Node;

/**
 * XML descriptions for instances of this interfaces can be created.
 * This can be seen as an alternative to String.
 */
public interface Domable {
    Node toDom();
}
