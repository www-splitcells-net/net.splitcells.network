package net.splitcells.dem.testing;

public class ReportEntryTimeKey extends ReportEntryKey<Long> {

	protected ReportEntryTimeKey() {
		super(Long.class);
	}

	public String currentValue() {
		return "" + System.nanoTime();
	}

}
