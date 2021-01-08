package net.splitcells.gel.atrisinājums.optimizācija;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.Valoda;
import net.splitcells.gel.dati.tabula.RindasRādītājs;
import org.w3c.dom.Node;

import java.util.Objects;

import static net.splitcells.dem.lang.Xml.attribute;
import static net.splitcells.dem.lang.Xml.element;

public final class OptimizācijasNotikums implements Domable {

    private final RindasRādītājs prasība;
    private final RindasRādītājs piedāvājums;
    private final SoluTips solisTips;

    public static OptimizācijasNotikums optimizacijasNotikums(SoluTips solisTips, RindasRādītājs prasība, RindasRādītājs piedāvājums) {
        return new OptimizācijasNotikums(solisTips, prasība, piedāvājums);
    }

    private OptimizācijasNotikums(SoluTips solisTips, RindasRādītājs demand, RindasRādītājs supply) {
        this.solisTips = solisTips;
        this.prasība = demand;
        this.piedāvājums = supply;

    }

    public SoluTips soluTips() {
        return solisTips;
    }

    public RindasRādītājs piedāvājums() {
        return piedāvājums;
    }

    public RindasRādītājs prasība() {
        return prasība;
    }

    @Override
    public Node toDom() {
        final var dom = element(getClass().getSimpleName());
        dom.setAttribute(SoluTips.class.getSimpleName(), solisTips.name());
        dom.appendChild(Xml.element(Valoda.PRASĪBA.apraksts(), prasība.toDom()));
        dom.appendChild(Xml.element(Valoda.PIEDĀVĀJUMS.apraksts(), piedāvājums.toDom()));
        return dom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prasība.indekss(), piedāvājums.indekss(), solisTips);
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof OptimizācijasNotikums) {
            final var other = (OptimizācijasNotikums) arg;
            return prasība.equals(other.prasība())
                    && piedāvājums.equals(other.piedāvājums())
                    && solisTips.equals(other.soluTips());
        }
        throw new IllegalArgumentException();
    }
}
