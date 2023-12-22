package ab1.impl.GRUPPE;

import ab1.FinalizedStateException;
import ab1.NFA;
import ab1.Transition;

import java.util.*;

public class NFAImpl implements NFA {
    private final State initialState;
    private Map<String, State> unconnectedStates;
    private Set<Transition> transitions;
    public NFAImpl(String initialStateName) {
        this.initialState = new State(initialStateName);
        this.unconnectedStates = new HashMap<String, State>();
        this.transitions = new HashSet<Transition>();
    }
    private Set<State> states() {
        return initialState.getIterable(initialState, null, (State s) -> s.getNext());
    }
    private boolean contains(String s) {
        return this.getStates().contains(s);
    }
    private boolean contains(State s) {
        return this.states().contains(s);
    }
    private State getState(String name) {
        if(this.contains(name)) {
            for(State s : this.states()) {
                if(s.getName().equals(name)) {
                    return s;
                }
            }
        } else if (unconnectedStates.containsKey(name)) {
            return unconnectedStates.get(name);
        }
        this.unconnectedStates.put(name, new State(name));
        return unconnectedStates.get(name);
    }
    @Override
    public Set<String> getStates() {
        Set<String> strings = new HashSet<String>();
        for(State s : this.states()){
            strings.add(s.getName());
        }
        for(State s : unconnectedStates.values()) {
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
        for(State state : this.unconnectedStates.values()) {
            if(state.getAcceptence() == State.Acceptance.ACCEPTING) {
                strings.add(state.getName());
            }
        }
        return strings;
    }
    @Override
    public String getInitialState() {
        return initialState.getName();
    }
    @Override
    public void addTransition(Transition t) throws FinalizedStateException {
        if (isFinalized()) {
            throw new FinalizedStateException("Can't add transition to finalized automata");
        }
        this.transitions.add(t);
        if (this.contains(t.fromState())) {
            State fromState = getState(t.fromState());
            fromState.addTransition(t.readSymbol(), getState(t.toState()));
            if(unconnectedStates.containsKey(t.toState())) {
                State toState = unconnectedStates.get(t.toState());
                Set<State> set = toState.getIterable(toState, null, (State s) -> s.getNext());
                for(State s : set) {
                    unconnectedStates.remove(s.getName(),s);
                }
            }
        } else if (this.unconnectedStates.containsKey(t.fromState())) {
            unconnectedStates.get(t.fromState()).addTransition(t.readSymbol(),getState(t.toState()));
        } else {
            State fromState = getState(t.fromState());
            State toState = getState(t.toState());
            fromState.addTransition(t.readSymbol(), toState);
        }
    }
    @Override
    public void addAcceptingState(String name) throws FinalizedStateException {
        if(isFinalized()) {
            throw new FinalizedStateException("Can't add accepting state to finalized automata");
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
        List<State> duplicates = new ArrayList<State>();
        duplicates.add(initialState);
        initialState.getIterable(initialState, null, (State s) -> {
            duplicates.addAll(s.getNext());
            return s.getNext();
        });
        for(State s : states()) {
            duplicates.remove(s);
        }
        Set<State> reachableAcceptingStates = new HashSet<State>();
        for(State state : duplicates) {
            state.getIterable(state, null, (State s) -> {
               if(s.getAcceptence() == State.Acceptance.ACCEPTING) {
                   reachableAcceptingStates.add(s);
               }
               return s.getNext();
            });
        }
        return reachableAcceptingStates.isEmpty();
    }
    @Override
    public boolean acceptsWord(String word) {
        Set<State> set = new HashSet<>();
        set.add(initialState);
        for(Character c : word.toCharArray()) {
            set = initialState.getIterable(set, c, (State s) -> s.getNext(c));
        }
        for(State state : set) {
            if(state.getAcceptence() == State.Acceptance.ACCEPTING) {
                return true;
            }
        }
        return false;
    }
}
