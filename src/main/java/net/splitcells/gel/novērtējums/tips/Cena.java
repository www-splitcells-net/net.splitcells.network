package net.splitcells.gel.novērtējums.tips;

import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.order.Ordering.LESSER_THAN;

import java.util.Optional;

import net.splitcells.gel.novērtējums.struktūra.RefleksijaNovērtējums;
import net.splitcells.gel.novērtējums.struktūra.Novērtējums;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.order.Ordering;

public class Cena implements Novērtējums {
	protected static final Comparator<Double> COST_VALUE_COMPARATOR = new Comparator<Double>() {
		@Override
		public int compare(Double a, Double b) {
			return b.compareTo(a);
		}
	};
	private double vertība;

	public static Cena cena(double vertība) {
		return new Cena(vertība);
	}

	public static Cena bezMaksas() {
		return cena(0.0);
	}

	protected Cena(double vertība) {
		this.vertība = vertība;
	}

	public double vertība() {
		return vertība;
	}

	@Override
	public Optional<Ordering> compare_partially_to(Novērtējums arg) {
		if (arg instanceof Cena) {
			return Optional.of(COST_VALUE_COMPARATOR.compareTo(vertība, ((Cena) arg).vertība()));
		}
		if (arg instanceof Optimālums) {
			final Optimālums argOptimālums = ((Optimālums) arg);
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
	public Novērtējums kombinē(Novērtējums... additionalNovērtējumi) {
		if (additionalNovērtējumi.length == 1) {
			final Novērtējums additionalNovērtējums = additionalNovērtējumi[0];
			if (additionalNovērtējums instanceof Cena) {
				final Cena otherCena = (Cena) additionalNovērtējums;
				return cena(vertība + otherCena.vertība);
			}
			if (additionalNovērtējums instanceof RefleksijaNovērtējums) {
				return additionalNovērtējums.kombinē(this);
			}
			if (additionalNovērtējums instanceof Optimālums) {
				final Optimālums additionalOptimiality = (Optimālums) additionalNovērtējums;
				if (additionalOptimiality.vertība() == 1 && vertība == 0) {
					throw not_implemented_yet();
				}
			}
		}
		throw not_implemented_yet();
	}

	@Override
	public boolean equals(Object ob) {
		return compare_partially_to((Novērtējums) ob).get().equals(EQUAL);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends Novērtējums> R _clone() {
		return (R) new Cena(vertība);
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
