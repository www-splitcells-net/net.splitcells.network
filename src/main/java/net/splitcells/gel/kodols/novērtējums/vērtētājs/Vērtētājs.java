package net.splitcells.gel.kodols.novērtējums.vērtētājs;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.kodols.kopīgs.Vārdi.ARGUMENTI;

import net.splitcells.gel.kodols.ierobežojums.tips.struktūra.IerobežojumsAI;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.DiscoverableFromMultiplePathsSetter;
import net.splitcells.dem.utils.reflection.PubliclyConstructed;
import net.splitcells.dem.utils.reflection.PubliclyTyped;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.novērtējums.struktūra.Novērtējums;

public interface Vērtētājs extends PubliclyTyped<Vērtētājs>
        , PubliclyConstructed<Domable>
        , DiscoverableFromMultiplePathsSetter
        , Domable {
    NovērtējumsNotikums vērtē_pēc_papildinājumu(Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu);

    /**
     * @param rindas
     * @param noņemšana
     * @param bērni
     * @param novērtējumsPirmsNoņemšana
     * @return
     * @see Ierobežojums#rēgistrē_pirms_noņemšanas(Rinda)
     */
    @Deprecated
    NovērtējumsNotikums vērtē_pirms_noņemšana(Tabula rindas, Rinda noņemšana, List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana);

    @Override
    default Class<? extends Vērtētājs> type() {
        return Vērtētājs.class;
    }

    default Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
        throw not_implemented_yet(getClass().getName());
    }

    @Override
    default Node toDom() {
        final var dom = Xml.element(getClass().getSimpleName());
        if (!arguments().isEmpty()) {
            dom.appendChild(Xml.element2(ARGUMENTI, arguments().stream().map(arg -> arg.toDom())));
        }
        return dom;
    }

    String uzVienkāršuAprakstu(Rinda rinda, GrupaId grupa);
}
