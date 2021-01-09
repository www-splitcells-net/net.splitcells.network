package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

import net.splitcells.gel.common.Language;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.DiscoverableFromMultiplePathsSetter;
import net.splitcells.dem.utils.reflection.PubliclyConstructed;
import net.splitcells.dem.utils.reflection.PubliclyTyped;

public interface Rater extends PubliclyTyped<Rater>
        , PubliclyConstructed<Domable>
        , DiscoverableFromMultiplePathsSetter
        , Domable {
    RatingEvent vērtē_pēc_papildinājumu(Table rindas, Line papildinājums, List<Constraint> bērni, Table novērtējumsPirmsPapildinājumu);

    /**
     * @param rindas
     * @param noņemšana
     * @param bērni
     * @param novērtējumsPirmsNoņemšana
     * @return
     * @see Constraint#rēgistrē_pirms_noņemšanas(Line)
     */
    @Deprecated
    RatingEvent vērtē_pirms_noņemšana(Table rindas, Line noņemšana, List<Constraint> bērni, Table novērtējumsPirmsNoņemšana);

    @Override
    default Class<? extends Rater> type() {
        return Rater.class;
    }

    default Node argumentacija(GroupId grupa, Table piešķiršanas) {
        throw not_implemented_yet(getClass().getName());
    }

    @Override
    default Node toDom() {
        final var dom = Xml.element(getClass().getSimpleName());
        if (!arguments().isEmpty()) {
            dom.appendChild(Xml.element2(Language.ARGUMENTATION.value(), arguments().stream().map(arg -> arg.toDom())));
        }
        return dom;
    }

    default String uzVienkāršuAprakstu(Line rinda, GroupId grupa) {
        throw not_implemented_yet(getClass().getName());
    }
}
