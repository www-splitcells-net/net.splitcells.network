/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing;

public class Mocking {
	private Mocking() {

	}

	/**
	 * TODO
	 */
	public static Object anyObject() {
		return new Object();
	}

	/**
	 * TODO
	 */
	public static Class<?> anyClass() {
		return Object.class;
	}

	/**
	 * TODO
	 */
	public static String anyString() {
		return "";
	}

	/**
	 * TODO
	 */
	public static Integer anyInt() {
		return 7;
	}

}
