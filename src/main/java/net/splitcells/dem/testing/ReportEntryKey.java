package net.splitcells.dem.testing;

/**
 * @see org.junit.platform.engine.reporting.ReportEntry
 */
public class ReportEntryKey<T> {

	public static final ReportEntryTimeKey START_TIME = new ReportEntryTimeKey();

	public static final ReportEntryTimeKey END_TIME = new ReportEntryTimeKey();

	private final Class<T> clazz;

	protected ReportEntryKey(Class<T> argClazz) {
		clazz = argClazz;
	}

	public Class<T> key() {
		return clazz;
	}

	public String keyString() {
		return clazz.getName();
	}

}
