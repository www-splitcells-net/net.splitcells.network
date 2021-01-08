package net.splitcells.gel.atrisinājums.vēsture.refleksija.tips;

import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.novērtējums.struktūra.Novērtējums;
import org.w3c.dom.Node;

public class PiešķiršanaNovērtējums implements RefleksijaDati<Novērtējums> {

    public static PiešķiršanaNovērtējums pieškiršanasNovērtejums(Novērtējums novērtējums) {
        return new PiešķiršanaNovērtējums(novērtējums);
    }

    private final Novērtējums novērtējums;

    private PiešķiršanaNovērtējums(Novērtējums novērtējums) {
        this.novērtējums = novērtējums;
    }

    @Override
    public Novērtējums vertība() {
        return novērtējums;
    }

    @Override
    public Node toDom() {
        final var dom = Xml.element(getClass().getSimpleName());
        dom.appendChild(novērtējums.toDom());
        return dom;
    }
}
