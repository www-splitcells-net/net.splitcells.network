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

import static org.mockito.Mockito.withSettings;
import static org.powermock.api.mockito.PowerMockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import lombok.val;

/**
 * TODOC
 *
 */
public class ForEach {

	/**
	 *
	 * TODO Better configuration
	 */
	public static boolean IS_ONLY_STUBBING = new Boolean(
			System.getProperty("net.splitcells.den.merger.basic.experimental.ForEach.stubOnly", "true"));

	/**
	 * TODOC
	 *
	 * FIXME Empty collections are currently not supported:
	 * https://coderanch.com/t/383648/java/java-reflection-element-type-List
	 */
	@SuppressWarnings("unchecked")
	public static <T> T forEach(Collection<T> arg) {
		if (arg.isEmpty()) {
			throw new UnsupportedOperationException();
		}
		if (IS_ONLY_STUBBING) {
			return (T) mock(arg.iterator().next().getClass(),
					withSettings().stubOnly().defaultAnswer(interceptor(arg)));
		}
		return (T) mock(arg.iterator().next().getClass(), interceptor(arg));
	}

	/**
	 * TODOC
	 *
	 * TEST
	 */
	private static <T> Answer<Object> interceptor(Collection<T> arg) {
		return new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				List<Object> rVal = new ArrayList<>(arg.size());
				for (T e : arg) {
					try {
						val method = e.getClass().getMethod(invocation.getMethod().getName(),
								convert(invocation.getArguments(), i -> i.getClass(),
										new Class<?>[invocation.getArguments().length]));
						Object rInvocation = method.invoke(e, invocation.getArguments());
						if (rInvocation != null) {
							rVal.add(rInvocation);
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
						throw new RuntimeException(ex);
					}
				}
				if (rVal.isEmpty()) {
					return null;
				}
				return forEach(rVal);
			}

		};
	}

	/**
	 * TEST
	 *
	 * TODO Move to not experimental part.
	 */
	private static <A, R> R[] convert(A[] from, Function<A, R> converter, R[] to) {
		assert from.length == to.length;
		IntStream.range(0, from.length).forEach(i -> to[i] = converter.apply(from[i]));
		return to;
	}

}
