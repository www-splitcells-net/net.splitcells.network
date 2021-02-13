package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.Dem;
import net.splitcells.dem.resource.host.interaction.Domsole;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.resource.host.interaction.MessageFilter;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;

public class Den {

    public static Perspective val(String name) {
        return perspective("val").withProperty("name", name);
    }

    public static Perspective project(Perspective... arg) {
        return perspective("project");
    }

    public static Perspective todo(Perspective... arg) {
        return perspective("todo");
    }

    public static Perspective todo(String text, Perspective... arg) {
        return perspective("todo");
    }

    public static Perspective priority(Perspective... arg) {
        return perspective("priority");
    }

    public static Perspective queue(Perspective... arg) {
        return perspective("queue");
    }

    public static Perspective scheduling(Perspective... arg) {
        return perspective("scheduling");
    }

    public static Perspective solution(Perspective... arg) {
        return perspective("solution");
    }
}
