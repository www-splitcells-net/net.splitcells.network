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
package net.splitcells.dem.environment.config.framework;

import java.util.function.Supplier;

/**
 * This template simplifies the implementation of new options.
 *
 * @param <Value> Type of the options value.
 */
public abstract class OptionI<Value> implements Option<Value> {

	protected final Supplier<Value> defaultValue;

	public OptionI(Supplier<Value> arg_default_value) {
		defaultValue = arg_default_value;
	}

	@Override
	public Value defaultValue() {
		return defaultValue.get();
	}

}
