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
    private static Vertex s0;
    private static Vertex s1;
    private static Vertex s2;
    private static Vertex s3;
    private static Vertex s4;
    private static Vertex s5;
    private static Vertex s6;
    private static Vertex s7;
    private static Vertex s8;
    private static Vertex s9;
    private static Vertex s10;
    private static Edge e1;
    private static Edge e2;
    private static Edge e3;
    private static Edge e4;
    private static Edge e5;


    @BeforeAll
    public static void setup(){
        s0 = new VertexImpl("s0");
        s1 = new VertexImpl("s1");
        s2 = new VertexImpl("s2");
        s3 = new VertexImpl("s3");
        s4 = new VertexImpl("s4");
        s5 = new VertexImpl("s5");
        s6 = new VertexImpl("s6");
        s7 = new VertexImpl("s7");
        s8 = new VertexImpl("s8");
        s9 = new VertexImpl("s9");
        s10 = new VertexImpl("s10");
        e1 = new EdgeImpl(s0, 'a', s1);
        e2 = new EdgeImpl(s0, 'a', s2);
        e3 = new EdgeImpl(s1, 'a', s2);
        e4 = new EdgeImpl(s2, 'a', s3);
        e5 = new EdgeImpl(s3, 'a', s1);
        g = new GraphImpl(s0);
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
        assertEquals(g,h);
    }
    @Test
    public void proximityTest() {
        Edge e6 = new EdgeImpl(s3, null, s4);
        Edge e7 = new EdgeImpl(s4, 'b', s5);
        Edge e8 = new EdgeImpl(s3, null, s5);
        Edge e9 = new EdgeImpl(s4, 'b', s6);
        g.addEdge(e6);
        g.addEdge(e7);
        g.addEdge(e8);
        g.addEdge(e9);
        Collection<Character> transitions = new HashSet<>();
        transitions.add(null);
        transitions.add('a');
        transitions.add('b');
        Collection<Vertex> start = new HashSet<>();
        start.add(s3);
        Collection<Vertex> proximity = g.getProximity(2, transitions, start);
        for(Vertex v : proximity) {
            System.out.println(v);
        }
    }
}
