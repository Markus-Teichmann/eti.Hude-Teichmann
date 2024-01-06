package ab1.impl.GRUPPE;

import ab1.FinalizedStateException;
import ab1.NFA;
import ab1.Transition;
import ab1.impl.GRUPPE.Graph.Edge;
import ab1.impl.GRUPPE.Graph.Graph;
import ab1.impl.GRUPPE.Graph.Vertex;
import ab1.impl.GRUPPE.Graph.impl.EdgeImpl;
import ab1.impl.GRUPPE.Graph.impl.GraphImpl;
import ab1.impl.GRUPPE.Graph.impl.VertexImpl;
import ab1.impl.GRUPPE.Graph.utils.SetOperations;

import java.sql.Array;
import java.util.*;

public class NFAImpl extends GraphImpl implements NFA {
    public NFAImpl(String initialStateName) {
        super(new State(initialStateName));
    }
    private boolean contains(String name) {
        for(Vertex v : this.getVertices()) {
            if(v.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    private boolean isDFA() {
        if(getVertices().size() > getStart().size() + getProximity(null, getAlphabet(getVertices()), getStart()).size()) {
            return false;
        }
        if(getAlphabet(getVertices()).contains(null)) {
            return false;
        }
        for(State s : states()) {
            for(Character c : getAlphabet(getVertices())) {
                if(s.getNext(new HashSet<>(){{add(c);}}).size() > 1) {
                    return false;
                }
            }
        }
        return true;
    }
    private void toDFA() {
        setUnconnected(null);
        Map<State, Collection<Vertex>> newStates = new HashMap<>();
        for(Vertex v : getStart()) {
            newStates.put(new State(v.getName()), getStart());
            break; //Nach konstruktor ist hier sowieso immer nur einer drin!
        }
        for(Vertex v : getVertices()) {
            String name = v.getName();
            Collection<Vertex> next = new HashSet<>(){{add(v);}};
            for(Vertex reached : getProximity(null, new HashSet<>(){{add(null);}}, new HashSet<>(){{add(v);}})) {
                name += reached.getName();
                SetOperations.add(next, reached);
            }
            if(next.size() > 1) {
                boolean exsists = false;
                for(State superState : newStates.keySet()) {
                    if(SetOperations.equals(newStates.get(superState), next)) {
                        exsists = true;
                        break;
                    }
                }
                if(!exsists) {
                    State superState = new State(name);
                    newStates.put(superState, next);
                }
            }
        }
        Map<State, Collection<Vertex>> roots = new HashMap<>(newStates);
        Map<State, Collection<? extends Vertex>> leafs = new HashMap<>();
        Collection<Edge> newEdges = new HashSet<>();
        State trap = new State("Falle");
        for(Character c : getAlphabet(getVertices())) {
            newEdges.add(new EdgeImpl(trap, c, trap));
        }
        int size;
        do {
            size = newStates.keySet().size();
            for(State root : roots.keySet()) {
                for(Character c : getAlphabet(getVertices())) {
                    Collection<State> next = new HashSet<>();
                    String name = "";
                    if(c != null) {
                        for (Vertex v : getProximity(1, new HashSet<>() {{add(c);}}, roots.get(root))) {
                            name += v.getName();
                            SetOperations.add(next, (State) v);
                        }
                        boolean exsits = false;
                        for (Vertex superState : newStates.keySet()) {
                            if (SetOperations.equals(newStates.get(superState), next)) {
                                exsits = true;
                                newEdges.add(new EdgeImpl(root, c, superState));
                                break;
                            }
                        }
                        if(next.isEmpty()) {
                            newEdges.add(new EdgeImpl(root, c, trap));
                        } else if (!exsits) {
                            State superState = new State(name);
                            newEdges.add(new EdgeImpl(root, c, superState));
                            leafs.put(superState, next);
                        }
                    }
                }
            }
            roots.clear();
            for(State superState : leafs.keySet()) {
                roots.put(superState, new HashSet<Vertex>());
                newStates.put(superState, new HashSet<>());
                for(Vertex leaf : leafs.get(superState)) {
                    roots.get(superState).add((State) leaf);
                    newStates.get(superState).add((State) leaf);
                    if(((State) leaf).getAcceptence() == State.Acceptance.ACCEPTING) {
                        superState.setAcceptance(State.Acceptance.ACCEPTING);
                    }
                }
            }
            leafs.clear();
        } while (size < newStates.keySet().size());
        for(State superState : newStates.keySet()) {
            if(SetOperations.contains(getStart(),superState)) {
                setStart(new HashSet<>(){{add(superState);}});
                break;
            }
        }
        for(Edge e : newEdges) {
            addEdge(e);
        }
    }
    private Set<State> states() {
        Set<State> states = new HashSet<>();
        for(Vertex v : getVertices()) {
            states.add((State) v);
        }
        return states;
    }
    private State getState(String name) {
        if(getVertex(name) == null) {
            return new State(name);
        } else {
            return (State) this.getVertex(name);
        }
    }

    @Override
    public Set<String> getStates() {
        Set<String> strings = new HashSet<String>();
        for(State s : states()) {
            strings.add(s.getName());
        }
        return strings;
    }

    @Override
    public Collection<Transition> getTransitions() {
        Collection<Transition> transitions = new HashSet<>();
        for(Edge e : getEdges(getVertices())) {
            transitions.add(new Transition(e.getStartVertex().getName(), e.getTransition(), e.getEndVertex().getName()));
        }
        return transitions;
    }

    @Override
    public Set<String> getAcceptingStates() {
        Set<String> strings = new HashSet<String>();
        for(State state : this.states()) {
            if(state.getAcceptence() == State.Acceptance.ACCEPTING) {
                strings.add(state.getName());
            }
        }
        return strings;
    }
    @Override
    public String getInitialState() {
        String states = "";
        for(Vertex v : getStart()) {
            states += v.getName() + " ";
        }
        return states.trim();
    }
    @Override
    public void addTransition(Transition t) throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException("Can't add transition to finalized automata");
        }
        State start = getState(t.fromState());
        State end = getState(t.toState());
        Edge edge = new EdgeImpl(start, t.readSymbol(), end);
        addEdge(edge);
    }
    @Override
    public void addAcceptingState(String name) throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException("Can't add accepting state to finalized automata");
        }
        if(!contains(name)) {
            addVertex(new State(name));
        }
        this.getState(name).setAcceptance(State.Acceptance.ACCEPTING);
    }
    @Override
    public NFA union(NFA other) throws FinalizedStateException {
        NFAImpl unionNFA = new NFAImpl("q0");
        ((NFAImpl) other).getState(other.getInitialState()).setName("q1");
        unionNFA.addEdge(new EdgeImpl(unionNFA.getState("q0"), null, ((NFAImpl) other).getState(other.getInitialState())));
        unionNFA.addEdge(new EdgeImpl(unionNFA.getState("q0"), null, this.getState(this.getInitialState())));

        return unionNFA;
    }
    @Override
    public NFA intersection(NFA other) throws FinalizedStateException {
        NFAImpl clone = (NFAImpl) this.clone();
        clone.complement();
        ((NFAImpl) other).complement();
        clone.union(other);
        clone.complement();
        System.out.println(clone);
        return clone;
    }
    @Override
    public NFA concatenation(NFA other) throws FinalizedStateException {
        NFAImpl clone = (NFAImpl) this.clone();
        for (String stateName : clone.getStates()) {
            if(stateName.equals("START")){
                clone.getState(stateName).setName("q0");
            } else if (stateName.equals("ACCEPT")){
                clone.getState(stateName).setName("q1");
            }
        }

        for (String stateName : other.getStates()) {
            if(stateName.equals("START")){
                ((NFAImpl) other).getState(stateName).setName("q2");
            } else if (stateName.equals("ACCEPT")){
                ((NFAImpl) other).getState(stateName).setName("q3");
            }
        }

        for(String acceptingStateName : clone.getAcceptingStates()){
            clone.getState(acceptingStateName).setAcceptance(State.Acceptance.DENYING);
        }

        clone.addEdge(new EdgeImpl(clone.getState("q1"), null, ((NFAImpl) other).getState(other.getInitialState())));


        return clone;
    }
    @Override
    public NFA kleeneStar() throws FinalizedStateException {
        NFAImpl starNFA = new NFAImpl("q0");
        starNFA.getState("q0").setAcceptance(State.Acceptance.ACCEPTING);
        this.getState("START").setName("q1");
        starNFA.addEdge(new EdgeImpl(starNFA.getState(starNFA.getInitialState()), null, this.getState("q1")));
        for (String acceptingStateName : this.getAcceptingStates()) {
            this.addEdge(new EdgeImpl(this.getState(acceptingStateName), null, this.getState("q1")));
        }

        return starNFA;
    }
    @Override
    public NFA plusOperator() throws FinalizedStateException {
        NFAImpl plusNFA = (NFAImpl) this.clone();
        plusNFA.kleeneStar();
        return plusNFA;
    }
    @Override
    public NFA complement() throws FinalizedStateException {
        NFAImpl clone = (NFAImpl) this.clone();
        clone.toDFA();
        for (State s : clone.states()) {
            if (s.getAcceptence() == State.Acceptance.ACCEPTING) {
                s.setAcceptance(State.Acceptance.DENYING);
            } else if (s.getAcceptence() == State.Acceptance.DENYING) {
                s.setAcceptance(State.Acceptance.ACCEPTING);
            }
        }
        return clone;
        /*
        System.out.println(this);
        NFAImpl clone = (NFAImpl) this.clone();
        clone.getState("ACCEPT").setName("q0");
        char[] realAlphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char alphabet = (Character) clone.getAlphabet(clone.getVertices()).toArray()[0];
        for (char c : realAlphabet){
            clone.addEdge(new EdgeImpl(clone.getState("q0"), c, clone.getState("qf")));
            clone.addEdge(new EdgeImpl(clone.getState("qf"), c, clone.getState("qf")));
            if (c != alphabet){
                clone.addEdge(new EdgeImpl(clone.getState("START"), c, clone.getState("qf")));
            }
        }
        for (State s : clone.states()) {
            if (s.getAcceptence() == State.Acceptance.ACCEPTING) {
                s.setAcceptance(State.Acceptance.DENYING);
            } else if (s.getAcceptence() == State.Acceptance.DENYING) {
                s.setAcceptance(State.Acceptance.ACCEPTING);
            }
        }
        clone.getState("qf").setAcceptance(State.Acceptance.ACCEPTING);
        System.out.println(clone);
        return clone;
         */
    }

    /*
        //Zuerst in DFA, dann in das Komplement des DFAs. Der ist ja selbst auch ein NFA.
        Graph graph = clone();
        Vertex start = (Vertex) graph.getStart().toArray()[0];
        NFAImpl clone = new NFAImpl(start.getName());
        clone.setStart(new HashSet<>(){{add(start);}});
        System.out.println("Aktueller NFA:");
        System.out.println(this);
        System.out.println();
        System.out.println("Geklonter NFA:");
        System.out.println(clone);
        clone.toDFA();
        System.out.println();
        System.out.println("NFA in DFA:");
        System.out.println(clone);
        clone.invert();
        System.out.println();
        System.out.println("DFA invertiert:");
        System.out.println(clone);
        for (State s : clone.states()) {
            if (s.getAcceptence() == State.Acceptance.ACCEPTING) {
                s.setAcceptance(State.Acceptance.DENYING);
            } else if (s.getAcceptence() == State.Acceptance.DENYING) {
                s.setAcceptance(State.Acceptance.ACCEPTING);
            }
        }
        System.out.println();
        System.out.println("Akzeptanz angepasst:");
        System.out.println(clone);
        return clone;
    }
     */
    @Override
    public boolean isFinalized() {
        return false;
    }
    @Override
    public void finalizeAutomaton() {

    }
    @Override
    public boolean isFinite() {
        Set<State> poi = new HashSet<>();
        for(State s : states()) {
            if(getProximity(null, getAlphabet(getVertices()), new HashSet<>(){{add(s);}}).contains(s)) {
                poi.add(s);
            }
        }
        for(State s : poi) {
            Collection<Vertex> collection = new HashSet<>();
            collection.add(s);
            for(Vertex v : getProximity(null, getAlphabet(getVertices()), collection)) {
                if (((State) v).getAcceptence() == State.Acceptance.ACCEPTING) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public boolean acceptsWord(String word) {
        Collection<Vertex> roots = new HashSet<>(getStart());
        Collection<Vertex> leafs = new HashSet<>();
        for(int i=0; i<word.length(); i++) {
            int size;
            roots.addAll(getProximity(null, new HashSet<Character>(){{add(null);}}, roots));
            Character c = word.charAt(i);
            if(getAlphabet(getVertices()).contains(c)) {
                for(Vertex v : roots) {
                    if(v.getNext(new HashSet<Character>(){{add(c);}}) == null) {
                        leafs.remove(v); //Wenn wir einen Knoten erreicht haben von dem es nicht weiter geht müssen wir diesen entfernen.
                    } else {
                        leafs.addAll(v.getNext(new HashSet<Character>(){{add(c);}}));
                    }
                }
            } else if(getState("Falle") != null) {
                if(getState("Falle").getAcceptence() == State.Acceptance.ACCEPTING) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
            leafs.addAll(getProximity(null, new HashSet<Character>(){{add(null);}}, leafs));
            roots.clear();
            roots.addAll(leafs);
            leafs.clear();
        }
        for(Vertex v : roots) {
            if(((State) v).getAcceptence() == State.Acceptance.ACCEPTING) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NFA clone() {
        Map<Vertex, State> clonedStates = new HashMap<>();
        for(State s : this.states()) {
            State clone = new State(s.getName());
            clone.setAcceptance(s.getAcceptence());
            clonedStates.put(s, clone);
        }
        Collection<Edge> clonedEdges = new HashSet<>();
        for(Edge e : this.getEdges(this.getVertices())) {
            Vertex start = (Vertex) clonedStates.get(e.getStartVertex());
            Vertex end = (Vertex) clonedStates.get(e.getEndVertex());
            Edge clone = new EdgeImpl(start, e.getTransition(), end);
            clonedEdges.add(clone);
        }
        Collection<Vertex> clonedStart = new HashSet<>();
        for(Vertex v : this.getStart()) {
            clonedStart.add((Vertex) clonedStates.get(v));
        }
        NFAImpl clone = new NFAImpl("clone");
        clone.setStart(clonedStart);
        for(Edge e : clonedEdges) {
            clone.addEdge(e);
        }
        return clone;
    }
}
