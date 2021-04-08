package net.splitcells.dem.data.set;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.dem.resource.communication.interaction.Dsui;

import java.util.function.Supplier;

import static net.splitcells.dem.data.set.SetFI_configured.setFiConfigured;

public class SetsR extends ResourceI<SetF> {
    public SetsR() {
        super(() -> setFiConfigured());
    }
}
