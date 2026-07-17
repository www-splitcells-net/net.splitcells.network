/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.splitcells.dem.resource.Trail;

import static net.splitcells.dem.resource.Trail.trail;

@Accessors(chain = true)
public class TrailLink {
    public static TrailLink trailLink(String name, String path) {
        return trailLink(name, trail(path));
    }

    public static TrailLink trailLink(String name, Trail path) {
        return new TrailLink(name, path);
    }

    @Getter @Setter private String name = "";
    @Getter @Setter private Trail path;

    private TrailLink(String argName, Trail argPath) {
        name = argName;
        path = argPath;
    }
    
    @Override
    public String toString() {
        return path.unixPathString() + " named as " + name;
    }
}
