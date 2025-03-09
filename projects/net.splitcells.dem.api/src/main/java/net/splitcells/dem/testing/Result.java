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
package net.splitcells.dem.testing;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.ExecutionException;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * <p>Stores the results of a calculation.
 * There may not be any value present,
 * if, for instance, the calculation was not successful.
 * This does only make sense, if multiple errors are somehow to be combined.</p>
 *
 * @param <Value>
 * @param <Message>
 */
public class Result<Value, Message> {
    public static <V, M> Result<V, M> result() {
        return new Result<>();
    }

    private List<Message> errorMessages = list();
    private Optional<Value> value = Optional.empty();

    private Result() {

    }

    public List<Message> errorMessages() {
        return errorMessages;
    }

    /**
     * This method's name is chosen to be longer, so that its usage is discouraged.
     * Use this method only, when an {@link Optional} is required.
     * Otherwise, use {@link #requiredValue()} instead,
     * so that you get better error messages, when an assumption is not fulfilled.
     *
     * @return
     */
    public Optional<Value> optionalValue() {
        return value;
    }

    /**
     * This method ensures, that the required {@link #optionalValue()} is present or
     * that the reason for its absence is known.
     *
     * @return Returns {@link #optionalValue()} or throws existing {@link #errorMessages()}.
     */
    public Value requiredValue() {
        if (value.isEmpty()) {
            throw execException(tree("The required value is not present.")
                    .withProperty("Error Messages", errorMessages().toString()));
        }
        return value.orElseThrow();
    }

    public Result<Value, Message> withErrorMessage(Message arg) {
        errorMessages.add(arg);
        return this;
    }

    public Result<Value, Message> withErrorMessages(Result<?, Message> otherResult) {
        errorMessages.withAppended(otherResult.errorMessages());
        return this;
    }

    public Result<Value, Message> withResult(Result<Value, Message> otherResult) {
        errorMessages.withAppended(otherResult.errorMessages());
        value = otherResult.optionalValue();
        return this;
    }

    public Result<Value, Message> withValue(Value arg) {
        value = Optional.of(arg);
        return this;
    }

    public boolean defective() {
        return value.isEmpty() || errorMessages.hasElements();
    }

    public boolean working() {
        return !defective();
    }

    public Result<Value, Message> requireWorking() {
        if (defective()) {
            throw ExecutionException.execException(tree("Result is defective")
                    .withProperty("error messages", errorMessages.toString()));
        }
        return this;
    }
}
