package net.splitcells.dem.resource.host.interaction;

import net.splitcells.dem.environment.resource.Console;
import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.dem.resource.communication.interaction.Dsui;

import static net.splitcells.dem.Dem.m;
import static net.splitcells.dem.resource.communication.interaction.Dsui.dsui;

public class Domsole extends ResourceI<Dsui> {
    public Domsole() {
        super(() -> dsui(m().configValue(Console.class), m().configValue(MessageFilter.class)));
    }

    public static Dsui domsole() {
        return m().configValue(Domsole.class);
    }
}
