package net.splitcells.gel.rating.rater.klasifikators;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.NovērtējumsNotikumsI.novērtejumuNotikums;
import static net.splitcells.gel.rating.type.Cost.bezMaksas;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

public class VērtētājsBalstītsUzGrupēšana implements Rater {
    public static VērtētājsBalstītsUzGrupēšana raterBasedGrouping(Rater grouping) {
        return new VērtētājsBalstītsUzGrupēšana(grouping);
    }

    private final Rater grupetājs;
    private final List<Discoverable> kontekts = list();

    protected VērtētājsBalstītsUzGrupēšana(Rater grupetājs) {
        this.grupetājs = grupetājs;
    }

    @Override
    public RatingEvent vērtē_pēc_papildinājumu(Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
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
    public RatingEvent vērtē_pirms_noņemšana(Tabula rindas, Rinda noņemšana, List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
        final var reasoning = Xml.element("group-by");
        reasoning.appendChild(grupetājs.argumentacija(grupa, piešķiršanas));
        return reasoning;
    }

    @Override
    public String uzVienkāršuAprakstu(Rinda rinda, GrupaId grupa) {
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
        return VērtētājsBalstītsUzGrupēšana.class;
    }

    public Rater grupetājs() {
        return grupetājs;
    }
}
