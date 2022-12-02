/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.merger.basic.experimental;

import static java.util.Arrays.asList;
import static net.splitcells.dem.merger.basic.experimental.ForEach.forEach;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import lombok.val;

/**
 * TODO Benchmark the effects that ForEach.IS_ONLY_STUBBING has on memory
 * consumption and runtime performance.
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ForEach.class, ForEachTest.class })
public class ForEachTest {

	private static final String RANDOM_STRING = "RANDOM_STRING";
	private static final String RANDOM_STRING_CONCATINATION = RANDOM_STRING + RANDOM_STRING;

	/**
	 * TODO File bug ticket for Powermockito/mockito: If the methods
	 * testStaticOnlyStubOption and testStaticOnlyStubOption2 have the same name
	 * than "java.lang.IllegalArgumentException: wrong number of arguments" is
	 * thrown at attr
	 * org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl$PowerMockJUnit44MethodRunner.runTestMethod(PowerMockJUnit44RunnerDelegateImpl.java:326).
	 */
	@Test
	public void testStaticOnlyStubOption() {
		testThatForEachReturnsMock2(true);
		testThatForEachReturnsMock2(false);
	}

	public void testThatForEachReturnsMock2(boolean stubOnly) {
		ForEach.IS_ONLY_STUBBING = stubOnly;
		// By default a mock returns "" then toString() is invoked on that mock.
		assertThat(ForEach.forEach(asList(new StringBuffer(), new StringBuffer())).toString()).isEmpty();
	}

	/**
	 * FIXME Primitive parameter of get is causing problems.
	 */
	@Test
	public void testPrimitiveParameter() {
		List<List<StringBuffer>> testData = new ArrayList<>(asList(new ArrayList<>(), new ArrayList<>()));
		testData.get(0).add(new StringBuffer(RANDOM_STRING));
		testData.get(1).add(new StringBuffer());
		forEach(testData).get(0).append(RANDOM_STRING);
		assertThat(testData.get(0).toString()).isEqualTo(RANDOM_STRING_CONCATINATION);
		assertThat(testData.get(1).toString()).isEqualTo(RANDOM_STRING);
	}

	/**
	 * An {@link List} implementation that has get method with no primitive
	 * parameters.
	 */
	public static class ListI<T> extends ArrayList<T> {
		public ListI() {
		}

		public ListI(List<T> values) {
			super(values);
		}

		public T getValue(Integer index) {
			return get(index);
		}
	}

	@Test
	public void testMethodsNotReturnThisViaNonPrimitiveParameters() {
		ListI<ListI<StringBuffer>> testData = new ListI<>(asList(new ListI<>(), new ListI<>()));
		testData.get(0).add(new StringBuffer(RANDOM_STRING));
		testData.get(1).add(new StringBuffer());
		forEach(testData).getValue(0).append(RANDOM_STRING);
		assertThat(testData.getValue(0).getValue(0).toString()).isEqualTo(RANDOM_STRING_CONCATINATION);
		assertThat(testData.getValue(0).getValue(0).toString()).isEqualTo(RANDOM_STRING);
	}

	private static void appendRandomString(StringBuffer arg) {
		arg.append(RANDOM_STRING);
	}

	private static Consumer<StringBuffer> appendString(String string) {
		return i -> i.append(string);
	}

	/**
	 * TODO Use an alternative to multiple string concatenation for better
	 * demonstration.
	 *
	 * This test show cases alternatives. Note that this is not about the actual
	 * StringBuffer operations.
	 */
	@Test
	public void testStandardLibraryClass() {
		val testSubjects = new ArrayList<>(asList(new StringBuffer(), new StringBuffer()));
		val controlGroup = new ArrayList<>(asList(new StringBuffer(), new StringBuffer()));
		val anotherControlGroup = new ArrayList<>(asList(new StringBuffer(), new StringBuffer()));
		val anotherOneControlGroup = new ArrayList<>(asList(new StringBuffer(), new StringBuffer()));
		{
			forEach(testSubjects).append(RANDOM_STRING).append(RANDOM_STRING);
			controlGroup.forEach(i -> i.append(RANDOM_STRING).append(RANDOM_STRING));
			{
				anotherControlGroup.forEach(ForEachTest::appendRandomString);
				anotherControlGroup.forEach(ForEachTest::appendRandomString);
			}
			{
				anotherOneControlGroup.forEach(appendString(RANDOM_STRING));
				anotherOneControlGroup.forEach(appendString(RANDOM_STRING));
			}
		}
		controlGroup.stream().forEach(i -> assertThat(i.toString()).isEqualTo(RANDOM_STRING_CONCATINATION));
		testSubjects.stream().forEach(i -> assertThat(i.toString()).isEqualTo(RANDOM_STRING_CONCATINATION));
		anotherControlGroup.stream().forEach(i -> assertThat(i.toString()).isEqualTo(RANDOM_STRING_CONCATINATION));
		anotherOneControlGroup.stream().forEach(i -> assertThat(i.toString()).isEqualTo(RANDOM_STRING_CONCATINATION));
	}
}