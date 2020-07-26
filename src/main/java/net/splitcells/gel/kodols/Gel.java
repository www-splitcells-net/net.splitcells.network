package net.splitcells.gel.kodols;

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
}