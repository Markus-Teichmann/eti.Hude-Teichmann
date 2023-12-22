package ab1.impl.GRUPPE;

import ab1.FinalizedStateException;
import ab1.NFA;
import ab1.Transition;

import java.util.*;

public class NFAImpl implements NFA {
    private final State initialState;
    public NFAImpl(String initialStateName) {
        this.initialState = new State(initialStateName);
    }
    @Override
    public Set<String> getStates() {
        Set<String> strings = new HashSet<String>();
        for(State s : initialState.getAllPossiblyFollowingStates()){
            strings.add(s.getName());
        }
        return strings;
    }
    @Override
    public Set<String> getAcceptingStates() {
        Set<String> states = new HashSet<String>();
        for(State state : initialState.getAllPossiblyFollowingStates()) {
            if(state.getAcceptence() == State.Acceptance.ACCEPTING) {
                states.add(state.getName());
            }
        }
        return states;
    }
    @Override
    public String getInitialState() {
        return initialState.getName();
    }
    @Override
    public void addTransition(Transition transition) throws FinalizedStateException {
        if(isFinalized()) {
            throw new FinalizedStateException("Can't add transition to finalized automata");
        }
        if(this.contains(transition.fromState())) {
            State toState = new State(transition.toState());
            if(this.contains(transition.toState())) {
                try {
                    toState = this.find(transition.toState());
                } catch (NoSuchStateException e) {
                    System.out.println(e.getMessage());
                }
            }
            try {
                this.find(transition.fromState()).addTransition(transition.readSymbol(), toState);
            } catch (NoSuchStateException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    private boolean contains(String string) {
        for(State s : initialState.getAllPossiblyFollowingStates()) {
            if(s.getName().equals(string)) {
                return true;
            }
        }
        return false;
    }
    private State find(String string) throws NoSuchStateException {
        for(State state : initialState.getAllPossiblyFollowingStates()) {
            if(state.getName().equals(string)) {
                return state;
            }
        }
        throw new NoSuchStateException(string);
    }
    @Override
    public void addAcceptingState(String state) throws FinalizedStateException {
        if(isFinalized()) {
            throw new FinalizedStateException("Can't add accepting state to finalized automata");
        } else {
            try {
                this.find(state).setAcceptance(State.Acceptance.ACCEPTING);
            } catch (NoSuchStateException e) {
                System.out.println(e.getMessage());
            }
        }
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
        return false;
    }

    @Override
    public boolean acceptsWord(String word) {
        System.out.println("Wort: " + word);
        System.out.println("Größe des Graphen: " + initialState.getAllPossiblyFollowingStates().size());
        Set<State> roots = new HashSet<State>();
        roots.add(initialState);
        Set<State> leafs = new HashSet<State>();
        while(word.length() > 0){ //Die Frage ist halt ob wir so sehr an der Wortlänge festhalten können, denn es gibt ja die Epsilon-Kanten.
            System.out.println(word.charAt(0));
            for(State state : roots) {
                leafs.addAll(state.step(word.charAt(0)));
            }
            roots.clear(); // Achtung hier ist deep-copy sehr wichtig!
            roots.addAll(leafs);
            leafs.clear();
            word = word.substring(1);
        }
        //Sprich hier müssen wir jetzt noch so lange weitermachen Epsilon-Kanten weiter zu gehen bis sich nichts mehr ändert...
        /*
        Set<State> states = new HashSet<State>(roots);
        int size;
        do {
            size = states.size();
            for(State state : roots) {
                leafs.addAll(state.getNext(null));
            }
            roots.clear();
            roots.addAll(leafs);
            states.addAll(leafs);
            leafs.clear();
        } while(states.size() > size);
         */
        for(State state : roots) {
            if(state.getAcceptence() == State.Acceptance.ACCEPTING) {
                return true;
            }
        }
        return false;
    }
}
