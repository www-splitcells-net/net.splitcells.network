/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.runners;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.splitcells.gel.editor.Editor;

import java.util.Optional;

@Accessors(chain = true)
public class FunctionCallRun {

    /**
     *
     * @param argSubject
     * @param argContext TODO Make context a required argument.
     * @return
     */
    @Deprecated
    public static FunctionCallRun functionCallRun(Optional<Object> argSubject, Optional<Editor> argContext) {
        return new FunctionCallRun(argSubject, argContext);
    }
    
    public static FunctionCallRun functionCallRun(Optional<Object> argSubject, Editor argContext) {
        return new FunctionCallRun(argSubject, Optional.of(argContext));
    }

    @Getter private final Optional<Object> subject;

    @Getter private final Optional<Editor> context;

    @Setter @Getter private Optional<Object> result = Optional.empty();

    public FunctionCallRun setResultVal(Object argResult) {
        result = Optional.of(argResult);
        return this;
    }

    private FunctionCallRun(Optional<Object> argSubject, Optional<Editor> argContext) {
        subject = argSubject;
        context = argContext;
    }
}
