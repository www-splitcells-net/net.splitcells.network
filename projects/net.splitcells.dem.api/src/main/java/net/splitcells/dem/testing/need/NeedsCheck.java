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
package net.splitcells.dem.testing.need;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.CommonMarkUtils;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.testing.Result;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.dem.utils.TreesException;

import java.util.Optional;
import java.util.function.Supplier;

import static net.splitcells.dem.lang.CommonMarkUtils.joinDocuments;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.need.NeedException.needErrorException;
import static net.splitcells.dem.testing.need.NeedException.needException;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.throwableToString;

/**
 * <h2>Needs Check API</h2>
 * <p>This API defines a single point of implicit error handling,
 * that provides user readable CommonMark error reports.
 * Implicit means, that code causing the error, does not have to propagate the error by itself.
 * It will be done by the environment.</p>
 * <p>Where possible, error checking should be done implicitly.
 * For example, when you insert an element into a {@link Set},
 * use {@link Set#add(Object)} instead of combining {@link Set#has(Object)} and {@link Set#ensureContains(Object)}.
 * In other words, this API is not suitable as a replacement for restrictive methods,
 * as the additional {@link Need} argument makes these harder to use,
 * which is the diametrical to the goals of restrictive methods.</p>
 * <p>This assertion framework provides an API to assert constraints in code,
 * while minimizing the impact of the assertion code to the legibility of the main code.
 * It also abstracts away, technical details like {@link Exception} and allows to use different technologies in the future.
 * This is done by splitting the code vertically, where the left side contains the code's main logic and
 * the right side contains the assertion:</p>
 * <pre><code>
 *     createCardDeck()
 *      .stream(isNotEmpty())
 *      .filter(card -> card.hasNumber(), needCardDescriptionIsNotEmpty())
 *      .reduce(0, (a,b) -> a.getNumber() + b.getNumber(), needNumberIsBiggerThanZero())
 *      .orElseThrow(needSumIsNotBiggerThan(364));
 * </code></pre>
 * <p>For a function to support the {@link Need} API,
 * one has to provide 1 or more parameters of {@link Need} instances,
 * that are checked via {@link #checkNeed(Need, Object)},
 * where the subject is this.</p>
 * <p>This can also be used to create user facing errors.
 * Keep in mind, that user facing error providers have to ensure,
 * the users only see info, that they are allowed to see.
 * This can be ensured, by only returning or throwing error info in security relevant objects,
 * for which the users have the rights to see them.
 * So, if there is an authentication error, no error info of any user is allowed to be returned or thrown.
 * In other words, only security sensitive code,
 * has to closely monitor, what is allowed to be returned or thrown as a message.</p>
 * {@link Need} builders like `needCardDescriptionIsNotEmpty` should always start with the word `need`,
 * just like classic `assert*` methods of Java libraries.
 * This makes it easier to mentally filter out such code,
 * when looking at the functionality of code while ignoring its error handling.
 * <h2>Concept</h2>
 * <p>Define a single point of implicit error handling.</p>
 * <p>It was considered to create a unified handler for any kind of exception, security, memory limit etc..
 * This way one could just define a single point of start and error handling,
 * without having to know what kind of handlers exist.
 * Therefore, error handling and error frameworks could be injected into the centralized error handler.
 * It is considered, that this is not possible for now,
 * as there are 2 types of such handlers:</p>
 * <p>Implicit ones, that work things like {@link Exception} and {@link Dem#config()}.
 * There it is easy to inject this type of handling in any code present without changing it.
 * The setup of such handlers is done implicitly as well without having to know what kind of handlers exist.
 * This can be easily used for user error handling,
 * where for example exceptions are used to abort and report the execution results.
 * Only a single point of error handling is required with no arguments or any kind of no special setup.
 * </p>
 * <p>Explicit ones, need explicit arguments, that are provided by the caller.
 * It is relatively hard to define a single point of any kind of error handling,
 * without knowing what kind of error handling exists.
 * Therefore, injecting explicit error handling is considered hard.
 * See handling of errors for one specific user.</p>
 * <h2>Implementation Notes</h2>
 * <p>Injection of error handlers etc. is not implemented, as there is currently no need for that.</p>
 * <h2>Tasks</h2>
 * <p>TODO One has to question if something like that, is really better than try-catch?
 * Consider this, when this API is used a lot more and thereby experience is acquired.
 * If this is removed in the future, you need to document the reasoning, so that the error is not done again.
 * One advantage of this API, is the fact,
 * that the code gets a bit more technology agnostic and thereby makes throw a tad bit more replaceable.</p>
 */
