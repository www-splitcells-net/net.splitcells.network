package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.rater.RatingEventI.novērtejumuNotikums;
import static net.splitcells.gel.rating.type.Cost.bezMaksas;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;
import org.w3c.dom.Node;

public class GroupMultiplier implements Rater {
    public static GroupMultiplier groupMultiplier(Rater... groupers) {
        return new GroupMultiplier(groupers);
    }

    private final List<Rater> grupetaji;
    protected final Map<List<GrupaId>, GrupaId> grupuReizinātājs = map();
    private final List<Discoverable> konteksti = list();

    protected GroupMultiplier(Rater... grupetaji) {
        this.grupetaji = list(grupetaji);
    }

    @Override
    public List<Domable> arguments() {
        return grupetaji.mapped(grupetajs -> (Domable) grupetajs);
    }

    @Override
    public void addContext(Discoverable konteksti) {
        this.konteksti.add(konteksti);
    }

    @Override
    public Collection<List<String>> paths() {
        return konteksti.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public RatingEvent vērtē_pēc_papildinājumu
            (Table rindas, Line papildinājums, List<Ierobežojums> bērni, Table novērtējumsPirmsPapildinājumu) {
        final var novērtejumuNotikums = novērtejumuNotikums();
        List<GrupaId> grupešanaNoPapildinajmiem = listWithValuesOf(
                grupetaji.stream()
                        .map(grupetajs -> grupetajs
                                .vērtē_pēc_papildinājumu(rindas, papildinājums, bērni, novērtējumsPirmsPapildinājumu))
                        .map(nn -> nn.papildinājumi())
                        .flatMap(papildinajums -> papildinajums.values().stream())
                        .map(papildumuNovērtējums -> papildumuNovērtējums.radītsIerobežojumuGrupaId())
                        .collect(toList())
        );
        grupuReizinātājs.computeIfAbsent(
                grupešanaNoPapildinajmiem
                , atslēga -> atslēga
                        .reduced((a, b) -> GrupaId.reizinatasGrupas(a, b))
                        .orElseGet(() -> GrupaId.grupa()));
        novērtejumuNotikums.papildinājumi().put(
                papildinājums
                , lokalsNovērtejums()
                        .arIzdalīšanaUz(bērni)
                        .arNovērtējumu(bezMaksas())
                        .arRadītuGrupasId(grupuReizinātājs.get(grupešanaNoPapildinajmiem)));
        return novērtejumuNotikums;
    }

    @Override
    public RatingEvent vērtē_pirms_noņemšana
            (Table rindas, Line noņemšana, List<Ierobežojums> bērni, Table novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public Node argumentacija(GrupaId grupa, Table piešķiršanas) {
        return Xml.textNode(getClass().getSimpleName());
    }

    @Override
    public String uzVienkāršuAprakstu(Line rinda, GrupaId grupa) {
        return grupetaji.stream()
                .map(grupetajis -> grupetajis.toString())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
}
