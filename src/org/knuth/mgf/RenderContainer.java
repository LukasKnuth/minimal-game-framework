package org.knuth.mgf;

/**
 * <p>Container for a {@code RenderEvent} with information about it's z-index.</p>
 * <p>The idea of this container is, that when the {@code RenderEvent}'s are called,
 *  the order in which they're called is indicated by the z-index. Therefor, this
 *  class implements {@code Comparable}, so a collection of it can be ordered.</p>
 * @author Fabian Bottler
 * @author Lukas Knuth
 * @version 1.1
 */
class RenderContainer implements Comparable<RenderContainer>{

    /** The z-index for this render-able object */
    private final int zIndex;
    /** The basic {@code RenderEvent} */
    private final RenderEvent event;

    /**
     * Create a new container holding a {@code RenderEvent} and it's z-index.
     * @param zIndex the z-index as a number {@code >= 0}.
     * @param event the {@code RenderEvent}.
     * @throws IllegalArgumentException if the given {@code zIndex} is negative.
     */
    public RenderContainer(int zIndex, RenderEvent event){
        if (zIndex < 0)
            throw new NullPointerException("The z-index can't be less then 0. Got '"+zIndex+"'");
        this.zIndex = zIndex;
        this.event = event;
    }

    /**
     * Returns the encapsulated {@code RenderEvent}.
     * @return the encapsulated {@code RenderEvent}.
     */
    public RenderEvent getEvent() {
        return event;
    }

    @Override
    public int compareTo(RenderContainer index) {
        return zIndex - index.zIndex;
    }
}
