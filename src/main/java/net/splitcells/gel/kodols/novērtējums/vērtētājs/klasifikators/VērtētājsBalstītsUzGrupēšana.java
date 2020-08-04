package net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.kodols.novērtējums.vērtētājs.NovērtējumsNotikumsI.novērtejumuNotikums;
import static net.splitcells.gel.kodols.novērtējums.tips.Cena.bezMaksas;
import static net.splitcells.gel.kodols.novērtējums.struktūra.VietējieNovērtējumsI.lokalsNovērtejums;

import java.util.Collection;

import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.Vērtētājs;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.NovērtējumsNotikums;

public class VērtētājsBalstītsUzGrupēšana implements Vērtētājs {
    public static VērtētājsBalstītsUzGrupēšana raterBasedGrouping(Vērtētājs grouping) {
        return new VērtētājsBalstītsUzGrupēšana(grouping);
    }

    private final Vērtētājs grupetājs;

    protected VērtētājsBalstītsUzGrupēšana(Vērtētājs grupetājs) {
        this.grupetājs = grupetājs;
    }

    private final List<Discoverable> kontekts = list();

    @Override
    public NovērtējumsNotikums vērtē_pēc_padildinājumu(Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final var novērtejumuNotikums = novērtejumuNotikums();
        final var rBase = grupetājs.vērtē_pēc_padildinājumu(rindas, papildinājums, bērni, novērtējumsPirmsPapildinājumu);
        novērtejumuNotikums.noņemšana().addAll(rBase.noņemšana());
        rBase.papildinājumi().forEach((line, localRating) ->
                novērtejumuNotikums.papildinājumi()
                        .put(line
                                , lokalsNovērtejums()
                                        .arIzdalīšanaUz(bērni)
                                        .arNovērtējumu(bezMaksas())
                                        .arRadītuGrupasId
                                                (localRating.radītsIerobežojumuGrupaId())));
        return novērtejumuNotikums;
    }

    @Override
    public NovērtējumsNotikums vērtē_pirms_noņemšana(Tabula rindas, Rinda noņemšana, List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
        final var reasoning = Xml.element("group-by");
        reasoning.appendChild(grupetājs.argumentacija(grupa, piešķiršanas));
        return reasoning;
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
    public Class<? extends Vērtētājs> type() {
        return VērtētājsBalstītsUzGrupēšana.class;
    }

    public Vērtētājs grupetājs() {
        return grupetājs;
    }
}
