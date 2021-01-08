package net.splitcells.gel.rating.struktūra;

import static net.splitcells.gel.rating.tips.Cena.bezMaksas;

import net.splitcells.dem.data.set.map.Map;

public interface RefleksijaNovērtējums extends Novērtējums, RatingTulks, RefleksijaRatingSavienoties {
    static RefleksijaNovērtējums neitrāla() {
        final RefleksijaNovērtējums neitrāla = RefleksijaNovērtējumsI.rflektētsNovērtējums();
        neitrāla.kombinē(bezMaksas());
        return neitrāla;
    }

    Map<Class<? extends Novērtējums>, Novērtējums> saturs();

    @SuppressWarnings("unchecked")
    default <T> T gūtSaturuDaļa(Class<? extends T> tips) {
        return (T) saturs().get(tips);
    }

    @Override
    default RefleksijaNovērtējums kāReflektētsNovērtējums() {
        return this;
    }
}
