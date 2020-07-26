package net.splitcells.gel.kodols.novērtējums.struktūra;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.order.PartiallyOrdered;

import static net.splitcells.gel.kodols.novērtējums.struktūra.RefleksijaNovērtējumsI.rflektētsNovērtējums;

public interface Novērtējums extends PartiallyOrdered<Novērtējums>, Domable {

    @Returns_this
    <R extends Novērtējums> R kombinē(Novērtējums... additionalNovērtējums);

    default RefleksijaNovērtējums kāReflektētsNovērtējums() {
        return rflektētsNovērtējums().kombinē(this);
    }

    <R extends Novērtējums> R _clone();

    default boolean labākNekā(Novērtējums novērtējums) {
        return smallerThan(novērtējums);
    }
}
