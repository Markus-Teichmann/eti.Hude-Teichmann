package ab1.impl.gruppe32_hude_teichmann.graph;


import java.util.Collection;

public interface Graph {
    public Collection<Vertex> getStart();
    public Collection<Vertex> getVertices();
    public Collection<Edge> getEdges(Collection<Vertex> vertices);
    public Collection<Character> getAlphabet(Collection<Vertex> vertices);
    public Collection<Vertex> getProximity(Integer radius, Collection<Character> transition, Collection<Vertex> start);

    public boolean contains(Vertex v);
    public boolean contains(Edge e);
    public boolean contains(Graph g);
    public void addVertex(Vertex v);
    public void addEdge(Edge edge);
    public Vertex getVertex(String name);
    public void remove(Graph g);
    public void invert();
    public Graph clone();
}
