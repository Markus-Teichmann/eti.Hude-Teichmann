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
    /*
        Use with care! Das Clonen der Edges erzeugt auch immer neue Vertices.
        Somit lassen sich geclonte Vertices nicht verbinden! da der End bzw. Startknoten immer ein neuer ist
        Folgich ist dies Methode eigentlich für nichts gut.
     */
    @Override
    public Edge clone() {
        return new EdgeImpl(start.clone(), transition, end.clone());
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof Edge) {
            return ((Edge) o).getStartVertex().getName().equals(this.start.getName()) &&
                    ((Edge) o).getTransition() == this.transition &&
                    ((Edge) o).getEndVertex().getName().equals(this.end.getName());
        } else {
            return super.equals(o);
        }
    }
    @Override
    public int hashCode() {
        return 0;
    }
    @Override
    public String toString() {
        return start.getName() + " -" + transition + "-> " + end.getName();
    }
}
