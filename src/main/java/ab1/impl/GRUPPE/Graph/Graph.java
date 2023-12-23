package ab1.impl.GRUPPE.Graph;
import ab1.Transition;


import java.util.Collection;

public interface Graph {
    public Collection<Vertex> getVertices();
    public Collection<Edge> getEdges();
    public boolean invert();
    public void addVertex(Vertex v);
    public void addEdge(Edge edge);
    public Collection<Vertex> getLeafs(String string);
}
