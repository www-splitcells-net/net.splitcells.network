package net.splitcells.gel.kodols.atrisinājums;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.host.DocumentsPath;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.tips.PriekšVisiem;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.kodols.problēma.ProblēmasSkats;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.Vērtētājs;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators.PriekšVisiemAtribūtsVērtībam;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators.PriekšVisiemVērtībasKombinācija;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators.VērtētājsBalstītsUzGrupēšana;
import net.splitcells.gel.kodols.atrisinājums.vēsture.Vēsture;
import org.w3c.dom.Element;

import java.nio.file.Path;

import static com.google.common.collect.Streams.concat;
import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.lang.Xml.toPrettyWithoutHeaderString;
import static net.splitcells.dem.resource.host.Files.*;
import static net.splitcells.gel.kodols.novērtējums.tips.Cena.cena;

public interface AtrisinājumaSkats extends ProblēmasSkats {

    default List<List<Ierobežojums>> brīvasGrupas() {
        return brīvasGrupas(ierobežojums(), list());
    }

    default List<List<Ierobežojums>> brīvasGrupas(Ierobežojums ierobežojums, List<Ierobežojums> vecākuTaka) {
        final var ierobežojumuTaka = vecākuTaka.shallowCopy().withAppended(ierobežojums);
        final List<List<Ierobežojums>> brīvasGrupas = list();
        ierobežojums.casted(PriekšVisiem.class)
                .ifPresent(priekšVisiiemIerobežojums -> {
                    final var priekšVisiiemAtribūti = prieksVisiemAtribūtuNoGrupetājs
                            (priekšVisiiemIerobežojums.grupešana())
                            .withAppended(
                                    priekšVisiiemIerobežojums.grupešana()
                                            .casted(VērtētājsBalstītsUzGrupēšana.class)
                                            .map(e -> prieksVisiemAtribūtuNoGrupetājs(e.grupetājs()))
                                            .orElseGet(() -> list())
                            );
                    if (priekšVisiiemAtribūti.stream()
                            .anyMatch(attribute -> prasība().nosaukumuSkats().contains(attribute))
                    ) {
                        brīvasGrupas.add(ierobežojumuTaka);
                    }
                });
        ierobežojums.skatsUsBerniem().stream()
                .map(bērns -> brīvasGrupas(bērns, ierobežojumuTaka))
                .forEach(brīvasGrupas::addAll);
        return brīvasGrupas;
    }

    private static List<Atribūts<?>> prieksVisiemAtribūtuNoGrupetājs(Vērtētājs grupetājs) {
        final List<Atribūts<?>> priekšVisiemAtribūtieNoGrupas = list();
        grupetājs.casted(PriekšVisiemAtribūtsVērtībam.class)
                .ifPresent(e -> priekšVisiemAtribūtieNoGrupas.add(e.atribūti()));
        grupetājs.casted(PriekšVisiemVērtībasKombinācija.class)
                .ifPresent(e -> priekšVisiemAtribūtieNoGrupas.addAll(e.attributes()));
        return priekšVisiemAtribūtieNoGrupas;
    }

    Vēsture vēsture();

    default Atrisinājums zars() {
        throw not_implemented_yet();
    }

    default boolean irPilns() {
        return prasības_nelietotas().izmērs() == 0 || (piedāvājums_nelietots().izmērs() == 0 && prasības_nelietotas().izmērs() > 0);
    }

    default boolean irOptimāls() {
        return irPilns() && ierobežojums().novērtējums().equalz(cena(0));
    }

    default void veidotStandartaAnalīze() {
        final var standardDokumentuMapu = environment().config().configValue(DocumentsPath.class);
        createDirectory(standardDokumentuMapu);
        final var analīzuKonteiners = standardDokumentuMapu
                .resolve(
                        path()
                                .reduced((a, b) -> a + "." + b)
                                .orElse(getClass().getSimpleName()));
        ensureAbsence(analīzuKonteiners);
        veidotAnalīzu(analīzuKonteiners);
    }

    default void veidotAnalīzu(Path mērķis) {
        createDirectory(mērķis);
        writeToFile(mērķis.resolve("analīye.fods"), uzFodsTabuluAnalīzu());
        writeToFile(mērķis.resolve("ierebežojumu.grafs.xml"), ierobežojums().grafiks());
        writeToFile(mērķis.resolve("ierebežojumu.novērtejums.xml"), ierobežojums().novērtējums().toDom());
        writeToFile(mērķis.resolve("ierebežojumu.stāvoklis.xml"), ierobežojums().toDom());
        writeToFile(mērķis.resolve("vēsture.fods"), vēsture().uzFods());
        writeToFile(mērķis.resolve("stāvoklis.xml"), ierobežojums().argumentācija().toDom());
        writeToFile(mērķis.resolve("radījums.fods"), uzFods());
    }

