package net.splitcells.gel;

import net.splitcells.dem.Dem;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.resource.host.Files;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.solution.SolutionAspect;
import net.splitcells.gel.solution.Solutions;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.gel.solution.history.HistoryRefFactory;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.resource.host.Files.writeToFile;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public final class GelTest {
    private GelTest() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        GelEnv.process(() -> net.splitcells.dem.testing.Test.main());
    }
}
