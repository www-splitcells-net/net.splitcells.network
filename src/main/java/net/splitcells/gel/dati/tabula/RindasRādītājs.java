package net.splitcells.gel.dati.tabula;

import net.splitcells.dem.lang.dom.Domable;
import org.w3c.dom.Node;

import java.util.Optional;

import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.lang.Xml.textNode;

public interface RindasRādītājs extends Domable {
    Tabula konteksts();

    int indekss();

    default Optional<Rinda> interpretē() {
        return interpretē(konteksts());
    }

    @Deprecated
    Optional<Rinda> interpretē(Tabula context);

    @Override
    default Node toDom() {
        final var dom = element(RindasRādītājs.class.getSimpleName());
        final var rinda = interpretē();
        if (rinda.isPresent()) {
            dom.appendChild(rinda.get().toDom());
        } else {
            dom.appendChild(element("indekss", textNode(indekss() + "")));
        }
        return dom;
    }
}
