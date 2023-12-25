package ab1.impl.GRUPPE.Graph.impl;

import ab1.impl.GRUPPE.Graph.Edge;
import ab1.impl.GRUPPE.Graph.Graph;
import ab1.impl.GRUPPE.Graph.Vertex;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

public class GraphImpl implements Graph {
    private Collection<Vertex> start;
    private Collection<Graph> unconnected;
    public GraphImpl(Vertex v) {
        this.start = new HashSet<Vertex>();
        start.add(v);
        unconnected = null;
    }
    public GraphImpl(Collection<Vertex> start) {
        this.start = start;
        unconnected = null;
    }
    private <T> Collection<T> iterate(Collection<Vertex> start, Function<Vertex,Collection<Vertex>> getter, Function<Vertex,T> function) {
        Collection<T> values = new HashSet<T>();
        Collection<Vertex> roots = new HashSet<Vertex>(start);
        Collection<Vertex> leafs = new HashSet<Vertex>();
        Collection<Vertex> set = new HashSet<Vertex>(start);
        int size;
        do {
            size = set.size();
            for(Vertex v : roots) {
                Collection<Vertex> get = getter.apply(v);
                if(get != null) {
                    leafs.addAll(get);
                }
                T value = function.apply(v);
                if(value != null) {
                    values.add(value);
                }
            }
            roots.clear();
            roots.addAll(leafs);
            set.addAll(leafs);
            leafs.clear();
        } while(size < set.size());
        return values;
    }
    private Collection<Edge> getForwardEdges(Collection<Vertex> vertices) {
        Collection<Edge> edges = new HashSet<>();
        for(Vertex v : vertices) {
            if(v.getOutgoingEdges() != null) {
                for (Character c : v.getOutgoingEdges()) {
                    for (Vertex vertex : v.getNext(c)) {
                        edges.add(new EdgeImpl(v, c, vertex));
                    }
                }
            }
        }
        return edges;
    }
    private Collection<Edge> getBackwardEdges(Collection<Vertex> vertices) {
        Collection<Edge> edges = new HashSet<>();
        for(Vertex v : vertices) {
            if(v.getIncommingEdges() != null) {
                for (Character c : v.getIncommingEdges()) {
                    for (Vertex vertex : v.getPrev(c)) {
                        edges.add(new EdgeImpl(vertex, c, v));
                    }
                }
            }
        }
        return edges;
    }
    @Override
    public Collection<Vertex> getStart() {
        return start;
    }
    @Override
    public Collection<Vertex> getVertices() {
        Collection<Vertex> connected = new HashSet<>();
        connected.addAll(start);
        connected.addAll(getConnected(connected));
        if(unconnected != null) {
            for(Graph g : unconnected) {
                connected.addAll(g.getVertices());
            }
        }
        return connected;
    }
    private Collection<Edge> getEdges(Collection<Vertex> vertices) {
        Collection<Edge> edges = getForwardEdges(vertices);
        edges.addAll(getBackwardEdges(vertices));
        return edges;
    }
    @Override
    public Collection<Edge> getEdges() {
        return getEdges(getVertices());
    }
    @Override
    public Collection<Character> getAlphabet() {
        Collection<Character> alphabet = new HashSet<>();
        for(Edge e : getEdges()) {
            alphabet.add(e.getTransition());
        }
        return alphabet;
    }
    @Override
    public boolean contains(Vertex v) {
        return getVertices().contains(v);
    }
    @Override
    public boolean contains(Edge e) {
        return getEdges().contains(e);
    }
    @Override
    public boolean contains(Graph graph) {
        if(this.equals(graph)) {
            return true;
        }
        if(unconnected != null) {
            for(Graph g : unconnected) {
                if(g.contains(graph)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addVertex(Vertex v) {
        if(!contains(v)) {
            if(unconnected == null) {
                this.unconnected = new HashSet<>();
            }
            unconnected.add(new GraphImpl(v));
        }
    }
    @Override
    public void addEdge(Edge edge) {
        if(!contains(edge)) {
            edge.getStartVertex().addNext(edge.getTransition(), edge.getEndVertex());
            edge.getEndVertex().addPrev(edge.getTransition(), edge.getStartVertex());
            if(!contains(edge.getStartVertex())) {
                if(unconnected == null) {
                    unconnected = new HashSet<>();
                }
                unconnected.add(new GraphImpl(edge.getStartVertex()));
            } else if(unconnected != null) {
                for(Graph g : unconnected) {
                    if (g.contains(edge.getEndVertex())) {
                        remove(getSubGraph(edge.getEndVertex()));
                    }
                }
            }
        }
    }
    @Override
    public void remove(Vertex v) {

    }
    @Override
    public void remove(Edge e) {

    }
    @Override
    public void remove(Graph graph) {
        if(unconnected != null) {
            if(unconnected.contains(graph)) {
                unconnected.remove(graph);
            } else {
                for (Graph g : unconnected) {
                    if (g.contains(graph)) {
                        g.remove(graph);
                    }
                }
            }
        }
    }
    @Override
    public Vertex getVertex(String name) {
        for(Vertex v : getVertices()) {
            if(v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }
    @Override
    public Collection<Vertex> getConnected(Vertex v) {
        Collection<Vertex> vertices = new HashSet<>();
        vertices.add(v);
        return getConnected(vertices);
    }
    @Override
    public Collection<Vertex> getConnected(Collection<Vertex> vertices) {
        Collection<Vertex> connected = new HashSet<>();
        Collection<Collection<Vertex>> collections = iterate(vertices, Vertex::getNext, (Vertex v) -> v.getNext());
        for(Collection<Vertex> collection : collections) {
            connected.addAll(collection);
        }
        return connected;
    }
    @Override
    public Graph getSubGraph(Vertex v) {
        if(getConnected(start).contains(v)) {
            if(unconnected == null) {
                return this;
            } else {
                for(Graph g : unconnected) {
                    if(g.contains(v)) {
                        return g.getSubGraph(v);
                    }
                }
            }
        }
        return null;
    }
    @Override
    public Collection<Vertex> getLeafs(Vertex v) {
        Collection<Vertex> vertices = new HashSet<>();
        vertices.add(v);
        return getLeafs(vertices);
    }
    @Override
    public Collection<Vertex> getLeafs(Collection<Vertex> vertices) {
        return iterate(vertices, Vertex::getNext, (Vertex v) -> v.getNext().isEmpty() ? v : null);
    }
    @Override
    public Collection<Vertex> getLeafs(String string) {
        Collection<Vertex> roots = new HashSet<>(start);
        Collection<Vertex> leafs = new HashSet<>();
        for(int i=0; i<string.length(); i++) {
            int size;
            do {
                size = roots.size();
                roots.addAll(iterate(roots,(Vertex v) -> v.getNext(null), (Vertex v) -> v));
            } while(size < roots.size());
            for(Vertex v : roots) {
                if(v.getNext(string.charAt(i)) == null) {
                    leafs.remove(v);
                } else {
                    leafs.addAll(v.getNext(string.charAt(i)));
                }
            }
            do {
                size = leafs.size();
                leafs.addAll(iterate(leafs, (Vertex v) -> v.getNext(null), (Vertex v) -> v));
            } while(size < leafs.size());
            roots.clear();
            roots.addAll(leafs);
            leafs.clear();
        }
        return roots;
    }
    @Override
    public Graph clone() {
        Collection<Vertex> vertices = new HashSet<>();
        for(Vertex v : getVertices()) {
            vertices.add(v.clone());
        }
        Collection<Edge> edges = new HashSet<>();
        for(Edge e : getEdges()) {
            Vertex start = null;
            for(Vertex v : vertices) {
                if(v.equals(e.getStartVertex())) {
                    start = v;
                }
            }
            Vertex end = null;
            for(Vertex v : vertices) {
                if(v.equals(e.getEndVertex())) {
                    end = v;
                }
            }
            edges.add(new EdgeImpl(start, e.getTransition(), end));
        }
        Collection<Vertex> start = new HashSet<>();
        for(Vertex v : vertices) {
            if(this.start.contains(v)) {
                start.add(v);
            }
        }
        Graph clone = new GraphImpl(start);
        for(Edge e : edges) {
            clone.addEdge(e);
        }
        return clone;
    }
    @Override
    public void invert() {
        Collection<Edge> forwardEdges = getForwardEdges(getVertices());
        Collection<Edge> backwardEdges = getBackwardEdges(getVertices());
        Collection<Vertex> end = new HashSet<>();
        for(Vertex v : getVertices()) {
            if(v.getNext() == null) {
                end.add(v);
            }
        }
        start.clear();
        start.addAll(end);
        for(Edge e : forwardEdges) {
            e.getStartVertex().removeNext(e.getTransition(), e.getEndVertex());
        }
        for(Edge e: backwardEdges) {
            e.getEndVertex().removePrev(e.getTransition(), e.getStartVertex());
        }
        for(Edge e : forwardEdges) {
            e.getEndVertex().addPrev(e.getTransition(), e.getStartVertex());
        }
        for(Edge e : backwardEdges) {
            e.getStartVertex().addNext(e.getTransition(), e.getEndVertex());
        }
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof Graph) {
            if(!((Graph) o).getVertices().equals(this.getVertices())) {
                return false;
            }
            if(!((Graph) o).getEdges().equals(this.getEdges())) {
                return false;
            }
            return true;
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
        String s = "+========= Graph =========+\n";
        s += "|  +------ Start ------+  |\n";
        for(Vertex v : start) {
            s += "|  |        " + v.toString() + "         |  |\n";
        }
        s += "|  +-------------------+  |\n";
        s += "|                         |\n";
        s += "|  +---- Connected ----+  |\n";
        for(Vertex v : getConnected(start)) {
            s += "|  |        " + v.toString() + "         |  |\n";
        }
        s += "|  +-------------------+  |\n";
        s += "|                         |\n";
        s += "|  +------ Edges ------+  |\n";
        for(Edge e : getEdges(getConnected(start))) {
            s += "|  |    " + e.toString() + "     |  |\n";
        }
        s += "|  +-------------------+  |\n";
        if(unconnected != null) {
            s += "+=========================+\n\n";
            s += "+------- SubGraphs -------+\n";
            for(Graph g : unconnected) {
                s += g.toString() + "\n";
            }
            s += "+-------------------------+\n";
        } else {
            s += "+=========================+\n";
        }
        return s;
    }
}
