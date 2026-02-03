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
package net.splitcells.dem.utils;

import lombok.Getter;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.CommonMarkUtils;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.tree.Tree;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.CommonMarkUtils.joinDocuments;

@JavaLegacy
public class ExecutionException extends RuntimeException {

    public static ExecutionException execException(Tree... argTrees) {
        return execException(listWithValuesOf(argTrees));
    }

    public static ExecutionException unsupportedOperation() {
        return execException("Unsupported operation");
    }

    /**
     * {@link #execException(String)} is named different from {@link ExecutionException},
     * as the autocompletion for the static method `executionException` is very bad.
     *
     * @param message This message describes reason for the exception.
     * @return This is an exception, that can be thrown in order to abort the execution.
     */
    public static ExecutionException execException(String message) {
        return new ExecutionException(message);
    }

    public static ExecutionException execException(Tree message) {
        return new ExecutionException(listWithValuesOf(message));
    }

    public static ExecutionException execException(List<Tree> messages) {
        return new ExecutionException(messages);
    }

    public static ExecutionException execException() {
        return new ExecutionException();
    }

    public static ExecutionException execException(Tree message, Throwable t) {
        return new ExecutionException(listWithValuesOf(message), t);
    }

    public static ExecutionException execException(String message, Throwable t) {
        return new ExecutionException(message, t);
    }

    public static ExecutionException execException(Throwable t) {
        return new ExecutionException(t);
    }

    @Getter private final List<Tree> messages;

    private ExecutionException(List<Tree> argTrees, Throwable t) {
        super(t);
        messages = argTrees;
    }

    private ExecutionException(List<Tree> argTrees) {
        messages = argTrees;
    }

    private ExecutionException(String message, Throwable t) {
        super(message, t);
        messages = list();
    }

    private ExecutionException() {
        super();
        messages = list();
    }

    private ExecutionException(String message) {
        super(message);
        messages = list();
    }

    private ExecutionException(Throwable t) {
        super(t);
        messages = list();
    }

    @Override
    public String getMessage() {
        if (messages.isEmpty()) {
            return super.getMessage();
        }
        return messages.stream()
                .map(Tree::toCommonMarkString)
                .reduce(CommonMarkUtils::joinDocuments)
                .orElse("");
    }
}
