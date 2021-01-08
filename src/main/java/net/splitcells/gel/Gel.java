package net.splitcells.gel;

import net.splitcells.gel.atrisinājums.AtrisinājumBūvētājs;
import net.splitcells.gel.problēma.Definē_prasības_nosaukumu;

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

    public static Definē_prasības_nosaukumu definē_problēmu() {
        return AtrisinājumBūvētājs.definē_problēmu();
    }
}