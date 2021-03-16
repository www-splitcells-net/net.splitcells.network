package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.rating.structure.Rating;
import org.w3c.dom.Node;

public final class CompleteRating implements MetaData<Rating>, Domable {

    public static CompleteRating completeRating(Rating rating) {
        return new CompleteRating(rating);
    }

    private final Rating rating;

    private CompleteRating(Rating rating) {
        this.rating = rating;

    }

    @Override
    public Rating value() {
        return rating;
    }

    @Override
	public String toString() {
    	return Xml.toPrettyString(toDom());
	}

	@Override
	public Node toDom() {
    	final var dom = Xml.elementWithChildren(getClass().getSimpleName());
    	dom.appendChild(rating.toDom());
		return dom;
	}
}
