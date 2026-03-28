/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing.annotations;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.testing.TestTypes;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is needed, as combining {@link TestFactory} and {@link UnitTest} does not work.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag(TestTypes.UNIT_TEST)
@TestFactory
@JavaLegacy
public @interface UnitTestFactory {
}
