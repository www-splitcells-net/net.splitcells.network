/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.processor;

import static net.splitcells.website.server.processor.ResponseImpl.responseImpl;

public interface Response<T> {
    static <T> Response<T> response(T data) {
        return responseImpl(data);
    }

    static <T> Response<T> emptyResponse() {
        return responseImpl();
    }

    /**
     * @return TODO This should return an optional value, as generic error handling is not possible otherwise.
     */
    T data();

    boolean hasData();
}
