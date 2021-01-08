package net.splitcells.gel.novērtējums.vērtētājs.klasifikators;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.ierobežojums.GrupaId.grupa;
import static net.splitcells.gel.novērtējums.vērtētājs.NovērtējumsNotikumsI.novērtejumuNotikums;
import static net.splitcells.gel.novērtējums.tips.Cena.bezMaksas;
import static net.splitcells.gel.novērtējums.struktūra.VietējieNovērtējumsI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.dati.tabula.Rinda;
import net.splitcells.gel.dati.tabula.Tabula;
import net.splitcells.gel.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.ierobežojums.GrupaId;
import net.splitcells.gel.ierobežojums.Ierobežojums;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.novērtējums.vērtētājs.Vērtētājs;
import net.splitcells.gel.novērtējums.vērtētājs.NovērtējumsNotikums;

@Deprecated
public class PriekšVisiemAtribūtsVērtībam implements Vērtētājs {
    public static PriekšVisiemAtribūtsVērtībam priekšVisiemAtribūtuVertības(final Atribūts<?> arg) {
        return new PriekšVisiemAtribūtsVērtībam(arg);
    }

    private final Atribūts<?> atribūts;

    protected PriekšVisiemAtribūtsVērtībam(final Atribūts<?> atribūts) {
        this.atribūts = atribūts;
    }

    protected final Map<GrupaId, Map<Object, GrupaId>> grupa = map();
    private final List<Discoverable> konteksti = list();

    public Atribūts<?> atribūti() {
        return atribūts;
    }

    @Override
    public NovērtējumsNotikums vērtē_pēc_papildinājumu
            (Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final var grupēšanasVertība = papildinājums.vērtība(Ierobežojums.RINDA).vērtība(atribūts);
        final var ienākošasGrupasId = papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID);
        if (!grupa.containsKey(ienākošasGrupasId)) {
            grupa.put(ienākošasGrupasId, map());
        }
        if (!grupa.get(ienākošasGrupasId).containsKey(grupēšanasVertība)) {
            grupa.get(ienākošasGrupasId).put(grupēšanasVertība, GrupaId.grupa(grupēšanasVertība.toString()));
        }
        final var novērtejumuNotikums = novērtejumuNotikums();
        novērtejumuNotikums.papildinājumi().put(papildinājums
                , lokalsNovērtejums()
                        .arIzdalīšanaUz(bērni)
                        .arNovērtējumu(bezMaksas())
                        .arRadītuGrupasId(grupa.get(ienākošasGrupasId).get(grupēšanasVertība))
        );
        return novērtejumuNotikums;
    }

    @Override
    public NovērtējumsNotikums vērtē_pirms_noņemšana
            (Tabula rindas, Rinda noņemšana, List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public List<Domable> arguments() {
        return list(atribūts);
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
        final var argumentācia = Xml.element("priekš-visiem");
        argumentācia.appendChild(
                Xml.element("vārds"
                        , Xml.textNode(getClass().getSimpleName())));
        argumentācia.appendChild(atribūts.toDom());
        return argumentācia;
    }

    @Override
    public String uzVienkāršuAprakstu(Rinda rinda, GrupaId grupa) {
        return "priekš visiem " + atribūts.vārds();
    }

    @Override
    public void addContext(Discoverable kontexsts) {
        konteksti.add(kontexsts);
    }

    @Override
    public Collection<List<String>> paths() {
        return konteksti.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Class<? extends Vērtētājs> type() {
        return PriekšVisiemAtribūtsVērtībam.class;
    }
    
    @Override
    public String toString() {
        return PriekšVisiemAtribūtsVērtībam.class.getSimpleName() + "-" + atribūts.vārds();
    }
}