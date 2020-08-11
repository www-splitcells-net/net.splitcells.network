package net.splitcells.gel.kodols.atrisinājums.vēsture.notikums;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import org.w3c.dom.Node;

import static net.splitcells.gel.kodols.Valoda.*;

public class Piešķiršana implements Domable {
    private final PiešķiršanaMaiņaTips tips;
    private final Rinda prasība;
    private final Rinda piedāvājums;

    public static Piešķiršana piešķiršana(PiešķiršanaMaiņaTips tips, Rinda prasība, Rinda piedāvājums) {
        return new Piešķiršana(tips, prasība, piedāvājums);
    }

    private Piešķiršana(PiešķiršanaMaiņaTips tips, Rinda prasība, Rinda piedāvājums) {
        this.tips = tips;
        this.prasība = prasība;
        this.piedāvājums = piedāvājums;

    }

    public PiešķiršanaMaiņaTips tips() {
        return tips;
    }

    public Rinda demand() {
        return prasība;
    }

    public Rinda supply() {
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
