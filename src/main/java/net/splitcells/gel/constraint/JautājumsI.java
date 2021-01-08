package net.splitcells.gel.constraint;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.utils.StreamUtils.ensureSingle;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.rating.struktūra.RefleksijaNovērtējumsI.rflektētsNovērtējums;
import static net.splitcells.gel.rating.vērtētājs.NemainīgsVērtētājs.constantRater;

import java.util.Collection;
import java.util.Optional;

import net.splitcells.dem.data.set.Sets;
import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.constraint.tips.PriekšVisiem;
import net.splitcells.gel.constraint.tips.PriekšVisiemF;
import net.splitcells.gel.constraint.tips.Tad;
import net.splitcells.gel.rating.vērtētājs.klasifikators.PriekšVisiemVērtībasKombinācija;
import net.splitcells.gel.rating.vērtētājs.klasifikators.VērtētājsBalstītsUzGrupēšana;
import net.splitcells.gel.rating.struktūra.Novērtējums;
import net.splitcells.gel.rating.vērtētājs.Vērtētājs;

public class JautājumsI implements Jautājums {
    public static Jautājums jautājums(Ierobežojums ierobežojums, Optional<Ierobežojums> root) {
        return new JautājumsI(ierobežojums, list(ierobežojums.injekcijasGrupa()), root);
    }

    public static Jautājums jautājums(Ierobežojums ierobežojums) {
        return new JautājumsI(ierobežojums, list(ierobežojums.injekcijasGrupa()), Optional.of(ierobežojums));
    }

    public static Jautājums jautājums(Ierobežojums ierobežojums, Collection<GrupaId> groups, Ierobežojums root) {
        return new JautājumsI(ierobežojums, groups, Optional.of(root));
    }

    private final Optional<Ierobežojums> sakne;
    private final Ierobežojums ierobežojums;
    private final Collection<GrupaId> grupas;

    public JautājumsI(Ierobežojums ierobežojums, Collection<GrupaId> grupas, Optional<Ierobežojums> sakne) {
        this.ierobežojums = ierobežojums;
        this.grupas = grupas;
        this.sakne = sakne;
    }

    @Override
    public Jautājums priekšVisiem(Vērtētājs vērtētājs) {
        var radijumuBaže = ierobežojums
                .skatsUsBerniem().stream()
                .filter(child -> PriekšVisiem.class.equals(child.type()))
                .filter(child -> child.arguments().size() == 1)
                .filter(child -> child.arguments().get(0).getClass().equals(VērtētājsBalstītsUzGrupēšana.class))
                .filter(child -> {
                    final var grupēšana = (VērtētājsBalstītsUzGrupēšana) child.arguments().get(0);
                    return grupēšana.arguments().get(0).equals(vērtētājs);
                }).reduce(ensureSingle());
        final var radītasGrupas = Sets.<GrupaId>setOfUniques();
        if (radijumuBaže.isPresent()) {
            for (GrupaId grupa : grupas) {
                radītasGrupas.addAll
                        (ierobežojums
                                .rindasAbstrāde()
                                .kolonnaSkats(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(grupa)
                                .kolonnaSkats(Ierobežojums.RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID)
                                .vertības());
            }
        } else {
            radijumuBaže = Optional.of(PriekšVisiemF.priekšVisiem(vērtētājs));
            ierobežojums.arBērnu(radijumuBaže.get());
            radītasGrupas.addAll(grupas);
        }
        if (sakne.isEmpty()) {
            return jautājums(radijumuBaže.get(), radītasGrupas, radijumuBaže.get());
        } else {
            return jautājums(radijumuBaže.get(), radītasGrupas, sakne.get());
        }
    }

    @Override
    public Jautājums priekšVisiem(Atribūts<?> arg) {
        var radijumuBaže
                = ierobežojums.skatsUsBerniem().stream()
                .filter(child -> PriekšVisiem.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var grupešana = (Vērtētājs) child.arguments().get(0);
                    final var atribūtuGrupešana = (Vērtētājs) grupešana.arguments().get(0);
                    if (atribūtuGrupešana.arguments().size() != 1) {
                        return false;
                    }
                    return arg.equals(atribūtuGrupešana.arguments().get(0));
                }).reduce(ensureSingle());
        final var radītasGrupas = Sets.<GrupaId>setOfUniques();
        if (radijumuBaže.isPresent()) {
            for (GrupaId grupa : grupas) {
                radītasGrupas.addAll(
                        ierobežojums.rindasAbstrāde()
                                .kolonnaSkats(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(grupa)
                                .kolonnaSkats(Ierobežojums.RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID)
                                .vertības());
            }
        } else {
            radijumuBaže = Optional.of(PriekšVisiemF.priekšVisiem(arg));
            ierobežojums.arBērnu(radijumuBaže.get());
            radītasGrupas.addAll(grupas);
        }
        if (sakne.isEmpty()) {
            return jautājums(radijumuBaže.get(), radītasGrupas, radijumuBaže.get());
        } else {
            return jautājums(radijumuBaže.get(), radītasGrupas, sakne.get());
        }
    }

    @Override
    public Jautājums priekšVisiem() {
        final var radijumuBaže
                = ierobežojums.skatsUsBerniem().stream()
                .filter(child -> PriekšVisiem.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var grupēšana = (Vērtētājs) child.arguments().get(0);
                    final var attributeGrouping = (Vērtētājs) grupēšana.arguments().get(0);
                    return attributeGrouping.arguments().isEmpty();
                }).reduce(ensureSingle())
                .get();
        if (sakne.isEmpty()) {
            return jautājums(radijumuBaže, listWithValuesOf(grupas), radijumuBaže);
        } else {
            return jautājums(radijumuBaže, listWithValuesOf(grupas), sakne.get());
        }
    }

