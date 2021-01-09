package net.splitcells.gel.rating.vērtētājs;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.structure.Rating;
import org.w3c.dom.Node;

public class NemainīgsVērtētājs implements Vērtētājs {
    public static Vērtētājs constantRater(Rating novērtējums) {
        return new NemainīgsVērtētājs(novērtējums);
    }

    private final Rating novērtējums;
    private final List<Discoverable> konteksts = list();

    protected NemainīgsVērtētājs(Rating novērtējums) {
        this.novērtējums = novērtējums;
    }

    @Override
    public NovērtējumsNotikums vērtē_pēc_papildinājumu(Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final var novērtejumuNotikums = NovērtējumsNotikumsI.novērtejumuNotikums();
        novērtejumuNotikums.papildinājumi().put(
                papildinājums
                , lokalsNovērtejums()
                        .arIzdalīšanaUz(bērni)
                        .arRadītuGrupasId(papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID))
                        .arNovērtējumu(novērtējums));
        return novērtejumuNotikums;
    }

    @Override
    public NovērtējumsNotikums vērtē_pirms_noņemšana(Tabula rindas, Rinda noņemšana, List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        return NovērtējumsNotikumsI.novērtejumuNotikums();
    }

    @Override
    public Class<? extends Vērtētājs> type() {
        return NemainīgsVērtētājs.class;
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
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
    public String uzVienkāršuAprakstu(Rinda rinda, GrupaId grupa) {
        return "";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
