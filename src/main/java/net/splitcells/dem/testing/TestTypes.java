package net.splitcells.dem.testing;

import net.splitcells.dem.utils.ConstructorIllegal;

public final class TestTypes {

	public static final String UNIT_TEST = "testing.unit";
	public static final String INTEGRATION_TEST = "testing.integration";
	public static final String CAPABILITY_TEST = "testing.capability";
	public static final String BENCHMARK_RUNTIME = "benchmarking.runtime";
	public static final String BENCHMARK_MEMORY_USAGE = "benchmarking.memoryUsage";
	public static final String PROFILING_RUNTIME = "profiling.runtime";
	public static final String PROFILING_MEMORY_USAGE = "profiling.memoryUsage";

	private TestTypes() {
		throw new ConstructorIllegal();
	}

}
