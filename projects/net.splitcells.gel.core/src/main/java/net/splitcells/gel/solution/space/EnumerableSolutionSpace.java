package net.splitcells.gel.solution.space;

import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.SolutionView;

/**
 * <p>This is a wrapper for a {@link Solution}.</p>
 * <p>It is used in order to organize changes to the wrapped {@link Solution}.
 * If all changes were applied {@link #endDiscovery} is used in order to
 * retrieve the wrapped value.</p>
 */
public interface EnumerableSolutionSpace {
    /**
     * Applies a possible change to the wrapped {@link Solution}.
     * 
     * @param index The index of the change. These are numbered from 0 to {@link #childrenCount} - 1.
     * @return Returns this wrapper with the applied changes.
     */
    @ReturnsThis
    EnumerableSolutionSpace child(int index);

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
     *
     * @return Returns this wrapper with the last change reverted.
     */
    @ReturnsThis
    EnumerableSolutionSpace parent();

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
}
