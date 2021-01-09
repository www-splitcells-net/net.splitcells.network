package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.RatingEventI.novērtejumuNotikums;
import static net.splitcells.gel.rating.type.Cost.bezMaksas;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

public class Propagation implements Rater {
    public static Propagation izdalīšana() {
        return new Propagation();
    }

    private Propagation() {
    }

    private final List<Discoverable> konteksti = list();

    @Override
    public RatingEvent vērtē_pēc_papildinājumu
            (Table rindas, Line papildinājums, List<Ierobežojums> bērni, Table novērtējumsPirmsPapildinājumu) {
        final RatingEvent novērtejumuNotikums = novērtejumuNotikums();
        novērtejumuNotikums.papildinājumi().put
                (papildinājums
                        , lokalsNovērtejums()
                                .arIzdalīšanaUz(bērni)
                                .arNovērtējumu(bezMaksas())
                                .arRadītuGrupasId(papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
        return novērtejumuNotikums;
    }

    @Override
    public RatingEvent vērtē_pirms_noņemšana
            (Table rindas, Line noņemšana, List<Ierobežojums> bērni, Table novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public Node argumentacija(GrupaId grupa, Table piešķiršanas) {
        return element("izdalīšana");
    }

    @Override
    public String uzVienkāršuAprakstu(Line rinda, GrupaId grupa) {
        return "";
    }

    @Override
    public List<Domable> arguments() {
        return list();
    }

    @Override
    public void addContext(Discoverable konteksts) {
        this.konteksti.add(konteksts);
    }

    @Override
    public Collection<List<String>> paths() {
        return konteksti.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
