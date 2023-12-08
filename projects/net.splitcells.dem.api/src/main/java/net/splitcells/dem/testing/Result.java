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

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * Stores the results of a calculation.
 * There may not be any value present,
 * if, for instance, the calculation was not successful.
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

    public Optional<Value> value() {
        return value;
    }

    public Result<Value, Message> withErrorMessage(Message arg) {
        errorMessages.add(arg);
        return this;
    }

    public Result<Value, Message> withValue(Value arg) {
        value = Optional.of(arg);
        return this;
    }
}
