package net.splitcells.gel.constraint;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.utils.StreamUtils.ensureSingle;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.rating.structure.MetaRatingI.rflektētsNovērtējums;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;

import java.util.Collection;
import java.util.Optional;

import net.splitcells.dem.data.set.Sets;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.rating.rater.classification.ForAllValueCombinations;
import net.splitcells.gel.rating.rater.classification.RaterBasedOnGrouping;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.rater.Rater;

public class QueryI implements Query {
    public static Query jautājums(Constraint ierobežojums, Optional<Constraint> root) {
        return new QueryI(ierobežojums, list(ierobežojums.injectionGroup()), root);
    }

    public static Query jautājums(Constraint ierobežojums) {
        return new QueryI(ierobežojums, list(ierobežojums.injectionGroup()), Optional.of(ierobežojums));
    }

    public static Query jautājums(Constraint ierobežojums, Collection<GroupId> groups, Constraint root) {
        return new QueryI(ierobežojums, groups, Optional.of(root));
    }

    private final Optional<Constraint> sakne;
    private final Constraint ierobežojums;
    private final Collection<GroupId> grupas;

    public QueryI(Constraint ierobežojums, Collection<GroupId> grupas, Optional<Constraint> sakne) {
        this.ierobežojums = ierobežojums;
        this.grupas = grupas;
        this.sakne = sakne;
    }

    @Override
    public Query priekšVisiem(Rater vērtētājs) {
        var radijumuBaže = ierobežojums
                .childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> child.arguments().size() == 1)
                .filter(child -> child.arguments().get(0).getClass().equals(RaterBasedOnGrouping.class))
                .filter(child -> {
                    final var grupēšana = (RaterBasedOnGrouping) child.arguments().get(0);
                    return grupēšana.arguments().get(0).equals(vērtētājs);
                }).reduce(ensureSingle());
        final var radītasGrupas = Sets.<GroupId>setOfUniques();
        if (radijumuBaže.isPresent()) {
            for (GroupId grupa : grupas) {
                radītasGrupas.addAll
                        (ierobežojums
                                .lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP_ID)
                                .lookup(grupa)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP_ID)
                                .values());
            }
        } else {
            radijumuBaže = Optional.of(ForAlls.priekšVisiem(vērtētājs));
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
    public Query priekšVisiem(Attribute<?> arg) {
        var radijumuBaže
                = ierobežojums.childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var grupešana = (Rater) child.arguments().get(0);
                    final var atribūtuGrupešana = (Rater) grupešana.arguments().get(0);
                    if (atribūtuGrupešana.arguments().size() != 1) {
                        return false;
                    }
                    return arg.equals(atribūtuGrupešana.arguments().get(0));
                }).reduce(ensureSingle());
        final var radītasGrupas = Sets.<GroupId>setOfUniques();
        if (radijumuBaže.isPresent()) {
            for (GroupId grupa : grupas) {
                radītasGrupas.addAll(
                        ierobežojums.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP_ID)
                                .lookup(grupa)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP_ID)
                                .values());
            }
        } else {
            radijumuBaže = Optional.of(ForAlls.priekšVisiem(arg));
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
    public Query priekšVisiem() {
        final var radijumuBaže
                = ierobežojums.childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var grupēšana = (Rater) child.arguments().get(0);
                    final var attributeGrouping = (Rater) grupēšana.arguments().get(0);
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
    public Query tad() {
        throw not_implemented_yet();
    }

    @Override
    public Query tad(Rater vērtētājs) {
        var radijumuBaže
                = ierobežojums.childrenView().stream()
                .filter(child -> Then.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> child.arguments().get(0).equals(vērtētājs))
                .reduce(ensureSingle());
        final var resultingGroups = Sets.<GroupId>setOfUniques();
        if (radijumuBaže.isPresent()) {
            for (GroupId grupa : grupas) {
                resultingGroups.addAll(
                        ierobežojums.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP_ID)
                                .lookup(grupa)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP_ID)
                                .values());
            }
        } else {
            radijumuBaže = Optional.of(Then.tad(vērtētājs));
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
    public Query tad(Rating novērtējums) {
        return tad(constantRater(novērtējums));
    }

    @Override
    public Query priekšVisamKombinācijam(Attribute<?>... args) {
        final Constraint radijumuBaže
                = ierobežojums.childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var grupešana = (Rater) child.arguments().get(0);
                    final var atribūtuGrupešana = (Rater) grupešana.arguments().get(0);
                    if (!atribūtuGrupešana.type().equals(ForAllValueCombinations.class)) {
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
        final var radītasGrupas = Sets.<GroupId>setOfUniques();
        for (GroupId grupa : grupas) {
            radītasGrupas.addAll(
                    ierobežojums.lineProcessing()
                            .columnView(Constraint.INCOMING_CONSTRAINT_GROUP_ID)
                            .lookup(grupa)
                            .columnView(Constraint.RESULTING_CONSTRAINT_GROUP_ID)
                            .values());
        }
        if (sakne.isEmpty()) {
            return jautājums(radijumuBaže, radītasGrupas, radijumuBaže);
        } else {
            return jautājums(radijumuBaže, radītasGrupas, sakne.get());
        }
    }

    @Override
    public Rating novērtējums() {
        final var grupasNovērtējums
                    = grupas.stream().map(group -> ierobežojums.novērtējums(group)).reduce((a, b) -> a.kombinē(b));
        if (grupasNovērtējums.isPresent()) {
            return grupasNovērtējums.get();
        }
        return rflektētsNovērtējums();
    }

    @Override
    public Constraint ierobežojums() {
        return ierobežojums;
    }

    @Override
    public Optional<Constraint> sakne() {
        return sakne;
    }
}
