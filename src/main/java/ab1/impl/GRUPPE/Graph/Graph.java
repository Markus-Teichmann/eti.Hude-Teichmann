package ab1.impl.GRUPPE.Graph;
import ab1.Transition;


import java.util.Collection;

public interface Graph {
    public boolean contains(Vertex v);
    public boolean contains(Edge e);
    public boolean contains(Graph g);
    public void addVertex(Vertex v);
    public void addEdge(Edge edge);
    public Collection<Vertex> getStart();
    public Collection<Vertex> getVertices();
    public Vertex getVertex(String name);
    public Collection<Edge> getEdges();
    public Collection<Character> getAlphabet();
    public Collection<Vertex> getConnected(Vertex v);
    public Collection<Vertex> getConnected(Collection<Vertex> v);
    public Collection<Vertex> getLeafs(String string);
    public Collection<Vertex> getLeafs(Vertex v);
    public Collection<Vertex> getLeafs(Collection<Vertex> vertices);
    public Graph getSubGraph(Vertex v);
    public void remove(Vertex v);
    public void remove(Edge e);
    public void remove(Graph g);
    public Graph clone();
    public void invert();

}
