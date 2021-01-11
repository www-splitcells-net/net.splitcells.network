package net.splitcells.gel.constraint.type;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.classification.ForAllValueCombinations;

import static net.splitcells.gel.rating.rater.classification.ForAllValueCombinations.forAllValueCombinations;
import static net.splitcells.gel.rating.rater.classification.Propagation.propagation;
import static net.splitcells.gel.rating.rater.classification.ForAllWithCondition.forAllWithCondition;
import static net.splitcells.gel.rating.rater.classification.ForAllAttributeValues.forAllAtributeValues;

public class ForAllFactory {
    protected static final ForAllFactory INSTANCE = new ForAllFactory();

    public static ForAllFactory instance() {
        return INSTANCE;
    }

    protected ForAllFactory() {

    }

    public <T> ForAll forAllWithValue(Attribute<T> attribute, T value) {
        return ForAll.forAll(
                forAllWithCondition(line -> value.equals(line.value(attribute))));
    }

    public ForAll forAll() {
        return ForAll.forAll(propagation());
    }

    public ForAll forAll(final Attribute<?> attribute) {
        return ForAll.forAll(forAllAtributeValues(attribute));
    }

    public ForAll forAll(Rater classifier) {
        return ForAll.forAll(classifier);
    }

    public ForAll forAllCombinations(final Attribute<?>... attributes) {
        return ForAll.forAll(forAllValueCombinations(attributes));
    }

}
