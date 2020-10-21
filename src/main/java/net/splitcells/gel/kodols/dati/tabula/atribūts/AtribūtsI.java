package net.splitcells.gel.kodols.dati.tabula.atribūts;

import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.data.atom.BoolI.bool;

import net.splitcells.dem.environment.config.StaticFlags;
import org.w3c.dom.Element;

import net.splitcells.dem.data.atom.Bool;

import java.util.function.Function;

public final class AtribūtsI<T> implements Atribūts<T> {

    private final Class<T> tips;
    private final String vārds;

    public static <T> Atribūts<T> atribūts(Class<T> tips) {
        return new AtribūtsI<>(tips, tips.getSimpleName());
    }

    public static <T> Atribūts<T> atributs(Class<T> tips, String vārds) {
        return new AtribūtsI<>(tips, vārds);
    }

    private AtribūtsI(Class<T> tips, String vārds) {
        this.tips = tips;
        this.vārds = vārds;
    }

    @Override
    public String vārds() {
        return vārds;
    }

    @Override
    public boolean equals(Object arg) {
        return super.equals(arg);
    }

    @Override
    public Bool irGadījumsNo(Object arg) {
        return bool(tips.isAssignableFrom(arg.getClass()));
    }

    @Override
    public Element toDom() {
        return element(Atribūts.class.getSimpleName()
                , element("vārds", textNode(vārds))
                , element(tips.getSimpleName())
        );
    }

    @Override
    public T deserializēVērtību(String vērtība) {
        throw new UnsupportedOperationException();
    }
}
