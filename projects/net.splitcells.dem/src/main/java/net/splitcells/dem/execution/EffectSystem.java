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
package net.splitcells.dem.execution;

/**
 * TODO This should provide the basis for parallelization.
 * <br />
 * TODO Make it as simple as possible from API side.
 * <p>
 * The idea is to create an event based effect system, which can also be used in functional(=side effect free)
 * programs. Currently there is no effort being made, to make the executor of such a system explicitly
 * side effect free. It is enough to ensure that a side effect free executor can be implemented
 * and use it as an alternative executor without changing the code that uses the effect system.
 * </p>
 * <p>
 * In Java the disruptor pattern, the actor model and distributed event streaming platforms (like Kafka) are similar thins.
 * This interface should be implementable via Thread, thread pools, actor model frameworks and the disruptor pattern.
 * </p>
 * <p>
 * TODO Understand disruptor pattern.
 * <ol>
 *     <li>TODO LMAX</li>
 *     <li>TODO http://www.coralblocks.com/index.php/category/coralqueue/</li>
 *     <li>TODO SEDA</li>
 * </ol>
 * </p>
 * <p>
 * TODO Understand actor model.
 * <ol>
 *     <li>TODO https://medium.com/@zakgof/type-safe-actor-model-for-java-7133857a9f72</li>
 *     <li>TODO https://github.com/zakgof/actr This seems to be the most promising, regarding API.</li>
 *     <li>TODO https://www.baeldung.com/akka-actors-java</li>
 * </ol>
 * </p>
 */
@Deprecated
public interface EffectSystem<T> extends EventProcessor, EventQueue<T> {
}