    @Override
    public Jautājums tad() {
        throw not_implemented_yet();
    }

    @Override
    public Jautājums tad(Vērtētājs vērtētājs) {
        var radijumuBaže
                = ierobežojums.skatsUsBerniem().stream()
                .filter(child -> Tad.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> child.arguments().get(0).equals(vērtētājs))
                .reduce(ensureSingle());
        final var resultingGroups = Sets.<GrupaId>setOfUniques();
        if (radijumuBaže.isPresent()) {
            for (GrupaId grupa : grupas) {
                resultingGroups.addAll(
                        ierobežojums.rindasAbstrāde()
                                .kolonnaSkats(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                                .uzmeklēšana(grupa)
                                .kolonnaSkats(Ierobežojums.RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID)
                                .vertības());
            }
        } else {
            radijumuBaže = Optional.of(Tad.tad(vērtētājs));
            ierobežojums.arBērnu(radijumuBaže.get());
            resultingGroups.addAll(grupas);
        }
        if (sakne.isEmpty()) {
            return jautājums(radijumuBaže.get(), resultingGroups, radijumuBaže.get());
        } else {
            return jautājums(radijumuBaže.get(), resultingGroups, sakne.get());
        }
    }

    @Override
    public Jautājums tad(Novērtējums novērtējums) {
        return tad(constantRater(novērtējums));
    }

    @Override
    public Jautājums priekšVisamKombinācijam(Atribūts<?>... args) {
        final Ierobežojums radijumuBaže
                = ierobežojums.skatsUsBerniem().stream()
                .filter(child -> PriekšVisiem.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var grupešana = (Vērtētājs) child.arguments().get(0);
                    final var atribūtuGrupešana = (Vērtētājs) grupešana.arguments().get(0);
                    if (!atribūtuGrupešana.type().equals(PriekšVisiemVērtībasKombinācija.class)) {
                        return false;
                    }
                    if (args.length != atribūtuGrupešana.arguments().size()) {
                        return false;
                    }
                    return range(0, args.length)
                            .filter(index -> !args[index].equals(atribūtuGrupešana.arguments().get(index)))
                            .findAny()
                            .isEmpty();
                }).reduce(ensureSingle()).get();
        final var radītasGrupas = Sets.<GrupaId>setOfUniques();
        for (GrupaId grupa : grupas) {
            radītasGrupas.addAll(
                    ierobežojums.rindasAbstrāde()
                            .kolonnaSkats(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                            .uzmeklēšana(grupa)
                            .kolonnaSkats(Ierobežojums.RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID)
                            .vertības());
        }
        if (sakne.isEmpty()) {
            return jautājums(radijumuBaže, radītasGrupas, radijumuBaže);
        } else {
            return jautājums(radijumuBaže, radītasGrupas, sakne.get());
        }
    }

    @Override
    public Novērtējums novērtējums() {
        final var grupasNovērtējums
                    = grupas.stream().map(group -> ierobežojums.novērtējums(group)).reduce((a, b) -> a.kombinē(b));
        if (grupasNovērtējums.isPresent()) {
            return grupasNovērtējums.get();
        }
        return rflektētsNovērtējums();
    }

    @Override
    public Ierobežojums ierobežojums() {
        return ierobežojums;
    }

    @Override
    public Optional<Ierobežojums> sakne() {
        return sakne;
    }
}
