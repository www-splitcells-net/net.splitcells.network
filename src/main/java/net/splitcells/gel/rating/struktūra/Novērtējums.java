package net.splitcells.gel.rating.struktūra;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.order.PartiallyOrdered;

public interface Novērtējums extends PartiallyOrdered<Novērtējums>, Domable {

    @Returns_this
    <R extends Novērtējums> R kombinē(Novērtējums... additionalNovērtējums);

    default RefleksijaNovērtējums kāReflektētsNovērtējums() {
        return RefleksijaNovērtējumsI.rflektētsNovērtējums().kombinē(this);
    }

    <R extends Novērtējums> R _clone();

    default boolean labākNekā(Novērtējums novērtējums) {
        return smallerThan(novērtējums);
    }
}
