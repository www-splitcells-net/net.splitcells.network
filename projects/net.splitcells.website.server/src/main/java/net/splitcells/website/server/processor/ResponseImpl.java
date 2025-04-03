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
package net.splitcells.website.server.processor;

public class ResponseImpl<T> implements Response<T>{
    public static <T> ResponseImpl<T> responseImpl(T data) {
        return new ResponseImpl<>(data);
    }

    public static <T> ResponseImpl<T> responseImpl() {
        return new ResponseImpl<>();
    }

    private final T data;

    private ResponseImpl(T dataArg) {
        data = dataArg;
    }

    private ResponseImpl() {
        data = null;
    }

    public T data() {
        return data;
    }

    @Override
    public boolean hasData() {
        return data != null;
    }
}
