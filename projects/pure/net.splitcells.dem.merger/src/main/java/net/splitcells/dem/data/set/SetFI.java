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
package net.splitcells.dem.data.set;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public final class SetFI implements SetF {

	@Override
	public <T> Set<T> lagacySet() {
		return new HashSet<>();
	}

	@Override
	public <T> Set<T> legacySet(Collection<T> arg) {
		return new HashSet<>(arg);
	}

	@Override
	public <T> net.splitcells.dem.data.set.Set<T> set() {
		return null;
	}

	@Override
	public <T> net.splitcells.dem.data.set.Set<T> set(Collection<T> arg) {
		throw notImplementedYet();
	}

}
