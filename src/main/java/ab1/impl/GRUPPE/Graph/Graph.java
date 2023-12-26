package ab1.impl.GRUPPE.Graph;


import java.util.Collection;

public interface Graph {
    public Collection<Vertex> getStart();
    public Collection<Vertex> getVertices();
    public Collection<Edge> getEdges();
    public Collection<Character> getAlphabet();
    public Collection<Vertex> getProximity(Integer radius, Collection<Character> transition, Collection<Vertex> start);

    public boolean contains(Vertex v);
    public boolean contains(Edge e);
    public boolean contains(Graph g);
    public void addVertex(Vertex v);
    public void addEdge(Edge edge);
    public Vertex getVertex(String name);
    public Graph getSubGraph(Vertex v);
    public void remove(Vertex v);
    public void remove(Edge e);
    public void remove(Graph g);
    public Graph clone();
    public void invert();
}
