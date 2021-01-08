package net.splitcells.gel.constraint;

import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.lang.Xml.textNode;

import java.util.Optional;

import org.w3c.dom.Element;
import net.splitcells.dem.lang.dom.Domable;

public class GrupaId implements Domable {
    @Deprecated
    public GrupaId() {
        vārds = Optional.empty();
    }

    private GrupaId(String vārds) {
        this.vārds = Optional.ofNullable(vārds);
    }

    private final Optional<String> vārds;

    public static GrupaId grupa() {
        return new GrupaId();
    }

    public static GrupaId grupa(String vārds) {
        return new GrupaId(vārds);
    }

    @Deprecated
    public String gūtVārds() {
        return vārds.get();
    }

    public Optional<String> vārds() {
        return vārds;
    }

    public static GrupaId reizinatasGrupas(GrupaId a, GrupaId b) {
        return new GrupaId(a.toString() + " un " + b.toString());
    }

    @Override
    public String toString() {
        return vārds.orElse(super.toString());
    }

    @Override
    public Element toDom() {
        final var dom = element("grupa");
        if (vārds.isPresent()) {
            dom.appendChild(element("vārds", textNode(vārds.get())));
        }
        dom.appendChild(element("id", textNode(this.hashCode() + "")));
        return dom;
    }
}
