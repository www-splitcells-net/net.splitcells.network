package net.splitcells.gel.rating.vērtētājs;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.constraint.GrupaId.grupa;
import static net.splitcells.gel.rating.type.Cena.bezMaksas;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.structure.Rating;

public class VērtētājsBalstītaUzRindaVērtība implements Vērtētājs {
    public static Vērtētājs rindasVertībasBalstītasUzGrupetajs(String apraksts, Function<Rinda, Integer> grupetajs) {
        return rindasVertībasBalstītasUzGrupetajs(new Function<>() {
            private final Map<Integer, GrupaId> lineNumbering = map();

            @Override
            public GrupaId apply(Rinda arg) {
                return lineNumbering.computeIfAbsent
                        (grupetajs.apply(arg.vērtība(Ierobežojums.RINDA))
                                , classification -> GrupaId.grupa(apraksts + ": " + classification));
            }

            @Override
            public String toString() {
                return apraksts;
            }
        });
    }

    public static Vērtētājs rindasVertībaBalstītaUzVērtētāju(Function<Rinda, Rating> vērtētājsBalstītsUzRindasVertības) {
        return new VērtētājsBalstītaUzRindaVērtība(vērtētājsBalstītsUzRindasVertības, papildinājums -> papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID));
    }

    public static Vērtētājs rindasVertībasBalstītasUzGrupetajs(Function<Rinda, GrupaId> grupetajsBalstītsUzRindasVertības) {
        return new VērtētājsBalstītaUzRindaVērtība(papilduRinda -> bezMaksas(), grupetajsBalstītsUzRindasVertības);
    }

    private final Function<Rinda, Rating> rindasBalstītsUzVertībasVērtētājs;
    private final Function<Rinda, GrupaId> grupetajsBalstītsUzRindasVertības;
    private final List<Discoverable> konteksts = list();

    private VērtētājsBalstītaUzRindaVērtība(Function<Rinda, Rating> rindasBalstītsUzVertībasVērtētājs, Function<Rinda, GrupaId> grupetajsBalstītsUzRindasVertības) {
        this.rindasBalstītsUzVertībasVērtētājs = rindasBalstītsUzVertībasVērtētājs;
        this.grupetajsBalstītsUzRindasVertības = grupetajsBalstītsUzRindasVertības;
    }

    @Override
    public NovērtējumsNotikums vērtē_pēc_papildinājumu(Tabula rindas, Rinda papildinājums, net.splitcells.dem.data.set.list.List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final NovērtējumsNotikums rVal = NovērtējumsNotikumsI.novērtejumuNotikums();
        rVal.papildinājumi().put
                (papildinājums
                        , lokalsNovērtejums()
                                .arIzdalīšanaUz(bērni)
                                .arRadītuGrupasId(grupetajsBalstītsUzRindasVertības.apply(papildinājums))
                                .arNovērtējumu(rindasBalstītsUzVertībasVērtētājs.apply(papildinājums.vērtība(Ierobežojums.RINDA))));
        return rVal;
    }

    @Override
    public NovērtējumsNotikums vērtē_pirms_noņemšana(Tabula rindas, Rinda noņemšana, net.splitcells.dem.data.set.list.List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        return NovērtējumsNotikumsI.novērtejumuNotikums();
    }

    @Override
    public Class<? extends Vērtētājs> type() {
        return VērtētājsBalstītaUzRindaVērtība.class;
    }

    @Override
    public net.splitcells.dem.data.set.list.List<Domable> arguments() {
        return list(() -> Xml.element
                ("rindasBalstītsUzVertībasVērtētājs"
                        , Xml.textNode(rindasBalstītsUzVertībasVērtētājs.toString())));
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
        final var argumentācija = Xml.element("grupa");
        argumentācija.appendChild
                (Xml.textNode(grupa.vārds().orElse("pazudis-grupas-vards")));
        return argumentācija;
    }

    @Override
    public void addContext(Discoverable konteksts) {
        this.konteksts.add(konteksts);
    }

    @Override
    public Collection<net.splitcells.dem.data.set.list.List<String>> paths() {
        return konteksts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Element toDom() {
        final org.w3c.dom.Element dom = Xml.element(getClass().getSimpleName());
        dom.appendChild(Xml.element("args", arguments().get(0).toDom()));
        return dom;
    }

    @Override
    public String uzVienkāršuAprakstu(Rinda rinda, GrupaId grupa) {
        return grupetajsBalstītsUzRindasVertības.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + rindasBalstītsUzVertībasVērtētājs + ", " + grupetajsBalstītsUzRindasVertības;
    }
}
