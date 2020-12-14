package net.splitcells.gel.kodols.ierobežojums.tips.struktūra;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.namespace.NameSpaces.GEL;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.dem.resource.host.interaction.LogLevel.DEBUG;
import static net.splitcells.gel.kodols.Valoda.ARGUMENTĀCIJA;
import static net.splitcells.gel.kodols.Valoda.VĀRDS;
import static net.splitcells.gel.kodols.dati.datubāze.DatuBāzes.datuBāze;
import static net.splitcells.gel.kodols.dati.piešķiršanas.Piešķiršanass.piešķiršanas;
import static net.splitcells.gel.kodols.kopīgs.Vārdi.ARGUMENTI;
import static net.splitcells.gel.kodols.ierobežojums.vidējs.dati.PiešķiršanaNovērtējums.rindasNovērtējums;
import static net.splitcells.gel.kodols.ierobežojums.Ziņojums.report;
import static net.splitcells.gel.kodols.ierobežojums.vidējs.dati.GrupuIzdalīšanaVirziens.routingResult;
import static net.splitcells.gel.kodols.novērtējums.tips.Cena.bezMaksas;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.kodols.Valoda;
import net.splitcells.gel.kodols.ierobežojums.vidējs.dati.PiešķiršanaFiltrs;
import net.splitcells.gel.kodols.ierobežojums.vidējs.dati.PiešķiršanaNovērtējums;
import net.splitcells.gel.kodols.ierobežojums.Ziņojums;
import net.splitcells.gel.kodols.ierobežojums.vidējs.dati.MaršrutēšanaNovērtējums;
import net.splitcells.gel.kodols.novērtējums.struktūra.RefleksijaNovērtējumsI;
import org.assertj.core.api.Assertions;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;
import net.splitcells.gel.kodols.ierobežojums.Jautājums;
import net.splitcells.gel.kodols.ierobežojums.JautājumsI;
import net.splitcells.gel.kodols.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.novērtējums.struktūra.VietējieNovērtējums;
import net.splitcells.gel.kodols.novērtējums.struktūra.RefleksijaNovērtējums;
import net.splitcells.gel.kodols.novērtējums.struktūra.Novērtējums;

@Deprecated
public abstract class IerobežojumsAI implements Ierobežojums {
    private final GrupaId injekcijasGrupas;
    protected final net.splitcells.dem.data.set.list.List<Ierobežojums> bērni = list();
    protected Optional<Discoverable> golvenaisKonteksts = Optional.empty();
    private final List<Discoverable> konteksti = list();
    protected final DatuBāze rindas;
    protected final DatuBāze radījums = datuBāze("results", this, RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID, NOVĒRTĒJUMS, IZDALĪŠANA_UZ);
    protected final Piešķiršanas rindasApstrāde;
    protected final Map<GrupaId, Novērtējums> grupasApstrāde = map();

    protected IerobežojumsAI(GrupaId injekcijasGrupas) {
        this(injekcijasGrupas, "");
    }

    protected IerobežojumsAI(GrupaId injekcijasGrupas, String vārds) {
        this.injekcijasGrupas = injekcijasGrupas;
        rindas = datuBāze(vārds + ".rindas", this, RINDA, IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID);
        rindasApstrāde = piešķiršanas("rindasApstrāde", rindas, radījums);
        rindasApstrāde.abonē_uz_papildinājums(this::izdalīt_papildinajumu);
        rindasApstrāde.abonē_uz_iepriekšNoņemšana(this::izdalīt_noņemšana);
        rindas.abonē_uz_papildinājums(this::apstrāde_rindu_papildinajumu);
    }

    protected IerobežojumsAI() {
        this(Ierobežojums.standartaGrupa());
    }

    @Override
    public GrupaId injekcijasGrupa() {
        return injekcijasGrupas;
    }

