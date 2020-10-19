package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.namespace.NameSpaces;
import org.w3c.dom.Node;

import static net.splitcells.dem.lang.namespace.NameSpaces.string;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;

/**
 * This class defines the Den namespace.
 * As long as this code is not really used, it stays deprecated.
 * This class is not up to date. See {@link PerspectiveDocument};
 */
@Deprecated
public class Den implements Domable {

    public static Den project(Perspective... args) {
        final var project = den("project");
        project.getPerspective().withValues(args);
        return project;
    }

    public static Den name(String value) {
        final var name = den("name");
        name.getPerspective().withChild(string(value));
        return name;
    }

    public static Den val(Perspective... args) {
        final var name = den("val");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den ref(Perspective... args) {
        final var name = den("ref");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den optimization(Perspective... args) {
        final var name = den("optimization");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den scheduling(Perspective... args) {
        final var name = den("scheduling");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den queue(Perspective... args) {
        final var name = den("queue");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den prioritization(Perspective... args) {
        final var name = den("prioritization");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den acc(Perspective... args) {
        final var name = den("acc");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den arg(Perspective... args) {
        final var name = den("arg");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den var(Perspective... args) {
        final var name = den("var");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den core(Perspective... args) {
        final var name = den("core");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den environment(Perspective... args) {
        final var name = den("environment");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den merge(Perspective... args) {
        final var name = den("merge");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den mixed(Perspective... args) {
        final var name = den("mixed");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den names(Perspective... args) {
        final var name = den("names");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den nickName(Perspective... args) {
        final var name = den("nick-name");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den objective(Perspective... args) {
        final var name = den("objective");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den sheath(Perspective... args) {
        final var name = den("sheath");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den solution(Perspective... args) {
        final var name = den("solution");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den unscheduled(Perspective... args) {
        final var name = den("unscheduled");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den abbreviation(Perspective... args) {
        final var name = den("abbreviation");
        name.getPerspective().withValues(args);
        return name;
    }

    public static Den todo(String value) {
        final var name = den("todo");
        name.getPerspective().withChild(string(value));
        return name;
    }

    private static Den den(String value) {
        return new Den(value);
    }

    private final Perspective perspective;

    private Den(String value) {
        perspective = perspective(value, NameSpaces.DEN);
    }

    private Perspective getPerspective() {
        return perspective;
    }

    @Override
    public Node toDom() {
        return perspective.toDom();
    }
}
