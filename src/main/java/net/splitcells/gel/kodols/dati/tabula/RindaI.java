package net.splitcells.gel.kodols.dati.tabula;

import static java.util.Objects.requireNonNull;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.Xml.toFlatString;
import static net.splitcells.dem.data.set.list.Lists.list;

import net.splitcells.gel.kodols.dati.piešķiršanas.Piešķiršanas;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

import java.util.Objects;

public class RindaI implements Rinda {
    private final Tabula konteksts;
    private final int indekss;

    public static Rinda rinda(Tabula konteksts, int indekss) {
        return new RindaI(konteksts, indekss);
    }

    protected RindaI(Tabula konteksts, int indekss) {
        this.konteksts = konteksts;
        this.indekss = indekss;
    }

    @Override
    public <T> T vērtība(Atribūts<T> atribūts) {
        return konteksts.kolonnaSkats(requireNonNull(atribūts)).get(indekss);
    }

    @Override
    public int indekss() {
        return indekss;
    }

    @Override
    public Tabula konteksts() {
        return konteksts;
    }

    @Override
    public boolean equals(Object arg) {
        if (this == arg) {
            return true;
        } else if (arg instanceof Rinda) {
            final var argRinda = (Rinda) arg;
            return indekss() == argRinda.indekss() && konteksts().equals(argRinda.konteksts());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return toFlatString(toDom());
    }

    @Override
    public Element toDom() {
        final var gūtasDomVertība = element(Rinda.class.getSimpleName());
        gūtasDomVertība.appendChild(element("indekss", textNode("" + indekss)));
        konteksts.nosaukumuSkats().forEach(atribūts -> {
            final var vertība = konteksts.kolonnaSkats(atribūts).get(indekss);
            final Node domVertība;
            if (vertība == null) {
                domVertība = textNode("");
            } else {
                if (vertība instanceof Domable) {
                    domVertība = ((Domable) vertība).toDom();
                } else {
                    domVertība = textNode(vertība.toString());
                }
            }
            final var vertībasElements = element("vertība");
            vertībasElements.setAttribute("tips", atribūts.vārds());
            vertībasElements.appendChild(domVertība);
            gūtasDomVertība.appendChild(vertībasElements);
        });
        return gūtasDomVertība;
    }

    @Override
    public int hashCode() {
        return Objects.hash(indekss(), konteksts());
    }

}
