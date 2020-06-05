package net.splitcells.dem.resource.communication;

public interface Flushable extends java.io.Flushable {

	/**
	 * Used in order to circumvent checked exceptions.
	 */
	void flush();

}
