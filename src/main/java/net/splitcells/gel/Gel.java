package net.splitcells.gel;

import net.splitcells.gel.solution.SolutionBuilder;
import net.splitcells.gel.problem.Define_Demand_Attributes;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * Projektu Nosaukumu Vēsture:
 * <ul>
 *    <li>Gep = Ģenēriskais Piešķirtājs</li>
 *    <li>Gel = Generic Allocator</li>
 *    <li>Gal = Generic Allocator</li>
 *    <li>Nap = Universal Allocation Program</li>
 *    </ul>
 */
public final class Gel {
    private Gel() {
        throw constructorIllegal();
    }

    public static Define_Demand_Attributes definē_problēmu() {
        return SolutionBuilder.define_problem();
    }
}