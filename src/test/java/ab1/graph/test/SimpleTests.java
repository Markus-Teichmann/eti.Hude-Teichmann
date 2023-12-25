package ab1.graph.test;

import ab1.impl.GRUPPE.Graph.Edge;
import ab1.impl.GRUPPE.Graph.Graph;
import ab1.impl.GRUPPE.Graph.Vertex;
import ab1.impl.GRUPPE.Graph.impl.EdgeImpl;
import ab1.impl.GRUPPE.Graph.impl.GraphImpl;
import ab1.impl.GRUPPE.Graph.impl.VertexImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleTests {
    private static Graph g;
    private static Edge e1;
    private static Edge e2;
    private static Edge e3;
    private static Edge e4;
    private static Edge e5;


    @BeforeAll
    public static void setup(){
        Vertex start = new VertexImpl("s0");
        Vertex s1 = new VertexImpl("s1");
        Vertex s2 = new VertexImpl("s2");
        Vertex end = new VertexImpl("s3");
        e1 = new EdgeImpl(start, 'a', s1);
        e2 = new EdgeImpl(start, 'a', s2);
        e3 = new EdgeImpl(s1, 'a', s2);
        e4 = new EdgeImpl(s2, 'a', end);
        e5 = new EdgeImpl(end, 'a', s1);
        g = new GraphImpl(start);
        /*
        Collection<Edge> edges = new HashSet<>();
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);
        edges.add(e5);
         */
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
    }
    @Test
    public void addingVertecies() {
        Vertex start = new VertexImpl("start");
        Vertex s1 = new VertexImpl("s1");
        Vertex s2 = new VertexImpl("s2");
        Vertex end = new VertexImpl("end");
        Graph g = new GraphImpl(start);
        g.addVertex(s1);
        g.addVertex(s2);
        g.addVertex(end);
        assertTrue(g.contains(start));
        assertTrue(g.contains(s1));
        assertTrue(g.contains(s2));
        assertTrue(g.contains(end));
        //assertTrue();
    }
    @Test
    public void addingEdges() {
        assertTrue(g.contains(e1));
        assertTrue(g.contains(e2));
        assertTrue(g.contains(e3));
        assertTrue(g.contains(e4));
        assertTrue(g.contains(e5));
    }
    @Test
    public void cloneTest() {
        Graph h = g.clone();
        assertEquals(g,h);
    }
    @Test
    public void invertTest() {
        Graph h = g.clone();
        h.invert();
        h.invert();
        System.out.println(g.equals(h));
        assertEquals(g,h);
    }
}
