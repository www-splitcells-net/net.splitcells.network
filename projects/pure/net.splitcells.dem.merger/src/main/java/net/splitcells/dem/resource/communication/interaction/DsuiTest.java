/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.resource.communication.Sender;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static net.splitcells.dem.resource.communication.interaction.Dsui.dsui;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class DsuiTest {
    @Test
    public void testSenderOnlyClosedOnce() {
        final var verifier = SenderStub.<String>create();
        dsui(verifier, message -> true).close();
        assertThat(verifier.closed()).isTrue();
    }

    @Test
    public void testEndMessage() {
        final var verifier = SenderStub.<String>create();
        dsui(verifier, message -> true).close();
        assertThat(verifier.storage().lastValue()).contains("</d:execution>");
        assertThat(verifier.flushCount()).isEqualTo(1);
    }
}
