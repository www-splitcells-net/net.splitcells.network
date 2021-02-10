package net.splitcells.gel.solution.optimization.primitive;

public final class SupplySelection {

    public static SupplySelection supplySelection(int selectedIndex, boolean currentlyFree) {
        return new SupplySelection(selectedIndex, currentlyFree);
    }

    private final int selectedIndex;
    private final boolean isCurrentlyFree;
    
    private SupplySelection(int selectedIndex, boolean isCurrentlyFree) {
        this.selectedIndex = selectedIndex;
        this.isCurrentlyFree = isCurrentlyFree;
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public boolean isCurrentlyFree() {
        return isCurrentlyFree;
    }
}
