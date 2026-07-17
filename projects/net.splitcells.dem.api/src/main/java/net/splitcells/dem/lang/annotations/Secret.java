/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.annotations;

/**
 * <p>This is only used for documentation purposes.</p>
 * <p>Values marked with {@link Secret}, have security relevance and can be seen as private keys,
 * in order to get additional access.</p>
 * <p>These values should not be passed around outside their context.</p>
 * <p>This could be something like a password, where only a limited part of code should have access to such a password.</p>
 * <p>In publisher and subscriber models, the identity of a subscriber can be seen as a private key,
 * in order to remove an existing subscriber from its subscription.
 * Such a system can require the identity of the subscriber as a prove,
 * that an accessor has the right to remove the subscription from the subscriber.
 * In such a case the subscriber value can also be marked as {@link Secret}.</p>
 */
public @interface Secret {

}
