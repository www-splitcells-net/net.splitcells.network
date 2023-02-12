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
 * This is only used for documentation purposes.
 * <p/>
 * Values marked with {@link Secret}, have security relevance and can be seen as private keys,
 * in order to get additional access.
 * These values should not be passed around outside their context.
 * <p/>
 * This could be something like a password, where only a limited part of code should have access to such a password.
 * <p/>
 * In publisher and subscriber models, the identity of a subscriber can be seen as a private key,
 * in order to remove an existing subscriber from its subscription.
 * Such a system can require the identity of the subscriber as a prove,
 * that an accessor has the right to remove the subscription from the subscriber.
 * In such a case the subscriber value can also be marked as {@link Secret}.
 */
public @interface Secret {

}
