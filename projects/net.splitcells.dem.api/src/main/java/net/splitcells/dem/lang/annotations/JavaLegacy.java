/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.annotations;

/**
 * <p>Marks things that are only present for compatibility with Java or its ecosystem.
 * Files containing code marked as such are not checked for portability automatically.
 * Portable code and legacy code should not be mixed inside a Java file,
 * as this makes it harder to port the portable code.
 * </p>
 * <p>If this project's source code would to be translated to another programming language via an automated tool,
 * code blocks with this annotations would have to be created, removed or changed manually.
 * The reason for this, is the fact, that these code blocks are specific to Java or Java libraries.</p>
 */
public @interface JavaLegacy {

}
