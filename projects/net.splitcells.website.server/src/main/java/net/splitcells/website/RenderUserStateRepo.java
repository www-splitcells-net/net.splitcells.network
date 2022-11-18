package net.splitcells.website;

import net.splitcells.dem.environment.config.framework.OptionI;

public class RenderUserStateRepo extends OptionI<Boolean> {
    public RenderUserStateRepo() {
        super(() -> false);
    }
}
