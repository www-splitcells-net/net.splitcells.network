package net.splitcells.dem.config;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * IDEA Get values out of properties file.
 */
public final class StaticFlags {
	public static final boolean ENFORCING_UNIT_CONSISTENCY = true;
	public static final boolean FUZZING = true;
	public static final boolean ENFORCING_INTEGRATION_CONSISTENCY = true;
	@Deprecated
	public static final boolean PROFILING_RUNTIME = true;
	@Deprecated
	public static final boolean PROFILING_MEMORY_USAGE = true;
	@Deprecated
	public static final boolean TELLING_STORY = true;
	@Deprecated
	public static final boolean WARNING = true;
	public static final boolean TRACING = true;

	private StaticFlags() {
		throw constructorIllegal();
	}
}