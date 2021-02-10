package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.Dem;
import net.splitcells.dem.resource.host.interaction.Domsole;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.resource.host.interaction.MessageFilter;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;

public class DocumentTest {
    public static void main(String... args) {
        Dem.process(() -> {
            /**
             * Works good, considering the fact, that no adjustments were made.
             */
            perspective("project").withProperty("name", "Dependency Manager").withValues(
                    perspective("solution").withValues(
                            perspective("scheduling").withValues(
                                    perspective("queues").withValues(
                                            perspective("priority").withValues(
                                                    perspective("todo").withChild(perspective("Clean up ConfigurationI."))
                                                    , perspective("todo").withChild(perspective("Support deterministic mode."))))))
                    , perspective("logging")
                    , perspective("side-effects"));
            /**
             * Works great considering the fact, that no real grammar specific adjustments were made.
             * Compared to xml, only the with suffixes are unnecessary text.
             */
            val("project").withProperty("name", "Dependency Manager").withValues(
                    val("solution").withValues(
                            val("scheduling").withValues(
                                    val("queues").withValues(
                                            val("priority").withValues(
                                                    val("todo").withText("Clean up ConfigurationI.")
                                                    , val("todo").withText("Support deterministic mode.")))))
                    , perspective("logging")
                    , perspective("side-effects"));
            /**
             * Loose grammars without restrictions can be created very fast.
             * Keep in mind, that with such a technology automatic refactoring is easier to implement,
             * because the IDE can take care of it.
             */
            Domsole.domsole().append(
                    project().withProperty("name", "Dependency Manager").withValues(
                            solution().withValues(
                                    scheduling().withValues(
                                            queue().withValues(
                                                    priority().withValues(
                                                            todo().withText("Clean up ConfigurationI.")
                                                            , todo().withText("Support deterministic mode.")
                                                            , todo().withText("This is a long text with bla bla bla."
                                                                    + "A little bit more bla bla."
                                                                    + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id tortor ante."
                                                                    + "Donec gravida a lectus varius cursus. Vestibulum ultricies vestibulum enim vehicula semper"
                                                                    + "Vestibulum scelerisque egestas viverra. Ut pharetra neque at tellus tincidunt rhoncus."
                                                                    + "Duis erat purus, gravida et nisl ac, blandit pharetra leo. Praesent semper sem tellus, sit amet interdum felis consequat a."
                                                                    + "Suspendisse cursus, augue ac dictum egestas, ligula massa bibendum ex, eget consectetur purus mi eu leo."
                                                                    + "Nullam felis orci, aliquam vel venenatis ultrices, tristique at dui.")))
                                            , queue()))
                            , perspective("logging")
                            , perspective("side-effects"))
                            .toDom()
                    , () -> list()
                    , LogLevel.INFO);
            /**
             * By defining constructors, we can create a loose grammars.
             * Now we just need to require, that the return value of such functions are immutable
             * and we effectively have a grammar, that has a comparable quality compared to XML.
             */
            Domsole.domsole().append(
                    project(
                            solution(
                                    scheduling(
                                            queue(
                                                    priority(
                                                            todo("Clean up ConfigurationI.")
                                                            , todo("Support deterministic mode.")
                                                            , todo("This is a long text with bla bla bla."
                                                                    + " A little bit more bla bla."
                                                                    + " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id tortor ante."
                                                                    + " Donec gravida a lectus varius cursus. Vestibulum ultricies vestibulum enim vehicula semper"
                                                                    + " Vestibulum scelerisque egestas viverra. Ut pharetra neque at tellus tincidunt rhoncus."
                                                                    + " Duis erat purus, gravida et nisl ac, blandit pharetra leo. Praesent semper sem tellus, sit amet interdum felis consequat a."
                                                                    + " Suspendisse cursus, augue ac dictum egestas, ligula massa bibendum ex, eget consectetur purus mi eu leo."
                                                                    + " Nullam felis orci, aliquam vel venenatis ultrices, tristique at dui.")))
                                            , queue()))
                            , val("logging")
                            , val("side-effects"))
                            .withProperty("name", "Dependency Manager")
                            .toDom()
                    , () -> list()
                    , LogLevel.INFO);
        }, (env) -> {
            env.config()
                    .withConfigValue(MessageFilter.class, (message) -> true);
        });
    }

    private static Perspective val(String name) {
        return perspective("val").withProperty("name", name);
    }

    private static Perspective project(Perspective... arg) {
        return perspective("project");
    }

    private static Perspective todo(Perspective... arg) {
        return perspective("todo");
    }

    private static Perspective todo(String text, Perspective... arg) {
        return perspective("todo");
    }

    private static Perspective priority(Perspective... arg) {
        return perspective("priority");
    }

    private static Perspective queue(Perspective... arg) {
        return perspective("queue");
    }

    private static Perspective scheduling(Perspective... arg) {
        return perspective("scheduling");
    }

    private static Perspective solution(Perspective... arg) {
        return perspective("solution");
    }
}
