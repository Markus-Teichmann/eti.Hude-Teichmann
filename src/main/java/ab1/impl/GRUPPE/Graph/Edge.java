package ab1.impl.GRUPPE.Graph;

public interface Edge {
    public Vertex getStartVertex();
    public Character getTransition();
    public Vertex getEndVertex();
    public Edge clone();
}
