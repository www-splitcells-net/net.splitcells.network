package net.splitcells.gel;

import net.splitcells.gel.solution.SolutionBuilder;
import net.splitcells.gel.problem.DefineDemandAttributes;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * Project name history:
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

    public static DefineDemandAttributes defineProblem() {
        return SolutionBuilder.defineProblem();
    }
}