package net.splitcells.gel.rating.type;

import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.order.Ordering.LESSER_THAN;

import java.util.Optional;

import net.splitcells.gel.rating.structure.MetaRating;
import net.splitcells.gel.rating.structure.Rating;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.order.Ordering;

public class Cost implements Rating {
	protected static final Comparator<Double> COST_VALUE_COMPARATOR = new Comparator<Double>() {
		@Override
		public int compare(Double a, Double b) {
			return b.compareTo(a);
		}
	};
	private double vertība;

	public static Cost cost(double vertība) {
		return new Cost(vertība);
	}

	public static Cost bezMaksas() {
		return cost(0.0);
	}

	protected Cost(double vertība) {
		this.vertība = vertība;
	}

	public double vertība() {
		return vertība;
	}

	@Override
	public Optional<Ordering> compare_partially_to(Rating arg) {
		if (arg instanceof Cost) {
			return Optional.of(COST_VALUE_COMPARATOR.compareTo(vertība, ((Cost) arg).vertība()));
		}
		if (arg instanceof Optimality) {
			final Optimality argOptimālums = ((Optimality) arg);
			if (argOptimālums.vertība() == 1 && vertība == 0) {
				return Optional.of(EQUAL);
			}
			if (argOptimālums.vertība() == 1 && vertība > 0) {
				return Optional.of(LESSER_THAN);
			}
			return Optional.empty();
		}
		throw new IllegalArgumentException(arg.getClass().getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Rating kombinē(Rating... additionalNovērtējumi) {
		if (additionalNovērtējumi.length == 1) {
			final Rating additionalNovērtējums = additionalNovērtējumi[0];
			if (additionalNovērtējums instanceof Cost) {
				final Cost otherCena = (Cost) additionalNovērtējums;
				return cost(vertība + otherCena.vertība);
			}
			if (additionalNovērtējums instanceof MetaRating) {
				return additionalNovērtējums.kombinē(this);
			}
			if (additionalNovērtējums instanceof Optimality) {
				final Optimality additionalOptimiality = (Optimality) additionalNovērtējums;
				if (additionalOptimiality.vertība() == 1 && vertība == 0) {
					throw not_implemented_yet();
				}
			}
		}
		throw not_implemented_yet();
	}

	@Override
	public boolean equals(Object ob) {
		return compare_partially_to((Rating) ob).get().equals(EQUAL);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends Rating> R _clone() {
		return (R) new Cost(vertība);
	}

	@Override
	public Element toDom() {
		final var dom = element(this.getClass().getSimpleName());
		dom.appendChild(Xml.textNode("" + vertība));
		return dom;
	}

	@Override
	public String toString() {
		return Xml.toPrettyString(toDom());
	}
}
