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
