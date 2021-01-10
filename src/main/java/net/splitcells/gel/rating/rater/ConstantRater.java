package net.splitcells.gel.rating.rater;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.structure.Rating;
import org.w3c.dom.Node;

public class ConstantRater implements Rater {
    public static Rater constantRater(Rating novērtējums) {
        return new ConstantRater(novērtējums);
    }

    private final Rating novērtējums;
    private final List<Discoverable> konteksts = list();

    protected ConstantRater(Rating novērtējums) {
        this.novērtējums = novērtējums;
    }

    @Override
    public RatingEvent vērtē_pēc_papildinājumu(Table rindas, Line papildinājums, List<Constraint> bērni, Table novērtējumsPirmsPapildinājumu) {
        final var novērtejumuNotikums = RatingEventI.novērtejumuNotikums();
        novērtejumuNotikums.papildinājumi().put(
                papildinājums
                , lokalsNovērtejums()
                        .arIzdalīšanaUz(bērni)
                        .arRadītuGrupasId(papildinājums.value(Constraint.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID))
                        .arNovērtējumu(novērtējums));
        return novērtejumuNotikums;
    }

    @Override
    public RatingEvent vērtē_pirms_noņemšana(Table rindas, Line noņemšana, List<Constraint> bērni, Table novērtējumsPirmsNoņemšana) {
        return RatingEventI.novērtejumuNotikums();
    }

    @Override
    public Class<? extends Rater> type() {
        return ConstantRater.class;
    }

    @Override
    public Node argumentacija(GroupId grupa, Table piešķiršanas) {
        final var argumentācija = Xml.element("nemainīgs-novērtējums");
        argumentācija.appendChild(novērtējums.toDom());
        return argumentācija;
    }

    @Override
    public List<Domable> arguments() {
        return list(novērtējums);
    }

    @Override
    public void addContext(Discoverable context) {
        konteksts.add(context);
    }

    @Override
    public Collection<List<String>> paths() {
        return konteksts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Element toDom() {
        final var dom = Xml.element(getClass().getSimpleName());
        dom.appendChild(novērtējums.toDom());
        return dom;
    }

    @Override
    public String uzVienkāršuAprakstu(Line rinda, GroupId grupa) {
        return "";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
