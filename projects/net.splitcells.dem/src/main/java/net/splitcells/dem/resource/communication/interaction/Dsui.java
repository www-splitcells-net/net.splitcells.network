/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.environment.config.EndTime;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.data.set.SetWA;
import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.resource.host.interaction.Domsole;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.resource.host.interaction.LogMessage;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.lang.namespace.NameSpaces.DEN;
import static net.splitcells.dem.lang.namespace.NameSpaces.NATURAL;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.resource.host.interaction.LogMessageI.logMessage;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * DSUI ^= Dom Stream and Stack based User Interface
 * <p>
 * IDEA Support recursive stacking.
 * <p>
 * TOFIX Remove duplicate name space declaration.
 * <p>
 * TODO Split log file into multiple, in order to avoid files that are too large for easy processing.
 */
public class Dsui implements Sui<LogMessage<Node>>, Flushable {
    private static final String ENTRY_POINT = "ENTRY.POINT.237048021";

    public static Dsui dsui(Sender<String> output, Predicate<LogMessage<Node>> messageFilter) {
        Element execution = elementWithChildren(
                elementWithChildren(rElement(DEN, "execution"), nameSpaceDecleration(NATURAL)),
                elementWithChildren(DEN, "name", environment().config().configValue(ProgramName.class)),
                elementWithChildren(NATURAL, "start-time", environment().config().configValue(StartTime.class).toString()),
                textNode(ENTRY_POINT));
        return dsui(output, execution, messageFilter);
    }

    private static Dsui dsui(Sender<String> output, Element root, Predicate<LogMessage<Node>> messageFilter) {
        return new Dsui(output, root, messageFilter);
    }

    private final Sender<String> baseOutput;
    private final Sender<String> contentOutput;
    private final Element root;
    private final Predicate<LogMessage<Node>> messageFilter;
    private boolean isClosed = false;

    public Dsui(Sender<String> output, Element root, Predicate<LogMessage<Node>> messageFilter) {
        this.messageFilter = messageFilter;
        baseOutput = requireNonNull(output);
        this.root = requireNonNull(root);
        {
            // HACK
            String tmp = Xml.toDocumentString(root);
            if (!tmp.contains(Dsui.ENTRY_POINT)) {
                throw new IllegalArgumentException(tmp);
            }
            // FIXME Remove last line if only whitespace.
            baseOutput.append(tmp.split(Dsui.ENTRY_POINT)[0]);
        }
        contentOutput = Sender.extend(baseOutput, "   ", "");
    }

    public <R extends ListWA<LogMessage<Node>>> R appendError(Throwable throwable) {
        // TOFIX Additional namespace declaration should not be needed.
        final var error = Xml.rElement(DEN, "error");
        {
            final var errorMessage = Xml.elementWithChildren(DEN, "message");
            errorMessage.appendChild(textNode(throwable.getMessage()));
            error.appendChild(errorMessage);
        }
        {
            final var stackTrace = Xml.elementWithChildren(DEN, "stack-trace");
            final var stackTraceValue = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stackTraceValue));
            stackTrace.appendChild(textNode(stackTraceValue.toString()));
            error.appendChild(stackTrace);
        }
        return Domsole.domsole().append(error, Optional.empty(), LogLevel.CRITICAL);
    }

    public <R extends ListWA<LogMessage<Node>>> R append(Node domable, LogLevel logLevel) {
        return append(logMessage(domable, NO_CONTEXT, logLevel));
    }

    public <R extends ListWA<LogMessage<Node>>> R append(Domable domable, LogLevel logLevel) {
        return append(logMessage(domable.toDom(), NO_CONTEXT, logLevel));
    }

    /**
     * There is no real usage for the optionality of context.
     *
     * @param domable  domable
     * @param context  context
     * @param logLevel logLevel
     * @param <R>      type
     * @return return
     */
    @Deprecated
    public <R extends ListWA<LogMessage<Node>>> R append(Node domable, Optional<Discoverable> context,
                                                         LogLevel logLevel) {
        return append(logMessage(domable, context.orElse(NO_CONTEXT), logLevel));
    }

    public <R extends ListWA<LogMessage<Node>>> R append(Node domable, Discoverable context, LogLevel logLevel) {
        return append(logMessage(domable, context, logLevel));
    }

    public <R extends ListWA<LogMessage<Node>>> R append(Domable domable, Discoverable context, LogLevel logLevel) {
        return append(domable.toDom(), context, logLevel);
    }

    public <R extends ListWA<LogMessage<Node>>> R append(Domable domable, Optional<Discoverable> context,
                                                         LogLevel logLevel) {
        return append(domable.toDom(), context, logLevel);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Deprecated
    public <R extends ListWA<LogMessage<Node>>> R append(LogMessage<Node> arg) {
        if (messageFilter.test(arg)) {
            print(arg.content());
        }
        return (R) this;
    }

    private void print(Node arg) {
        asList(
                Xml.toPrettyWithoutHeaderString(arg)
                        .split("\\R"))
                .forEach(contentOutput::append);
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public <R extends ListWA<LogMessage<Node>>> R append(String text) {
        // HACK
        contentOutput.append(text);
        return (R) this;
    }

    @Override
    public void close() {
        if (isClosed) {
            throw new IllegalStateException();
        }
        final var endTime = environment().config().configValue(EndTime.class);
        if (endTime.isPresent()) {
            print(Xml.elementWithChildren(
                    Xml.rElement(NATURAL, "end-time"),
                    Xml.textNode(endTime.get().toString())));
            print(Xml.elementWithChildren(
                    Xml.rElement(NATURAL, "runtime-in-seconds"),
                    Xml.textNode("" + Duration.between
                            (environment().config().configValue(StartTime.class)
                                    , endTime.get())
                            .toSeconds())));
            print(Xml.elementWithChildren(
                    Xml.rElement(NATURAL, "runtime-in-nanoseconds"),
                    Xml.textNode("" + Duration.between
                            (environment().config().configValue(StartTime.class)
                                    , endTime.get())
                            .toNanos())));
        }
        String endingMessage = Xml.toPrettyString(root);
        if (!endingMessage.contains(Dsui.ENTRY_POINT)) {
            throw new IllegalArgumentException(endingMessage);
        }
        baseOutput.append(endingMessage.split(Dsui.ENTRY_POINT)[1]);

        /**
         * TODO FIX This does sometimes not work. See {@link Dem}.
         */
        contentOutput.flush();
        contentOutput.close();

        isClosed = true;
    }

    @Override
    public void flush() {
        contentOutput.flush();
    }

    @Override
    public <R extends SetWA<LogMessage<Node>>> R add(LogMessage<Node> value) {
        throw notImplementedYet();
    }

}
