package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.rating.structure.Rating;
import org.w3c.dom.Node;

public final class CompleteRating implements MetaData<Rating>, Domable {

    public static CompleteRating pilnsNovērtejums(Rating novērtējums) {
        return new CompleteRating(novērtējums);
    }

    private final Rating novērtējums;

    private CompleteRating(Rating novērtējums) {
        this.novērtējums = novērtējums;

    }

    @Override
    public Rating vertība() {
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
