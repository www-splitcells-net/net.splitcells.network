package net.splitcells.dem.lang.perspective;

import static net.splitcells.dem.lang.namespace.NameSpaces.string;
import static net.splitcells.dem.lang.perspective.Den.den;

public class Dens {
    public static Den project(Perspective... args) {
        final var project = den("project");
        project.withValues(args);
        return project;
    }

    public static Den name(String value) {
        final var name = den("name");
        name.withChild(string(value));
        return name;
    }

    public static Den val(Perspective... args) {
        final var name = den("val");
        name.withValues(args);
        return name;
    }

    public static Den ref(Perspective... args) {
        final var name = den("ref");
        name.withValues(args);
        return name;
    }

    public static Den optimization(Perspective... args) {
        final var name = den("optimization");
        name.withValues(args);
        return name;
    }

    public static Den scheduling(Perspective... args) {
        final var name = den("scheduling");
        name.withValues(args);
        return name;
    }

    public static Den queue(Perspective... args) {
        final var name = den("queue");
        name.withValues(args);
        return name;
    }

    public static Den prioritization(Perspective... args) {
        final var name = den("prioritization");
        name.withValues(args);
        return name;
    }

    public static Den acc(Perspective... args) {
        final var name = den("acc");
        name.withValues(args);
        return name;
    }

    public static Den arg(Perspective... args) {
        final var name = den("arg");
        name.withValues(args);
        return name;
    }

    public static Den var(Perspective... args) {
        final var name = den("var");
        name.withValues(args);
        return name;
    }

    public static Den core(Perspective... args) {
        final var name = den("core");
        name.withValues(args);
        return name;
    }

    public static Den environment(Perspective... args) {
        final var name = den("environment");
        name.withValues(args);
        return name;
    }

    public static Den merge(Perspective... args) {
        final var name = den("merge");
        name.withValues(args);
        return name;
    }

    public static Den mixed(Perspective... args) {
        final var name = den("mixed");
        name.withValues(args);
        return name;
    }

    public static Den names(Perspective... args) {
        final var name = den("names");
        name.withValues(args);
        return name;
    }

    public static Den nickName(Perspective... args) {
        final var name = den("nick-name");
        name.withValues(args);
        return name;
    }

    public static Den objective(Perspective... args) {
        final var name = den("objective");
        name.withValues(args);
        return name;
    }

    public static Den sheath(Perspective... args) {
        final var name = den("sheath");
        name.withValues(args);
        return name;
    }

    public static Den solution(Perspective... args) {
        final var name = den("solution");
        name.withValues(args);
        return name;
    }

    public static Den unscheduled(Perspective... args) {
        final var name = den("unscheduled");
        name.withValues(args);
        return name;
    }

    public static Den abbreviation(Perspective... args) {
        final var name = den("abbreviation");
        name.withValues(args);
        return name;
    }

    public static Den todo(String value) {
        final var name = den("todo");
        name.withChild(string(value));
        return name;
    }

    private Dens() {
    }
}
