/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
