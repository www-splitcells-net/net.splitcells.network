package net.splitcells.dem.data.order;

public enum Ordering {
	EQUAL, LESSER_THAN, GREATER_THAN;

	public Ordering invert() {
		if (this.equals(LESSER_THAN)) {
			return GREATER_THAN;
		} else if (this.equals(GREATER_THAN)) {
			return LESSER_THAN;
		} else {
			return EQUAL;
		}
	}
}