package net.splitcells.gel.solution.optimization.space;

import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.SolutionView;

import java.util.Optional;

/**
 * <p>This is a wrapper for a {@link Solution}.</p>
 * <p>It is used in order to organize changes to the wrapped {@link Solution}.
 * If all changes were applied {@link #endDiscovery} is used in order to
 * retrieve the wrapped value.</p>
 * <p>Note that this interface does represent the same as the feasible region
 * or the feasible set.
 * The reason for this, is the fact, that this space does not necessarily
 * consider things like hard constraints.
 * This interface is called optimization space, because the possible
 * values are generally defined via {@link net.splitcells.gel.solution.optimization.OnlineOptimization}.
 * </p>
 * <p>This interface can also be used as an interface to a search tree
 * and can be used as a base for tree search algorithms.
 * In such a use case, it is recommended to make this space deterministic.</p>
 * <p>This space is considered enumerable,
 * because all children have an unique index,
 * that identifies the child in question</p>
 * <p>TODO This is currently based on side effects. This should not be the case in the future.</p>
 * <p>TODO Invalidate object after certain method calls.</p>
 */
public interface EnumerableOptimizationSpace {
    /**
     * Applies a possible change to the wrapped {@link Solution}.
     *
     * @param index The index of the change. These are numbered from 0 to {@link #childrenCount} - 1.
     * @return Returns a wrapper with the applied changes.
     */
    EnumerableOptimizationSpace child(int index);

    /**
     * Returns the number of possible changes,
     * that can be applied to the wrapped {@link Solution}.
     * These changes are numbered from 0 to {@link #childrenCount} - 1.
     *
     * @return Number of possible changes.
     */
    int childrenCount();

    /**
     * Reverts the last change.
     * If this is the root element,
     * then nothing is returned.
     *
     * @return Returns a wrapper with the last change reverted.
     */
    Optional<EnumerableOptimizationSpace> parent();

    /**
     * @return Read only view on the wrapped {@link Solution}.
     */
    SolutionView currentState();

    /**
     * Returns the wrapped {@link Solution} and prevents further changes to the
     * wrapped {@link Solution} by the wrapper.
     *
     * @return The Wrapped {@link Solution}
     */
    Solution endDiscovery();
    
    int historyIndex();
}