public class NeedsCheck {
    private NeedsCheck() {
        throw constructorIllegal();
    }

    public static <T> void checkNeed(Need<T> need, T subject) {
        final var defiance = need.checkCompliance(subject);
        if (defiance.isEmpty()) {
            return;
        }
        throw needException(defiance);
    }

    /**
     * Using this method instead of throwing the {@link Exception} directly,
     * makes it possible to replace the underlying error report technology.
     * For instance, one could use a static variable or a logging framework instead.
     *
     * @param messages
     */
    public static void reportErrorNeed(Tree... messages) {
        throw needErrorException(messages);
    }

    public static <T> Result<T, String> runWithCheckedNeeds(Supplier<T> supplier) {
        try {
            return Result.<T, String>result().withValue(supplier.get());
        } catch (NeedException e) {
            return Result.<T, String>result().withErrorMessage(toCommonMark(e));
        } catch (TreesException e) {
            return Result.<T, String>result().withErrorMessage(toCommonMark(e));
        } catch (ExecutionException e) {
            return Result.<T, String>result().withErrorMessage(toCommonMark(e));
        } catch (Throwable t) {
            return Result.<T, String>result().withErrorMessage(toCommonMark(t));
        }
    }

    private static String toCommonMark(NeedException arg) {
        final var errorMessage = StringUtils.stringBuilder();
        errorMessage.append("# Error Summary");
        joinDocuments(errorMessage
                , arg.getMessages().stream()
                        .map(l -> l.content().toCommonMarkString())
                        .reduce("", (a, b) -> joinDocuments(a, b))
                        + "\n");
        joinDocuments(errorMessage, "# Stack Trace");
        joinDocuments(errorMessage, tree(throwableToString(arg)).toCommonMarkString());
        return errorMessage.toString();
    }

    private static String toCommonMark(ExecutionException arg) {
        return toCommonMark(arg, false);
    }

    private static String toCommonMark(ExecutionException arg, boolean isCause) {
        final var errorMessage = StringUtils.stringBuilder();
        if (isCause) {
            errorMessage.append("# Causing Error Message");
        } else {
            errorMessage.append("# Error Message");
        }
        joinDocuments(errorMessage, arg.getMessage());
        if (isCause) {
            joinDocuments(errorMessage, "# Causing Stack Trace");
        } else {
            joinDocuments(errorMessage, "# Stack Trace");
        }
        joinDocuments(errorMessage, tree(throwableToString(arg)).toCommonMarkString());
        if (arg.getCause() != null) {
            joinDocuments(errorMessage, toCommonMark(arg.getCause(), true));
        }
        return errorMessage.toString();
    }

    private static String toCommonMark(Throwable arg) {
        return toCommonMark(arg, false);
    }

    private static String toCommonMark(Throwable arg, boolean isCause) {
        final var errorMessage = StringUtils.stringBuilder();
        if (isCause) {
            errorMessage.append("# Causing Error Message");
        } else {
            errorMessage.append("# Internal Error Message");
        }
        joinDocuments(errorMessage, arg.getMessage());
        joinDocuments(errorMessage, "# Causing Stack Trace");
        joinDocuments(errorMessage, tree(throwableToString(arg)).toCommonMarkString());
        if (arg.getCause() != null) {
            joinDocuments(errorMessage, toCommonMark(arg.getCause(), true));
        }
        return errorMessage.toString();
    }

    private static String toCommonMark(TreesException arg) {
        return toCommonMark(arg, false);
    }

    private static String toCommonMark(TreesException arg, boolean isCause) {
        final var errorMessage = StringUtils.stringBuilder();
        if (isCause) {
            errorMessage.append("# Causing Error Message\n");
        } else {
            errorMessage.append("# Error Message\n");
        }
        errorMessage.append(arg.getTrees().stream()
                .map(Tree::toCommonMarkString)
                .reduce(CommonMarkUtils::joinDocuments)
                .orElse(""));
        joinDocuments(errorMessage, "# Causing Stack Trace");
        joinDocuments(errorMessage, tree(throwableToString(arg)).toCommonMarkString());
        if (arg.getCause() != null) {
            joinDocuments(errorMessage, toCommonMark(arg.getCause(), true));
        }
        return errorMessage.toString();
    }
}
