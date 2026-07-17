/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
 * <p>This is the preferred way to model explicit error handling.
 * Stores the results of a calculation.
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
     * This method ensures, that the required {@link #optionalValue()} is present and errors are absent.
     * This helps one to fail fast and early with a description.
     * This avoids situations, where later on the {@link Result} is thrown away and
     * the {@link #value} is found to be invalid.
     *
     * @return Returns {@link #optionalValue()} or throws existing {@link #errorMessages()}.
     */
    public Value requiredValue() {
        if (value.isEmpty()) {
            throw execException(tree("The required value is not present.")
                    .withProperty("Error Messages", errorMessages().toString()));
        }
        if (errorMessages.hasElements()) {
            throw execException(tree("A value and errors are present.")
                    .withProperty("Error Messages", errorMessages().toString())
                    .withProperty("Value", value.get().toString()));
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
