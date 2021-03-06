package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface DefineSupplyAttributes {
    DefineSupply withSupplyAttributes(Attribute<?>... supplyAttributes);

    DefineSupply withSupplyAttributes(List<Attribute<?>> supplyAttributes);

    DefineSupply withSupplyAttributes2(List<Attribute<Object>> supplyAttributes);

    DefineSupply withSupplies(Database supplies);
}
