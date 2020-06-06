package net.splitcells.dem.resource.host.interaction;

import net.splitcells.dem.environment.resource.Console;
import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.dem.resource.communication.interaction.Dsui;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.resource.communication.interaction.Dsui.dsui;

public class Domsole extends ResourceI<Dsui> {
    public Domsole() {
        super(() -> dsui(environment().configValue(Console.class), environment().configValue(MessageFilter.class)));
    }

    public static Dsui domsole() {
        return environment().configValue(Domsole.class);
    }
}
