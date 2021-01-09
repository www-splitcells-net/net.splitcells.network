package net.splitcells.gel.data.table.atribūts;

import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.data.atom.BoolI.bool;

import org.w3c.dom.Element;

import net.splitcells.dem.data.atom.Bool;

import java.util.function.Function;

public final class AtribūtsI<T> implements Atribūts<T> {

    private final Class<T> tips;
    private final String vārds;
    private final Function<String, T> deserializācija;

    public static <T> Atribūts<T> atribūts(Class<T> tips) {
        return new AtribūtsI<>(tips, tips.getSimpleName());
    }

    public static Atribūts<Integer> integerAtributs(String vārds) {
        return new AtribūtsI<>(Integer.class, vārds, arg -> Integer.valueOf(arg));
    }

    public static Atribūts<String> stringAtributs(String vārds) {
        return new AtribūtsI<>(String.class, vārds, arg -> arg);
    }

    public static <T> Atribūts<T> atributs(Class<T> tips, String vārds) {
        return new AtribūtsI<>(tips, vārds);
    }

    private AtribūtsI(Class<T> tips, String vārds) {
        this(tips, vārds, arg -> {
            throw new UnsupportedOperationException();
        });
    }

    private AtribūtsI(Class<T> tips, String vārds, Function<String, T> deserializācija) {
        this.tips = tips;
        this.vārds = vārds;
        this.deserializācija = deserializācija;
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
        return deserializācija.apply(vērtība);
    }
}
