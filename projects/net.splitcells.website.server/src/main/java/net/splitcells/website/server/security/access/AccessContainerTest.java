/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.access;

import lombok.val;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.website.server.security.access.AccessContainer.accessContainer;
import static net.splitcells.website.server.security.authentication.Authentication.anonymous;
import static net.splitcells.website.server.security.authentication.Authentication.lifeCycleId;

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
        testSubject.access(a -> requireEquals(a, "test"), user2, lifeCycleId(user1));
        testSubject.access(a -> requireEquals(a, "query"), user1, lifeCycleId(user2));
        testSubject.access((u, a) -> requireEquals(a, "test"), user2, lifeCycleId(user1));
        testSubject.access((u, a) -> requireEquals(a, "query"), user1, lifeCycleId(user2));
        requireEquals(testSubject.process(a -> a + " 3", user2, lifeCycleId(user1)), "test 3");
        requireEquals(testSubject.process(a -> a + " 4", user1, lifeCycleId(user2)), "query 4");
        testSubject.delete(user1);
        requireThrow(() -> testSubject.access(a -> {
        }, user2, lifeCycleId(user1)));
        testSubject.access(a -> {
        }, user1, lifeCycleId(user2));
        testSubject.access(a -> {
        }, user2);
        testSubject.delete(user1, lifeCycleId(user2));
        requireThrow(() -> testSubject.access(a -> {
        }, user2));
    }
}
