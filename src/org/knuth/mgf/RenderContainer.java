package org.knuth.mgf;

/**
 * @author Fabian Bottler
 * @version 1.0
 */
public class RenderContainer implements Comparable<RenderContainer>{
    private int zIndex = -1;
    private RenderEvent event;

    public RenderContainer(int zIndex, RenderEvent event){
        this.zIndex = zIndex;
        this.event = event;
    }
    public int getZIndex() {
        return zIndex;
    }

    public RenderEvent getEvent() {
        return event;
    }




    @Override
    public int compareTo(RenderContainer index) {
        if(zIndex<index.getZIndex()){
            return 1;
        }else if(zIndex>index.getZIndex()){
            return -1;
        }else{
            return 0;
        }
    }
}
