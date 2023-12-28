package ab1.impl.GRUPPE;

import ab1.FinalizedStateException;
import ab1.NFA;
import ab1.Transition;
import ab1.impl.GRUPPE.Graph.Edge;
import ab1.impl.GRUPPE.Graph.Graph;
import ab1.impl.GRUPPE.Graph.Vertex;
import ab1.impl.GRUPPE.Graph.impl.EdgeImpl;
import ab1.impl.GRUPPE.Graph.impl.GraphImpl;

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
            if(s.getNext(getAlphabet(getVertices())).size() > 1) {
                return false;
            }
        }
        return true;
    }
    private void toDFA() {
        //setUnconnected(null);
        //NFAImpl dfa = clone();
        Map<State, Collection<Vertex>> roots = new HashMap<>();
        State initialState = new State("");
        for(Vertex v : getStart()) {
            initialState = (State) v;
        }
        roots.put(initialState, getStart());
        Collection<State> newStates = new HashSet<>(roots.keySet());
        int size;
        do {
            size = newStates.size();
            Map<State, Collection<Vertex>> leafs = new HashMap<>();
            for(State s : roots.keySet()) {
                System.out.println("===");
                System.out.println(s);
                System.out.println("---");
                for(Character c : getAlphabet(getVertices())) {
                    Collection<Vertex> next = new HashSet<>();
                    String name = "";
                    System.out.println("Erreichbar mit: " + c);
                    if(c == null) {
                        for(Vertex v : getProximity(null, new HashSet<>(){{add(c);}}, roots.get(s))) {
                            name += v.getName();
                            next.add((State) v);
                        }
                        System.out.println("Läuft nicht.");
                    } else {
                        for(Vertex v : getProximity(1, new HashSet<>(){{add(c);}}, roots.get(s))) {
                            System.out.println(v);
                            name += v.getName();
                            //if(!proximity ist empty und !durch Vertauschung der Buchstaben im Namen kommen wir zu einem Zustand im KeySet)
                            next.add((State) v);
                        }
                    }
                    State state = new State(name);
                    leafs.put(state, next);
                }
            }
            roots.clear();
            roots.putAll(leafs);
            newStates.addAll(leafs.keySet());
        } while (size < newStates.size());
        for(State s : newStates) {
            System.out.println(s);
        }


        /*
        Map<State, Map<Character, Collection<State>>> stateUnion = new HashMap<>();
        for(Vertex vert : getStart()) {
            State s = (State) vert;
            if(stateUnion.containsKey(s)) {
                for(Character c : getAlphabet(getVertices())) {
                    for (State oldState : stateUnion.get(s)) {

                    }
                }
            } else {
                if(s.getNext(new HashSet<>(){{add(null);}}) != null && !s.getNext(new HashSet<>(){{add(null);}}).isEmpty()) {
                    Collection<State> oldStates = new HashSet<>();
                    String name = s.getName();
                    for(Vertex v : getProximity(null, new HashSet<>(){{add(null);}}, new HashSet<>(){{add(s);}})) {
                        name += "";
                        oldStates.add((State) v);
                    }
                    State state = new State(name);
                    for(State oldState : oldStates) {
                        if(oldState.getAcceptence() == State.Acceptance.ACCEPTING) {
                            state.setAcceptance(State.Acceptance.ACCEPTING);
                            break;
                        }
                    }
                    System.out.println("Sollte nicht laufen!");
                    stateUnion.put(state, new HashMap<>(){{put(null, oldStates);}});
                }
                for(Character c : getAlphabet(getVertices())) {
                    if(s.getNext(new HashSet<>(){{add(c);}}) != null && s.getNext(new HashSet<>(){{add(c);}}).size() > 1) {
                        Collection<State> oldStates = new HashSet<>();
                        String name = "";
                        for(Vertex v : s.getNext(new HashSet<>(){{add(c);}})) {
                            name += v.getName();
                            oldStates.add((State) v);
                        }
                        State state = new State(name);
                        stateUnion.put(state, new HashMap<>(){{put(c, oldStates);}});
                    }
                }
            }
        }
        for(State s : stateUnion.keySet()) {
            System.out.println(s);
        }
         */
        /*
        Set<State> transformed = new HashSet<>();
        for(Vertex v : getStart()) {
            transformed.add((State) v);
        }
        Collection<Vertex> roots = new HashSet<>(transformed);
        do {
            for(Vertex s : getProximity(null, new HashSet<>(){{add(null);}},roots)) {


            }
        } while(!dfa.isDFA());
         */
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
    /*
        Diese Methode gibt eine Deep-Copy des aktuellen NFA's zurück.
     */
    public NFAImpl clone() {
        return null;
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
        return null;
    }
    @Override
    public NFA intersection(NFA other) throws FinalizedStateException {
        return null;
    }
    @Override
    public NFA concatenation(NFA other) throws FinalizedStateException {
        return null;
    }
    @Override
    public NFA kleeneStar() throws FinalizedStateException {
        return null;
    }
    @Override
    public NFA plusOperator() throws FinalizedStateException {
        return null;
    }
    @Override
    public NFA complement() throws FinalizedStateException {
        toDFA();
        //Zuerst in DFA, dann in das Komplement des DFAs. Der ist ja selbst auch ein NFA.
        return null;
    }
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
            for(Vertex v : roots) {
                Character c = word.charAt(i);
                if(v.getNext(new HashSet<Character>(){{add(c);}}) == null) {
                    leafs.remove(v); //Wenn wir einen Knoten erreicht haben von dem es nicht weiter geht müssen wir diesen entfernen.
                } else {
                    leafs.addAll(v.getNext(new HashSet<Character>(){{add(c);}}));
                }
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
}
