/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.Dem;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.communication.log.Logs;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.lang.perspective.Den.priority;
import static net.splitcells.dem.lang.perspective.Den.project;
import static net.splitcells.dem.lang.perspective.Den.queue;
import static net.splitcells.dem.lang.perspective.Den.scheduling;
import static net.splitcells.dem.lang.perspective.Den.solution;
import static net.splitcells.dem.lang.perspective.Den.todo;
import static net.splitcells.dem.lang.perspective.Den.val;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;

/**
 * This is only a demonstration, that XML like documents, can be stored as Java code.
 */
@Deprecated
public class DocumentTest {

    @UnitTest
    public void test() {
        Dem.process(() -> {
        }, (env) -> {
            env.config()
                    .withConfigValue(MessageFilter.class, (message) -> true);
        });
    }

    private static void experiment() {
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
         *
         * We cannot do real typing without much boilerplate without using inheritance.
         */
        Logs.logs().append(
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
                , LogLevel.INFO);
        /**
         * By defining constructors, we can create a loose grammars.
         * Now we just need to require, that the return value of such functions are immutable
         * and we effectively have a grammar, that has a comparable quality compared to XML.
         */
        Logs.logs().append(
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
                , LogLevel.INFO);
    }
}
