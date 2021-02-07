package net.splitcells.dem.lang.annotations;

/**
 * This values is only used for documentation purposes.
 * <p/>
 * Values marked with {@link Private}, have security relevance and can be seen as private keys,
 * in order to get additional access.
 * These values should not be passed around outside of their context.
 * <p/>
 * This could be something like a password, where only a limited part of code should have access to such a password.
 * <p/>
 * In publisher and subscriber models, the identity of a subscriber can be seen as a private key,
 * in order to remove an existing subscriber from its subscription.
 * Such a system can require the identity of the subscriber as an prove, that
 * an accessor has the right to remove the subscription from the subscriber.
 * In such a case the subscriber value can also be marked as {@link Private}.
 */
public @interface Private {

}
