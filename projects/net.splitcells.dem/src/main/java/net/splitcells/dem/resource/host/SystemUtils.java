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
package net.splitcells.dem.resource.host;

import net.splitcells.dem.utils.ConstructorIllegal;

import java.io.*;

public final class SystemUtils {
	public SystemUtils() {
		throw new ConstructorIllegal();
	}

	public static void executeProgram(String... command) {
		// REMOVE or write output to log.
		System.out.println(command);
		final Process process;
		try {
			process = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			// FIXME Print Process output while waiting for process's completion.
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String inputLine = null;
			String errorLine = null;
			try {
				// Some commands wait for input although they do not needed to.
				process.getOutputStream().close();
				while ((inputLine = inputReader.readLine()) != null || (errorLine = errorReader.readLine()) != null) {

					if (errorLine != null) {
						System.out.println(errorLine);
					}
					if (inputLine != null) {
						System.out.println(inputLine);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			process.waitFor();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void executeShellCommand(String script) {
		executeProgram("/bin/bash", "-c", script);
	}

	/**
	 * REMOVE
	 *
	 * This is a general tactic in order to execute shell scripts:
	 * https://stackoverflow.com/questions/26830617/java-running-bash-commands
	 * 
	 * @return
	 */
	@Deprecated
	private static File createTempScript(String script) {
		File tempScript;
		try {
			tempScript = File.createTempFile("script", null);
			Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
			PrintWriter printWriter = new PrintWriter(streamWriter);

			printWriter.println("#!/bin/bash");
			printWriter.println(script);

			printWriter.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return tempScript;
	}

}
