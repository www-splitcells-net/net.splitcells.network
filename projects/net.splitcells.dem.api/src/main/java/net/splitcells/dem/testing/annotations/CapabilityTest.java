/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing.annotations;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.testing.TestTypes;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag(TestTypes.CAPABILITY_TEST)
@Test
@JavaLegacy
/**
 * Tests that are more like {@link UnitTest}, but require a lot of resources can be placed here,
 * in order to speed up the default build.
 */
public @interface CapabilityTest {
}
