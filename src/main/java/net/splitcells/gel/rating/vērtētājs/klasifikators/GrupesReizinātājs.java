package net.splitcells.gel.rating.vērtētājs.klasifikators;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.vērtētājs.NovērtējumsNotikumsI.novērtejumuNotikums;
import static net.splitcells.gel.rating.tips.Cena.bezMaksas;
import static net.splitcells.gel.rating.struktūra.VietējieNovērtējumsI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.rating.vērtētājs.Vērtētājs;
import net.splitcells.gel.rating.vērtētājs.NovērtējumsNotikums;
import org.w3c.dom.Node;

public class GrupesReizinātājs implements Vērtētājs {
    public static GrupesReizinātājs groupMultiplier(Vērtētājs... groupers) {
        return new GrupesReizinātājs(groupers);
    }

    private final List<Vērtētājs> grupetaji;
    protected final Map<List<GrupaId>, GrupaId> grupuReizinātājs = map();
    private final List<Discoverable> konteksti = list();

    protected GrupesReizinātājs(Vērtētājs... grupetaji) {
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
    public NovērtējumsNotikums vērtē_pēc_papildinājumu
            (Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
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
    public NovērtējumsNotikums vērtē_pirms_noņemšana
            (Tabula rindas, Rinda noņemšana, List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
        return Xml.textNode(getClass().getSimpleName());
    }

    @Override
    public String uzVienkāršuAprakstu(Rinda rinda, GrupaId grupa) {
        return grupetaji.stream()
                .map(grupetajis -> grupetajis.toString())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
}
