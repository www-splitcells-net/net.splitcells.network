package net.splitcells.gel.solution.history.event;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.data.table.Line;
import org.w3c.dom.Node;

import static net.splitcells.gel.Language.*;

public class Allocation implements Domable {
    private final AllocationChangeType tips;
    private final Line prasība;
    private final Line piedāvājums;

    public static Allocation piešķiršana(AllocationChangeType tips, Line prasība, Line piedāvājums) {
        return new Allocation(tips, prasība, piedāvājums);
    }

    private Allocation(AllocationChangeType tips, Line prasība, Line piedāvājums) {
        this.tips = tips;
        this.prasība = prasība;
        this.piedāvājums = piedāvājums;

    }

    public AllocationChangeType tips() {
        return tips;
    }

    public Line demand() {
        return prasība;
    }

    public Line supply() {
        return piedāvājums;
    }

    @Override
    public Node toDom() {
        final var piešķiršana = Xml.element(PIEŠĶIRŠANA.apraksts());
        piešķiršana.appendChild
                (Xml.element(TIPS.apraksts()).appendChild(Xml.textNode(tips.name())));
        piešķiršana.appendChild
                (Xml.element(PARSĪBA.apraksts()).appendChild(prasība.toDom()));
        piešķiršana.appendChild
                (Xml.element(PIEDĀVĀJUMS.apraksts()).appendChild(piedāvājums.toDom()));
        return piešķiršana;
    }

    @Override
    public String toString() {
        return Xml.toPrettyString(toDom());
    }
}
