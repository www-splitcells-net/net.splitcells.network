package net.splitcells.gel.solution.history.refleksija.tips;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.rating.struktūra.Novērtējums;
import org.w3c.dom.Node;

public final class PilnsNovērtejums implements RefleksijaDati<Novērtējums>, Domable {

    public static PilnsNovērtejums pilnsNovērtejums(Novērtējums novērtējums) {
        return new PilnsNovērtejums(novērtējums);
    }

    private final Novērtējums novērtējums;

    private PilnsNovērtejums(Novērtējums novērtējums) {
        this.novērtējums = novērtējums;

    }

    @Override
    public Novērtējums vertība() {
        return novērtējums;
    }

    @Override
	public String toString() {
    	return Xml.toPrettyString(toDom());
	}

	@Override
	public Node toDom() {
    	final var dom = Xml.element(getClass().getSimpleName());
    	dom.appendChild(novērtējums.toDom());
		return dom;
	}
}
