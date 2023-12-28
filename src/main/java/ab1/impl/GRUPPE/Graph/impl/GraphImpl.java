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
    private <T> Collection<T> iterate(Collection<Vertex> start, Integer radius, Function<Vertex,Collection<Vertex>> getter, Function<Vertex,T> function) {
        Collection<T> values = new HashSet<T>();
        Collection<Vertex> roots = new HashSet<Vertex>(start);
        Collection<Vertex> leafs = new HashSet<Vertex>();
        Collection<Vertex> set = new HashSet<Vertex>(start);
        int size;
        int i = 0;
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
            i++;
        } while(size < set.size() && (radius == null || i != radius));
        return values;
    }
    private Collection<Edge> getForwardEdges(Collection<Vertex> vertices) {
        Collection<Edge> edges = new HashSet<>();
        for(Vertex v : vertices) {
            if(v.getOutgoingEdges() != null) {
                for (Character c : v.getOutgoingEdges()) {
                    for (Vertex vertex : v.getNext(new HashSet<Character>(){{add(c);}})) {
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
                    for (Vertex vertex : v.getPrev(new HashSet<Character>(){{add(c);}})) {
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
        Collection<Vertex> connected = getProximity(null, null, start);
        connected.addAll(start);
        if(unconnected != null) {
            for(Graph g : unconnected) {
                connected.addAll(g.getVertices());
            }
        }
        return connected;
    }
    @Override
    public Collection<Edge> getEdges(Collection<Vertex> vertices) {
        Collection<Edge> edges = getForwardEdges(vertices);
        edges.addAll(getBackwardEdges(vertices));
        return edges;
    }
    @Override
    public Collection<Character> getAlphabet(Collection<Vertex> vertices) {
        Collection<Character> alphabet = new HashSet<>();
        for(Edge e : getEdges(vertices)) {
            alphabet.add(e.getTransition());
        }
        return alphabet;
    }
    /*
        @param radius Ist radius == null so ist es ein unbeschränkter Radius.
    */
    @Override
    public Collection<Vertex> getProximity(Integer radius, Collection<Character> transitions, Collection<Vertex> start) {
        if((radius != null && radius == 0) || (transitions != null && transitions.isEmpty())) {
            return start;
        } else {
            Collection<Vertex> proximity = new HashSet<>();
            Collection<Collection<Vertex>> collections = iterate(start, radius, (Vertex v) -> v.getNext(transitions), (Vertex v) -> v.getNext(transitions));
            for(Collection<Vertex> collection : collections) {
                proximity.addAll(collection);
            }
            return proximity;
        }
    }


    @Override
    public boolean contains(Vertex v) {
        return getVertices().contains(v);
    }
    @Override
    public boolean contains(Edge e) {
        return getEdges(getVertices()).contains(e);
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
    public Graph getSubGraph(Vertex v) {
        if(getProximity(null, getAlphabet(getVertices()), start).contains(v)) {
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
    public Graph clone() {
        Collection<Vertex> vertices = new HashSet<>();
        for(Vertex v : getVertices()) {
            vertices.add(v.clone());
        }
        Collection<Edge> edges = new HashSet<>();
        for(Edge e : getEdges(getVertices())) {
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
        if(unconnected != null) {
            for(Graph g : unconnected) {
                g.invert();
            }
        }
        Collection<Vertex> vertices = getProximity(null, getAlphabet(getVertices()), start);
        vertices.addAll(start);
        Collection<Edge> forwardEdges = getForwardEdges(vertices);
        Collection<Edge> backwardEdges = getBackwardEdges(vertices);
        for(Edge e : forwardEdges) {
            e.getStartVertex().removeNext(e.getTransition(), e.getEndVertex());
        }
        for(Edge e: backwardEdges) {
            e.getEndVertex().removePrev(e.getTransition(), e.getStartVertex());
        }
        for(Edge e : forwardEdges) {
            e.getEndVertex().addNext(e.getTransition(), e.getStartVertex());
        }
        for(Edge e : backwardEdges) {
            e.getStartVertex().addPrev(e.getTransition(), e.getEndVertex());
        }
        Collection<Vertex> end = new HashSet<>();
        /*
            Da wir hier eine geschlossenen Graphen betrachten, muss der Invertierte auch wieder ein geschlossener
            Graph sein. Es muss also eine Menge an Knoten geben von denen aus alle anderen Knoten in Vertices
            erreicht werden. Um diese zu finden, iterieren wir über die Potenzmenge von Vertices und hören auf, sobald
            wir eine passend Teilmenge gefunden haben. Bei dieser Programmierung wird auch darauf geachtet, alle Knoten
            der Teilmenge nachfolge Knoten haben.

            Anmerkung:
            Zählen wir Binär von 0 bis zur Größe einer Menge, so stellen die Positionen der 1en die Potenzmenge dar.
            Ein Beispiel: Seien M = {a, b, c} eine Menge so finden wir:
                a   b   c
            0   0   0   0 => {}
            1   0   0   1 => {c}
            2   0   1   0 => {b}
            3   0   1   1 => {b,c}
            4   1   0   0 => {a}
            5   1   0   1 => {a, c}
            6   1   1   0 => {a, b}
            7   1   1   1 => {a, b, c}
         */
        Collection<Vertex> collection = new HashSet<>();
        int i = 0;
        do {
            end.clear();
            int logOfI = 0;
            for(; (1 << logOfI) < i; logOfI++);
            for(int j=0; j <= logOfI; j++) {
                if(((i >> j) & 1) == 1) { //Von Rechts nach Links die Postionen der Binärzahl i an denen eine 1 steht.
                    if(((Vertex) vertices.toArray()[j]).getNext(getAlphabet(vertices)) != null &&
                            !((Vertex) vertices.toArray()[j]).getNext(getAlphabet(vertices)).isEmpty()) {
                        end.add((Vertex) vertices.toArray()[j]);
                    }
                }
            }
            collection.clear();
            collection.addAll(getProximity(null, getAlphabet(vertices), end));
            collection.addAll(end);
            i++;
        } while(collection.size() != vertices.size());
        start.clear();
        start.addAll(end);
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof Graph) {
            if(!((Graph) o).getVertices().equals(this.getVertices())) {
                return false;
            }
            if(!((Graph) o).getEdges(getVertices()).equals(this.getEdges(getVertices()))) {
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
        for(Vertex v : getProximity(null, getAlphabet(getVertices()), start)) {
            s += "|  |        " + v.toString() + "         |  |\n";
        }
        s += "|  +-------------------+  |\n";
        s += "|                         |\n";
        s += "|  +------ Edges ------+  |\n";
        for(Edge e : getEdges(getVertices())) {
            s += "|  |    " + e.toString() + "     |  |\n";
        }
        s += "|  +-------------------+  |\n";
        if(!(unconnected == null || unconnected.isEmpty())) {
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
    protected void setUnconnected(Collection<Graph> unconnected) {
        this.unconnected = unconnected;
    }
}