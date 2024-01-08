package ab1.impl.GRUPPE.Graph.impl;

import ab1.impl.GRUPPE.Graph.Edge;
import ab1.impl.GRUPPE.Graph.Graph;
import ab1.impl.GRUPPE.Graph.Vertex;
import ab1.impl.GRUPPE.Graph.utils.SetOperations;

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
    private Collection<Edge> getForwardEdges(Collection<Vertex> vertices) {
        Collection<Edge> edges = new HashSet<>();
        for(Vertex v : vertices) {
            if(v.getOutgoingEdges() != null) {
                for (Character c : v.getOutgoingEdges()) {
                    for (Vertex vertex : v.getNext(new HashSet<Character>(){{add(c);}})) {
                        SetOperations.add(edges, new EdgeImpl(v, c, vertex));
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
                        SetOperations.add(edges, new EdgeImpl(vertex, c, v));
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
        SetOperations.addAll(connected, start);
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
        SetOperations.addAll(edges, getBackwardEdges(vertices));
        return edges;
    }
    @Override
    public Collection<Character> getAlphabet(Collection<Vertex> vertices) {
        Collection<Character> alphabet = new HashSet<>();
        for(Edge e : getEdges(vertices)) {
            SetOperations.add(alphabet, e.getTransition());
        }
        return alphabet;
    }
    /*
        @param radius Ist radius == null so ist es ein unbeschränkter Radius.
    */
    @Override
    public Collection<Vertex> getProximity(Integer radius, Collection<Character> transitions, Collection<Vertex> start) {
        if(radius != null && radius == 0) {
            return null;
        } else {
            Collection<Vertex> proximity = new HashSet<>();
            Collection<Vertex> roots = new HashSet<Vertex>(start);
            Collection<Vertex> leafs = new HashSet<Vertex>();
            int size;
            int i = 0;
            do {
                size = proximity.size();
                for(Vertex v : roots) {
                    SetOperations.addAll(leafs, v.getNext(transitions));
                }
                roots.clear();
                roots.addAll(leafs);
                proximity.addAll(leafs);
                leafs.clear();
                i++;
            } while(size < proximity.size() && (radius == null || i != radius));
            return proximity;
        }
    }
    @Override
    public boolean contains(Vertex vert) {
        return SetOperations.contains(getVertices(), vert);
    }
    @Override
    public boolean contains(Edge edge) {
        return SetOperations.contains(getEdges(getVertices()), edge);
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
            SetOperations.add(unconnected, new GraphImpl(v));
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
                SetOperations.add(unconnected, new GraphImpl(edge.getStartVertex()));
            }
            if(unconnected != null) {
                Collection<Graph> graphsToBeRemoved = new HashSet<>();
                for(Graph g : unconnected) {
                    if (g.contains(edge.getEndVertex())) {
                        SetOperations.add(graphsToBeRemoved, g);
                    }
                }
                for(Graph g : graphsToBeRemoved) {
                    unconnected.remove(g);
                }
            }
        }
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
    public Graph clone() {
        Collection<Vertex> vertices = new HashSet<>();
        for(Vertex v : getVertices()) {
            SetOperations.add(vertices, v.clone());
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
            SetOperations.add(edges, new EdgeImpl(start, e.getTransition(), end));
        }
        Collection<Vertex> start = new HashSet<>();
        for(Vertex v : vertices) {
            if(SetOperations.contains(this.start, v)) {
                SetOperations.add(start, v);
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
        SetOperations.addAll(vertices, start);
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
            Zuerst einmal versuchen wir den selben Start zu behalten!
         */
        Collection<Vertex> newVertices = getProximity(null, null, start);
        SetOperations.addAll(newVertices, start);
        if(!SetOperations.equals(newVertices,vertices)) {
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
                for (; (1 << logOfI) < i; logOfI++) ;
                for (int j = 0; j <= logOfI; j++) {
                    if (((i >> j) & 1) == 1) { //Von Rechts nach Links die Postionen der Binärzahl i an denen eine 1 steht.
                        if (((Vertex) vertices.toArray()[j]).getNext(getAlphabet(vertices)) != null &&
                                !((Vertex) vertices.toArray()[j]).getNext(getAlphabet(vertices)).isEmpty()) {
                            SetOperations.add(end, (Vertex) vertices.toArray()[j]);
                        }
                    }
                }
                collection.clear();
                SetOperations.addAll(collection, getProximity(null, getAlphabet(vertices), end));
                SetOperations.addAll(collection, end);
                i++;
            } while (!SetOperations.equals(collection, vertices));
            start.clear();
            start.addAll(end);
        }
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof Graph) {
            if(((Graph) o).getStart() == null) {
                return false;
            }
            for(Vertex v : this.getStart()) {
                boolean contains = false;
                for(Vertex vert : ((Graph) o).getStart()) {
                    if(vert.equals(v)) {
                        contains = true;
                        break;
                    }
                }
                if(!contains) {
                    return false;
                }
            }
            for(Vertex v : ((Graph) o).getStart()) {
                boolean contains = false;
                for(Vertex vert : this.getStart()) {
                    if(vert.equals(v)) {
                        contains = true;
                        break;
                    }
                }
                if(!contains) {
                    return false;
                }
            }
            for(Vertex v : this.getVertices()) {
                if(!((Graph) o).contains(v)) {
                    return false;
                }
            }
            for(Vertex v : ((Graph) o).getVertices()) {
                if(!this.contains(v)) {
                    return false;
                }
            }
            for(Edge e : this.getEdges(this.getVertices())) {
                if(!((Graph) o).contains(e)) {
                    return false;
                }
            }
            for(Edge e : ((Graph) o).getEdges(((Graph) o).getVertices())) {
                if(!this.contains(e)) {
                    return false;
                }
            }
            return true;
        } else {
            return super.equals(o);
        }
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
    public void setUnconnected(Collection<Graph> unconnected) {
        this.unconnected = unconnected;
    }
    public void setStart(Collection<Vertex> start) {
        this.start = start;
    }
}