    @Override
    public void reģistrē_papildinājums(GrupaId ienākošieGrupasId, Rinda papildinajums) {
        // DARĪT Kustēt uz ārpuses projektu.
        if (TRACING) {
            domsole().append
                    (element("reģistrē_papildinajums." + Ierobežojums.class.getSimpleName()
                            , element("papildinajums", papildinajums.toDom())
                            , element("ienākošieGrupasId", textNode(ienākošieGrupasId.toString()))
                            )
                            , this
                            , DEBUG);
        }
        // DARĪT Kustēt uz ārpuses projektu.
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(
                    rindas
                            .kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                            .uzmeklēšana(ienākošieGrupasId)
                            .kolonnaSkats(RINDA)
                            .uzmeklēšana(papildinajums)
                            .gūtRindas()
            ).isEmpty();
        }
        rindas.pieliktUnPārtulkot(list(papildinajums, ienākošieGrupasId));
    }

    protected abstract void apstrāde_rindu_papildinajumu(Rinda papildinājums);

    @Override
    public void rēgistrē_pirms_noņemšanas(GrupaId ienākošaGrupaId, Rinda noņemšana) {
        // DARĪT Kustēt uz ārpuses projektu.
        if (ENFORCING_UNIT_CONSISTENCY) {
            Assertions.assertThat(noņemšana.irDerīgs()).isTrue();
        }
        apstrāda_rindas_primsNoņemšana(ienākošaGrupaId, noņemšana);
        rindasApstrāde
                .kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                .uzmeklēšana(ienākošaGrupaId)
                .kolonnaSkats(RINDA)
                .uzmeklēšana(noņemšana)
                .gūtRindas()
                .forEach(rindasApstrāde::noņemt);
        // JAUDA
        rindas.jēlaRindasSkats().stream()
                .filter(e -> e != null)
                .filter(line -> line.vērtība(RINDA).equals(noņemšana))
                .filter(line -> line.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID).equals(ienākošaGrupaId))
                .forEach(rindas::noņemt);
    }

    protected void apstrāda_rindas_primsNoņemšana(GrupaId ienākošaGrupaId, Rinda noņemšana) {
        rindasApstrāde.piedāvājums_nelietots().jēlaRindasSkats().stream()
                .filter(e -> e != null)
                .forEach(nelitotsPiedāvājums -> radījums.noņemt(nelitotsPiedāvājums));
    }

    protected void izdalīt_papildinajumu(Rinda papildinajums) {
        papildinajums.vērtība(IZDALĪŠANA_UZ).forEach(child ->
                child.reģistrē_papildinājums
                        (papildinajums.vērtība(RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID)
                                , papildinajums.vērtība(RINDA)));
    }

    protected void izdalīt_noņemšana(Rinda noņemšana) {
        noņemšana.vērtība(IZDALĪŠANA_UZ).forEach(child ->
                child.rēgistrē_pirms_noņemšanas
                        (noņemšana.vērtība(RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID)
                                , noņemšana.vērtība(RINDA)));
    }

    @Override
    public Ierobežojums arBērnu(Ierobežojums... ierobežojumi) {
        asList(ierobežojumi).forEach(ierobežojums -> {
            bērni.add(ierobežojums);
            ierobežojums.addContext(this);
        });
        return this;
    }

    public Set<Rinda> ievērojami(GrupaId grupaId, Set<Rinda> piešķiršanas) {
        final Set<Rinda> ievērojama = setOfUniques();
        piešķiršanas.forEach(piešķiršana -> {
            if (novērtējums(grupaId, piešķiršana.vērtība(RINDA)).equals(bezMaksas())) {
                ievērojama.add(rindasApstrāde.prasība_no_piešķiršana(piešķiršana).vērtība(RINDA));
            }
        });
        return ievērojama;
    }

    public Set<Rinda> neievērotaji(GrupaId grupaId, Set<Rinda> piešķiršanas) {
        final Set<Rinda> neievērotaji = setOfUniques();
        piešķiršanas.forEach(piešķiršana -> {
            if (!novērtējums(grupaId, piešķiršana.vērtība(RINDA)).equals(bezMaksas())) {
                neievērotaji.add(rindasApstrāde.prasība_no_piešķiršana(piešķiršana).vērtība(RINDA));
            }
        });
        return neievērotaji;
    }

    @Override
    public RefleksijaNovērtējums novērtējums(GrupaId grupaId, Rinda rinda) {
        final var novērtetāMaršrutēšana
                = atlasītNovērtetāMaršrutēšana(grupaId, processedLine -> processedLine.vērtība(RINDA).vienāds(rinda));
        novērtetāMaršrutēšana.gūtBērnusUzGrupas().forEach((bērns, grūpa) ->
                grūpa.forEach(group -> novērtetāMaršrutēšana.gūtNovērtējums().add(bērns.novērtējums(group, rinda)))
        );
        if (novērtetāMaršrutēšana.gūtNovērtējums().isEmpty()) {
            return RefleksijaNovērtējumsI.reflektētsNovērtējums(bezMaksas());
        }
        return novērtetāMaršrutēšana.gūtNovērtējums().stream()
                .reduce((a, b) -> a.kombinē(b))
                .get()
                .kāReflektētsNovērtējums();
    }

    protected MaršrutēšanaNovērtējums atlasītNovērtetāMaršrutēšana
            (GrupaId grupaId, Predicate<Rinda> apstrādsRindasAtlasītajs) {
        final var novērtetāMaršrutēšana = MaršrutēšanaNovērtējums.veidot();
        rindasApstrāde.jēlaRindasSkats().forEach(rinda -> {
            if (rinda != null
                    && apstrādsRindasAtlasītajs.test(rinda)
                    && grupaId.equals(rinda.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID))) {
                novērtetāMaršrutēšana.gūtNovērtējums().add(rinda.vērtība(NOVĒRTĒJUMS));
                rinda.vērtība(Ierobežojums.IZDALĪŠANA_UZ).forEach(bērni -> {
                    final Set<GrupaId> groupsOfChild;
                    if (!novērtetāMaršrutēšana.gūtBērnusUzGrupas().containsKey(bērni)) {
                        groupsOfChild = setOfUniques();
                        novērtetāMaršrutēšana.gūtBērnusUzGrupas().put(bērni, groupsOfChild);
                    } else {
                        groupsOfChild = novērtetāMaršrutēšana.gūtBērnusUzGrupas().get(bērni);
                    }
                    groupsOfChild.add(rinda.vērtība(RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID));
                });
            }
        });
        return novērtetāMaršrutēšana;
    }

    @Override
    public RefleksijaNovērtējums novērtējums(GrupaId grupaId) {
        final var novērtetāMaršrutēšana
                = atlasītNovērtetāMaršrutēšana(grupaId, atlasītaRinda -> true);
        novērtetāMaršrutēšana.gūtBērnusUzGrupas().forEach((bērni, grupas) ->
                grupas.forEach(group -> novērtetāMaršrutēšana.gūtNovērtējums().add(bērni.novērtējums(group))));
        if (novērtetāMaršrutēšana.gūtNovērtējums().isEmpty()) {
            return RefleksijaNovērtējumsI.reflektētsNovērtējums(bezMaksas());
        }
        return novērtetāMaršrutēšana.gūtNovērtējums().stream()
                .reduce((a, b) -> a.kombinē(b))
                .get()
                .kāReflektētsNovērtējums();
    }

    /**
     * JAUDA
     */
    @Override
    public List<Ierobežojums> skatsUsBerniem() {
        return listWithValuesOf(bērni);
    }

    public Set<Rinda> piešķiršanaNo(GrupaId grupaId) {
        final Set<Rinda> piešķiršanas = setOfUniques();
        rindasApstrāde.jēlaRindasSkats().forEach(rinda -> {
            if (rinda != null && rinda.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID).equals(grupaId)) {
                piešķiršanas.add(rinda);
            }
        });
        return piešķiršanas;
    }

    public Set<Rinda> izpildītāji(GrupaId grupaId) {
        return ievērojami(grupaId, piešķiršanaNo(grupaId));
    }

    public Set<Rinda> neievērotaji(GrupaId grupaId) {
        return neievērotaji(grupaId, piešķiršanaNo(grupaId));
    }

    @Override
    public Piešķiršanas rindasAbstrāde() {
        return rindasApstrāde;
    }

    public Tabula rindas() {
        return rindas;
    }

    public GrupaId grupaNo(Rinda rinda) {
        return rindasAbstrāde()
                .jēlaRindasSkats()
                .get(rinda.indekss())
                .vērtība(RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID);
    }

    @Override
    public Rinda pieliktRadījums(VietējieNovērtējums vietējieNovērtējums) {
        return radījums.pieliktUnPārtulkot
                (list
                        (vietējieNovērtējums.radītsIerobežojumuGrupaId()
                                , vietējieNovērtējums.novērtējums()
                                , vietējieNovērtējums.izdalīUz()));
    }

    @Override
    public void addContext(Discoverable konteksts) {
        if (golvenaisKonteksts.isEmpty()) {
            golvenaisKonteksts = Optional.of(konteksts);
        }
        konteksti.add(konteksts);
    }

    @Override
    public Collection<net.splitcells.dem.data.set.list.List<String>> paths() {
        return konteksti.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Ierobežojums arBērnu(Function<Jautājums, Jautājums> būvētājs) {
        būvētājs.apply(JautājumsI.jautājums(this, Optional.empty()));
        return this;
    }

    @Override
    public Element toDom() {
        final var dom = Xml.element(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.appendChild(Xml.element(ARGUMENTI, arg.toDom())));
        }
        dom.appendChild(Xml.element("novērtējums", novērtējums().toDom()));
        {
            final var novērtējumi = Xml.element("novērtējumi");
            dom.appendChild(novērtējumi);
            rindasApstrāde.kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                    .uzmeklēšana(injekcijasGrupa())
                    .gūtRindas()
                    .forEach(line -> novērtējumi.appendChild(line.toDom()));
        }
        skatsUsBerniem().forEach(bērns ->
                dom.appendChild(
                        bērns.toDom(
                                setOfUniques(radījums
                                        .kolonnaSkats(RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID)
                                        .vertības()))));
        return dom;
    }

    @Override
    public Element toDom(Set<GrupaId> grupas) {
        final var dom = Xml.element(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.appendChild(Xml.element(ARGUMENTI, arg.toDom())));
        }
        dom.appendChild(Xml.element("novērtējums", rating(grupas).toDom()));
        {
            final var novērtējumi = Xml.element("novērtējumi");
            dom.appendChild(novērtējumi);
            grupas.forEach(grupa ->
                    rindasApstrāde
                            .kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                            .uzmeklēšana(grupa)
                            .gūtRindas().
                            forEach(line -> novērtējumi.appendChild(line.toDom())));
        }
        skatsUsBerniem().forEach(bērns ->
                dom.appendChild(
                        bērns.toDom(
                                grupas.stream().
                                        map(group -> rindasApstrāde
                                                .kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                                .uzmeklēšana(group)
                                                .gūtRindas()
                                                .stream()
                                                .map(groupLines -> groupLines.vērtība(RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID))
                                                .collect(toSet()))
                                        .flatMap(resultingGroupings -> resultingGroupings.stream())
                                        .collect(toSet()))));
        return dom;
    }

    protected abstract List<String> vietēijaDabiskaArgumentācija(Ziņojums ziņojums);

    protected Optional<List<String>> vietēijaDabiskaArgumentācija
            (Rinda rinda, GrupaId grupa, Predicate<PiešķiršanaNovērtējums> piešķiršanaAtlasītājs) {
        final var vietējaArgumentācijas
                = rindasApstrāde
                .kolonnaSkats(RINDA)
                .uzmeklēšana(rinda)
                .kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                .uzmeklēšana(grupa)
                .gūtRindas()
                .stream()
                .filter(piešķiršana -> piešķiršanaAtlasītājs
                        .test(rindasNovērtējums
                                (piešķiršana
                                        , novērtējums(piešķiršana.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID), rinda))))
                .map(piešķiršana -> report
                        (piešķiršana.vērtība(RINDA)
                                , piešķiršana.vērtība(RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID)
                                , piešķiršana.vērtība(NOVĒRTĒJUMS)))
                .map(ziņojums -> vietēijaDabiskaArgumentācija(ziņojums))
                .findFirst();
        return vietējaArgumentācijas;
    }

    @Override
    public Perspective dabiskaArgumentācija(GrupaId grupa) {
        final var vietējiaArgumentācijas = rindasApstrāde
                .kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                .uzmeklēšana(grupa)
                .gūtRindas()
                .stream()
                .map(piešķiršana -> piešķiršana.vērtība(RINDA))
                .map(rinda -> dabiskaArgumentācija(rinda, grupa, PiešķiršanaFiltrs::atlasītArCenu))
                .collect(toList());
        if (vietējiaArgumentācijas.size() == 1) {
            return vietējiaArgumentācijas.get(0);
        }
        final var vietējiaArgumentācija = perspective(ARGUMENTĀCIJA.apraksts(), GEL);
        vietējiaArgumentācijas.stream()
                .forEach(naturalReasoning -> vietējiaArgumentācija.withChild(naturalReasoning));
        return vietējiaArgumentācija;
    }

    @Override
    public Perspective dabiskaArgumentācija(Rinda rinda, GrupaId grupa, Predicate<PiešķiršanaNovērtējums> rindasAtlasītājs) {
        final var vietējiaArgumēntacija = vietēijaDabiskaArgumentācija(rinda, grupa, rindasAtlasītājs);
        final var bērnuArgumēntacija = bērnuArgumēntacija(rinda, grupa, rindasAtlasītājs);
        final var argumentācija = perspective(ARGUMENTĀCIJA.apraksts(), GEL);
        if (vietējiaArgumēntacija.isPresent()) {
            vietējiaArgumēntacija.get().stream()
                    .map(e -> perspective(e, NameSpaces.STRING))
                    .forEach(argumentācija::withChild);
        }
        if (bērnuArgumēntacija.isPresent()) {
            if (bērnuArgumēntacija.get().name().isEmpty()) {
                bērnuArgumēntacija.get().children().stream()
                        .filter(e -> !e.children().isEmpty())
                        .forEach(argumentācija::withChild);
            } else {
                argumentācija.withChild(bērnuArgumēntacija.get());
            }
        }
        return argumentācija;
    }

    protected Optional<Perspective> bērnuArgumēntacija
            (Rinda rinda, GrupaId grupa, Predicate<PiešķiršanaNovērtējums> piešķiršanaAtlasītājs) {
        final var argumēntacijas
                = rindasApstrāde
                .kolonnaSkats(RINDA)
                .uzmeklēšana(rinda)
                .kolonnaSkats(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                .uzmeklēšana(grupa)
                .gūtRindas()
                .stream()
                .filter(piešķiršana
                        -> piešķiršanaAtlasītājs
                        .test(rindasNovērtējums
                                (piešķiršana
                                        , novērtējums(piešķiršana.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID), rinda))))
                .map(piešķiršana -> piešķiršana.vērtība(IZDALĪŠANA_UZ)
                        .stream()
                        .map(propagatedTo ->
                                routingResult
                                        (piešķiršana.vērtība(RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID)
                                                , propagatedTo)))
                .flatMap(e -> e)
                .map(routingResult -> routingResult.izplatītājs().dabiskaArgumentācija(rinda, routingResult.grupa()))
                .collect(toSet());
        if (argumēntacijas.isEmpty()) {
            return Optional.empty();
        }
        if (argumēntacijas.size() == 1) {
            return Optional.of(argumēntacijas.iterator().next());
        }
        final var argumēntacija = perspective(ARGUMENTĀCIJA.apraksts(), GEL);
        argumēntacijas.forEach(argumēntacija::withChild);
        return Optional.of(argumēntacija);
    }

    public Optional<Discoverable> galvenaisKonteksts() {
        return golvenaisKonteksts;
    }
}
