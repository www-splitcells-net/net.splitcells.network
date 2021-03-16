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