    default Element uzFodsTabuluAnalīzu() {
        final var fodsTabuluAnalīzu = rElement(NameSpaces.FODS_OFFICE, "document");
        final var analīzuSaturs = element(NameSpaces.FODS_OFFICE, "body");
        fodsTabuluAnalīzu.setAttributeNode(attribute(NameSpaces.FODS_OFFICE, "mimetype", "application/vnd.oasis.opendocument.spreadsheet"));
        fodsTabuluAnalīzu.appendChild(analīzuSaturs);
        {
            final var izklājlapa = element(NameSpaces.FODS_OFFICE, "spreadsheet");
            analīzuSaturs.appendChild(izklājlapa);
            final var tabula = rElement(NameSpaces.FODS_TABLE, "table");
            izklājlapa.appendChild(tabula);
            tabula.setAttributeNode(attribute(NameSpaces.FODS_TABLE, "name", "values"));
            {
                tabula.appendChild(atribūtiNotFodsAnalīzu());
                gūtRindas().stream().
                        map(this::uzRindasFodsAnalīzu)
                        .forEach(e -> tabula.appendChild(e));
            }
        }
        return fodsTabuluAnalīzu;
    }

    default Element atribūtiNotFodsAnalīzu() {
        final var atribūti = element(NameSpaces.FODS_TABLE, "table-row");
        nosaukumuSkats().stream().map(att -> att.vārds()).map(attName -> {
            final var tabulasElements = element(NameSpaces.FODS_TABLE, "table-cell");
            final var tabulasVertība = rElement(NameSpaces.FODS_TEXT, "p");
            tabulasElements.appendChild(tabulasVertība);
            tabulasVertība.appendChild(Xml.textNode(attName));
            return tabulasElements;
        }).forEach(attDesc -> atribūti.appendChild(attDesc));
        {
            final var tabulasElements = element(NameSpaces.FODS_TABLE, "table-cell");
            final var tabulasVertība = rElement(NameSpaces.FODS_TEXT, "p");
            tabulasElements.appendChild(tabulasVertība);
            tabulasVertība.appendChild(Xml.textNode("lrindas argumentacija"));
            atribūti.appendChild(tabulasElements);
        }
        {
            final var novērtejums = element(NameSpaces.FODS_TABLE, "table-cell");
            final var novērtejumuVertība = rElement(NameSpaces.FODS_TEXT, "p");
            novērtejums.appendChild(novērtejumuVertība);
            atribūti.appendChild(novērtejums);
            novērtejumuVertība.appendChild(
                    Xml.textNode(
                            toPrettyWithoutHeaderString(
                                    ierobežojums()
                                            .novērtējums()
                                            .toDom()
                            )));
        }
        return atribūti;
    }

    default Element uzRindasFodsAnalīzu(Rinda piešķiršana) {
        final var tabulasRinda = element(NameSpaces.FODS_TABLE, "table-row");
        {
            nosaukumuSkats().stream().map(attribute -> piešķiršana.vērtība(attribute)).map(value -> {
                final var tabulasElements = element(NameSpaces.FODS_TABLE, "table-cell");
                final var tabulasVertība = rElement(NameSpaces.FODS_TEXT, "p");
                tabulasElements.appendChild(tabulasVertība);
                tabulasVertība.appendChild(Xml.textNode(value.toString()));
                return tabulasElements;
            }).forEach(tableCell -> tabulasRinda.appendChild(tableCell));
        }
        {
            final var rindasArgumentacija = element(NameSpaces.FODS_TABLE, "table-cell");
            final var rindasArgumentacijasVertīiba = rElement(NameSpaces.FODS_TEXT, "p");
            tabulasRinda.appendChild(rindasArgumentacija);
            rindasArgumentacija.appendChild(rindasArgumentacijasVertīiba);
            rindasArgumentacijasVertīiba.appendChild(
                    Xml.textNode(
                            toPrettyWithoutHeaderString(
                                    ierobežojums().argumentācija(piešķiršana, ierobežojums().injekcijasGrupa()).toDom())));
        }
        return tabulasRinda;
    }
}

