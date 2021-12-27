package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.PerspectiveI;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import org.w3c.dom.Node;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.resource.communication.interaction.LogMessageI.logMessage;
import static net.splitcells.dem.utils.NotImplementedYet.TODO_NOT_IMPLEMENTED_YET;

/**
 * TODO Render stack traces as one line optionally.
 * Maybe this should be somehow be optionally supported for
 * messages consisting of multiple lines.
 * In other words, this may be should be implemented as a general
 * functionality in order to provide one message one line logs.
 */
public interface Ui extends Sui<LogMessage<Perspective>>, Flushable, Closeable {

    default Ui append(String name) {
        return append(logMessage(perspective(name), NO_CONTEXT, LogLevel.DEBUG));
    }

    default Ui append(String message, LogLevel logLevel) {
        return append(logMessage(perspective(message), NO_CONTEXT, logLevel));
    }

    default Ui append(Domable domable, LogLevel logLevel) {
        return append(logMessage(domable.toPerspective(), NO_CONTEXT, logLevel));
    }

    default Ui append(Perspective perspective, LogLevel logLevel) {
        return append(logMessage(perspective, NO_CONTEXT, logLevel));
    }

    @Deprecated
    default Ui append(Node content, Discoverable context, LogLevel logLevel) {
        return append(logMessage(perspective(TODO_NOT_IMPLEMENTED_YET), context, logLevel));
    }

    default Ui append(Domable content, Discoverable context, LogLevel logLevel) {
        return append(logMessage(content.toPerspective(), context, logLevel));
    }

    default Ui append(Domable content, Optional<Discoverable> context, LogLevel logLevel) {
        return append(logMessage(content.toPerspective(), context.orElse(NO_CONTEXT), logLevel));
    }

    default Ui append(Perspective content, Discoverable context, LogLevel logLevel) {
        return append(logMessage(content, context, logLevel));
    }

    default Ui appendError(Throwable throwable) {
        final var error = perspective("error");
        error.withProperty("message", throwable.getMessage());
        {
            final var stackTraceValue = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stackTraceValue));
            error.withProperty("stack-trace", stackTraceValue.toString());
        }
        return append(logMessage(error, NO_CONTEXT, LogLevel.CRITICAL));
    }

    /**
     * Warnings are errors, where an action is not required.
     * <p>
     * This is typically used in order to find out,
     * if a certain usage of the code executes code,
     * which is not fully implemented and where therefore the functionality is not complete.
     * {@link Throwable}s are used, so that it is obvious,
     * where the related code is and to identify the caller.
     *
     * @param throwable
     * @return
     */
    default Ui appendWarning(Throwable throwable) {
        final var warning = perspective("warning");
        warning.withProperty("message", throwable.getMessage());
        {
            final var stackTraceValue = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stackTraceValue));
            warning.withProperty("stack-trace", stackTraceValue.toString());
        }
        return append(logMessage(warning, NO_CONTEXT, LogLevel.CRITICAL));
    }
}