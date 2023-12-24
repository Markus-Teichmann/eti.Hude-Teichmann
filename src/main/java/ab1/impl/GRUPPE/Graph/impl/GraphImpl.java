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
    private Collection<Edge> getForwardEdges() {
        Collection<Edge> edges = new HashSet<>();
        for(Vertex v : getVertices()) {
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
    private Collection<Edge> getBackwardEdges() {
        Collection<Edge> edges = new HashSet<>();
        for(Vertex v : getVertices()) {
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
    @Override
    public Collection<Edge> getEdges() {
        Collection<Edge> edges = getForwardEdges();
        edges.addAll(getBackwardEdges());
        return edges;
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
        /*
        for(Vertex vertex : getVertices()) {
            if(vertex.equals(v)) {
                return true;
            }
        }
        return false;
         */
        return getVertices().contains(v);
    }
    @Override
    public boolean contains(Edge e) {
        /*
        for(Edge edge : getEdges()) {
            if(edge.equals(e)) {
                return true;
            }
        }
        return false;
         */
        return getEdges().contains(e);
    }
    @Override
    public boolean contains(Graph graph) {
        if(unconnected == null) {
            if(graph == this) {
                return true;
            } else {
                return false;
            }
        } else {
            for(Graph g : unconnected) {
                if(g.contains(graph)) {
                    return true;
                }
            }
            return false;
        }
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
        if(contains(v)) {
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
        Collection<Vertex> start = new HashSet<>(this.start);
        Graph clone = new GraphImpl(start);
        for(Vertex v : getVertices()) {
            clone.addVertex(new VertexImpl(v.getName()));
        }
        for(Edge e : getEdges()) {
            clone.addEdge(e);
        }
        return clone;
    }
    @Override
    public void invert() {
        Collection<Edge> forwardEdges = getForwardEdges();
        Collection<Edge> backwardEdges = getBackwardEdges();
        /*
            start = iterate(start, Vertex::getNext, (Vertex v) -> {
                if(v.getNext() == null){
                    return v;
                }
                return null;
             });
         */
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
}
