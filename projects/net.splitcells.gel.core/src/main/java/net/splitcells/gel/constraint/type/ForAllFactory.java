package net.splitcells.gel.constraint.type;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;

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

    public <T> ForAllI forAllWithValue(Attribute<T> attribute, T value) {
        return ForAllI.forAll(
                forAllWithCondition(line -> value.equals(line.value(attribute))));
    }

    public ForAllI forAll() {
        return ForAllI.forAll(propagation());
    }

    public ForAllI forAll(final Attribute<?> attribute) {
        return ForAllI.forAll(forAllAtributeValues(attribute));
    }

    public ForAllI forAll(Rater classifier) {
        return ForAllI.forAll(classifier);
    }

    public ForAllI forAllCombinations(final Attribute<?>... attributes) {
        return ForAllI.forAll(forAllValueCombinations(attributes));
    }

}
