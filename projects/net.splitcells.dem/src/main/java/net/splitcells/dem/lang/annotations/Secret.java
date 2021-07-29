/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.lang.annotations;

/**
 * This values is only used for documentation purposes.
 * <p/>
 * Values marked with {@link Secret}, have security relevance and can be seen as private keys,
 * in order to get additional access.
 * These values should not be passed around outside of their context.
 * <p/>
 * This could be something like a password, where only a limited part of code should have access to such a password.
 * <p/>
 * In publisher and subscriber models, the identity of a subscriber can be seen as a private key,
 * in order to remove an existing subscriber from its subscription.
 * Such a system can require the identity of the subscriber as an prove, that
 * an accessor has the right to remove the subscription from the subscriber.
 * In such a case the subscriber value can also be marked as {@link Secret}.
 */
public @interface Secret {

}
