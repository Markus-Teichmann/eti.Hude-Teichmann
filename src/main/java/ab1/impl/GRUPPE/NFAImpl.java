package ab1.impl.GRUPPE;

import ab1.FinalizedStateException;
import ab1.NFA;
import ab1.Transition;
import ab1.impl.GRUPPE.Graph.Edge;
import ab1.impl.GRUPPE.Graph.Vertex;
import ab1.impl.GRUPPE.Graph.impl.EdgeImpl;
import ab1.impl.GRUPPE.Graph.impl.GraphImpl;
import ab1.impl.GRUPPE.Graph.impl.VertexImpl;

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
        if(!contains(new State(name))) {
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
        //GetConnected scheint mir hier nicht richtig zu sein.
        Set<State> poi = new HashSet<>();
        for(State s : states()) {
            if(getConnected(s).contains(s)) {
                poi.add(s);
            }
        }
        for(State s : poi) {
            for(Vertex v : getConnected(s)) {
                if (((State) v).getAcceptence() == State.Acceptance.ACCEPTING) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public boolean acceptsWord(String word) {
        for(Vertex v : getLeafs(word)) {
            if(((State) v).getAcceptence() == State.Acceptance.ACCEPTING) {
                return true;
            }
        }
        return false;
    }
}
