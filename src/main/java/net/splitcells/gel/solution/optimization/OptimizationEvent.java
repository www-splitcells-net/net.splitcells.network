package net.splitcells.gel.solution.optimization;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.Language;
import net.splitcells.gel.data.table.RindasRādītājs;
import org.w3c.dom.Node;

import java.util.Objects;

import static net.splitcells.dem.lang.Xml.attribute;
import static net.splitcells.dem.lang.Xml.element;

public final class OptimizationEvent implements Domable {

    private final RindasRādītājs prasība;
    private final RindasRādītājs piedāvājums;
    private final StepType solisTips;

    public static OptimizationEvent optimizacijasNotikums(StepType solisTips, RindasRādītājs prasība, RindasRādītājs piedāvājums) {
        return new OptimizationEvent(solisTips, prasība, piedāvājums);
    }

    private OptimizationEvent(StepType solisTips, RindasRādītājs demand, RindasRādītājs supply) {
        this.solisTips = solisTips;
        this.prasība = demand;
        this.piedāvājums = supply;

    }

    public StepType soluTips() {
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
        dom.setAttribute(StepType.class.getSimpleName(), solisTips.name());
        dom.appendChild(Xml.element(Language.PRASĪBA.apraksts(), prasība.toDom()));
        dom.appendChild(Xml.element(Language.PIEDĀVĀJUMS.apraksts(), piedāvājums.toDom()));
        return dom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prasība.indekss(), piedāvājums.indekss(), solisTips);
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof OptimizationEvent) {
            final var other = (OptimizationEvent) arg;
            return prasība.equals(other.prasība())
                    && piedāvājums.equals(other.piedāvājums())
                    && solisTips.equals(other.soluTips());
        }
        throw new IllegalArgumentException();
    }
}
