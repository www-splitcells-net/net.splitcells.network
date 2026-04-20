/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.access;

import lombok.val;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.website.server.security.authentication.Authentication.anonymous;
import static net.splitcells.website.server.security.authentication.Authentication.lifeCycleToken;

public class AccessContainerTest {
    @UnitTest
    public void test() {
        val testSubject = AccessContainer.<String>accessContainer();
        val user1 = anonymous();
        val user2 = anonymous();
        requireThrow(() -> testSubject.access(a -> {
        }, user1));
        requireEquals(testSubject.createAndAccess(u -> "test", a -> a + " 2", user1)
                , "test 2");
        requireEquals(testSubject.createAndAccess(u -> "query", a -> a + " 3", user2)
                , "query 3");
        requireThrow(() -> testSubject.createAndAccess(u -> "query", a -> a + " 3", user2));
        testSubject.access(a -> requireEquals(a, "test"), user2, lifeCycleToken(user1));
        testSubject.access(a -> requireEquals(a, "query"), user1, lifeCycleToken(user2));
        testSubject.access((u, a) -> requireEquals(a, "test"), user2, lifeCycleToken(user1));
        testSubject.access((u, a) -> requireEquals(a, "query"), user1, lifeCycleToken(user2));
        requireEquals(testSubject.process(a -> a + " 3", user2, lifeCycleToken(user1)), "test 3");
        requireEquals(testSubject.process(a -> a + " 4", user1, lifeCycleToken(user2)), "query 4");
        testSubject.delete(user1);
        requireThrow(() -> testSubject.access(a -> {
        }, user2, lifeCycleToken(user1)));
        testSubject.access(a -> {
        }, user1, lifeCycleToken(user2));
        testSubject.access(a -> {
        }, user2);
        testSubject.delete(user1, lifeCycleToken(user2));
        requireThrow(() -> testSubject.access(a -> {
        }, user2));
    }
}
