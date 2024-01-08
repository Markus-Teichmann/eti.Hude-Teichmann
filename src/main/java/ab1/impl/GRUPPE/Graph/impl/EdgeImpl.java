package ab1.impl.GRUPPE.Graph.impl;

import ab1.impl.GRUPPE.Graph.Edge;
import ab1.impl.GRUPPE.Graph.Vertex;

public class EdgeImpl implements Edge {
    private Vertex start;
    private Character transition;
    private Vertex end;
    public EdgeImpl(Vertex start, Character transition, Vertex end) {
        this.start = start;
        if(transition == null) {
            this.transition = null;
        } else {
            this.transition = transition;
        }
        this.end = end;
    }
    @Override
    public Vertex getStartVertex() {
        return this.start;
    }
    @Override
    public Character getTransition() {
        return this.transition;
    }
    @Override
    public Vertex getEndVertex() {
        return this.end;
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof Edge) {
            return ((Edge) o).getStartVertex().equals(this.start) &&
                    ((Edge) o).getEndVertex().equals(this.end) &&
                    (((Edge) o).getTransition() == this.transition ||
                            (this.transition == null && ((Edge) o).getTransition() == null));
        } else {
            return super.equals(o);
        }
    }
    @Override
    public String toString() {
        return start.getName() + " -" + transition + "-> " + end.getName();
    }
}
