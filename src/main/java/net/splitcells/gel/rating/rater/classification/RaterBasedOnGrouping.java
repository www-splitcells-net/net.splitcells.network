package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.RatingEventI.novērtejumuNotikums;
import static net.splitcells.gel.rating.type.Cost.bezMaksas;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

public class RaterBasedOnGrouping implements Rater {
    public static RaterBasedOnGrouping raterBasedGrouping(Rater grouping) {
        return new RaterBasedOnGrouping(grouping);
    }

    private final Rater grupetājs;
    private final List<Discoverable> kontekts = list();

    protected RaterBasedOnGrouping(Rater grupetājs) {
        this.grupetājs = grupetājs;
    }

    @Override
    public RatingEvent vērtē_pēc_papildinājumu(Table rindas, Line papildinājums, List<Constraint> bērni, Table novērtējumsPirmsPapildinājumu) {
        final var novērtejumuNotikums = novērtejumuNotikums();
        final var rBase = grupetājs.vērtē_pēc_papildinājumu(rindas, papildinājums, bērni, novērtējumsPirmsPapildinājumu);
        novērtejumuNotikums.noņemšana().addAll(rBase.noņemšana());
        rBase.papildinājumi().forEach((rinda, vietējiasNovērtējums) ->
                novērtejumuNotikums.papildinājumi()
                        .put(rinda
                                , lokalsNovērtejums()
                                        .arIzdalīšanaUz(vietējiasNovērtējums.izdalīUz())
                                        .arNovērtējumu(bezMaksas())
                                        .arRadītuGrupasId
                                                (vietējiasNovērtējums.radītsIerobežojumuGrupaId())));
        return novērtejumuNotikums;
    }

    @Override
    public RatingEvent vērtē_pirms_noņemšana(Table rindas, Line noņemšana, List<Constraint> bērni, Table novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public Node argumentacija(GroupId grupa, Table piešķiršanas) {
        final var reasoning = Xml.element("group-by");
        reasoning.appendChild(grupetājs.argumentacija(grupa, piešķiršanas));
        return reasoning;
    }

    @Override
    public String uzVienkāršuAprakstu(Line rinda, GroupId grupa) {
        return grupetājs.uzVienkāršuAprakstu(rinda, grupa);
    }

    @Override
    public List<Domable> arguments() {
        return list(grupetājs);
    }

    @Override
    public void addContext(Discoverable context) {
        kontekts.add(context);
    }

    @Override
    public Collection<List<String>> paths() {
        return kontekts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Class<? extends Rater> type() {
        return RaterBasedOnGrouping.class;
    }

    public Rater grupetājs() {
        return grupetājs;
    }
}
