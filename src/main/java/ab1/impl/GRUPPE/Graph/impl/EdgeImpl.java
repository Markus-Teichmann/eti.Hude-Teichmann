package ab1.impl.GRUPPE.Graph.impl;

import ab1.impl.GRUPPE.Graph.Edge;
import ab1.impl.GRUPPE.Graph.Vertex;

public class EdgeImpl implements Edge {
    private Vertex start;
    private char transition;
    private Vertex end;
    public EdgeImpl(Vertex start, char transition, Vertex end) {
        this.start = start;
        this.transition = transition;
        this.end = end;
    }
    @Override
    public Vertex getStartVertex() {
        return this.start;
    }
    @Override
    public char getTransition() {
        return this.transition;
    }
    @Override
    public Vertex getEndVertex() {
        return this.end;
    }
}
