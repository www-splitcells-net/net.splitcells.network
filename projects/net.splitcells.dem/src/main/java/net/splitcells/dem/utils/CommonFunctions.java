package net.splitcells.dem.utils;

import java.io.*;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;

/**
 * TODO cleanup
 * 
 * Caller is the routine which is calling the callee.
 * <p>
 * From the perspective of the caller the thing which is passed is an argument.
 * From the perspective of the routine that receives the call, i.e. the callee,
 * the thing which is passed is a parameter.
 */
public class CommonFunctions {

	public static <T> T removeAny(Set<T> arg) {
		final T rVal = arg.iterator().next();
		arg.remove(rVal);
		return rVal;
	}

	public static int hash_code(Object... args) {
		return Objects.hash(args);
	}

	public static void findSystemOutput() {
		System.setOut(new PrintStream(System.out) {
			@Override
			public void println(long arg) {
				new RuntimeException().printStackTrace();
			}

			@Override
			public void println(Object arg) {
				new RuntimeException().printStackTrace();
			}

			@Override
			public void println(String arg) {
				new RuntimeException().printStackTrace();
			}
		});
	}

	public static String currentTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss.SSS"));
	}

	public static void disableSystemOutput() {
		final PrintStream outF = new PrintStream(System.out);
		System.setOut(new PrintStream(System.out) {
			public void println(String x) {
			}

			public void println(long x) {
			}

			public void println(boolean x) {

			}

			public void println(Object x) {
			}

			public void println(int x) {
			}
		});
	}

	public static void makeSystemOutputTracing() {
		final PrintStream outF = new PrintStream(System.out);
		System.setOut(new PrintStream(System.out) {
			public void println(String x) {
				outF.println(x);
				new Throwable().printStackTrace(outF);
			}

			public void println(long x) {
				outF.println(x);
				new Throwable().printStackTrace(outF);
			}

			public void println(Object x) {
				outF.println(x);
				new Throwable().printStackTrace(outF);
			}

			public void println(int x) {
				outF.println(x);
				new Throwable().printStackTrace(outF);
			}
		});
	}

	public static String toString(Throwable arg) {
		return arg.getMessage() + "\n" + CommonFunctions.stackTraceString(arg);
	}

	public static String stackTraceString(Throwable arg) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		arg.printStackTrace(pw);
		return sw.toString();
	}

	public static void appendToFile(Path filePath, String content) {
		FileOutputStream basicOutput = null;
		OutputStreamWriter managedOutput = null;
		FileLock outputFileLock = null;
		try {
			File file = filePath.toFile();
			if (!file.exists()) {
				file.createNewFile();
			}
			basicOutput = new FileOutputStream(file);
			managedOutput = new OutputStreamWriter(basicOutput, "UTF8");
			outputFileLock = basicOutput.getChannel().lock();
			managedOutput.append(content);
			managedOutput.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				outputFileLock.release();
				basicOutput.close();
				managedOutput.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
     * TODO variadic argument support
	 */
	public static <T extends Object> T[] concat(T[] a, T[] b) {
		final T[] rVal = (T[]) new Object[a.length + b.length];
		int current_index = 0;

		for (int i = 0; i < a.length; i++) {
			rVal[current_index++] = a[i];
		}
		for (int i = 0; i < b.length; i++) {
			rVal[current_index++] = b[i];
		}
		return rVal;
	}

	private CommonFunctions() {
		throw new UnsupportedOperationException();
	}
}
