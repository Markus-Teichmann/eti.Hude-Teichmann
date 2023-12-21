package ab1.impl.GRUPPE;

import ab1.FinalizedStateException;
import ab1.NFA;
import ab1.Transition;

import java.util.*;

public class NFAImpl implements NFA {
    private State initialState;
    private Map<String,State> states;
    private List<Transition> transitions;
    public NFAImpl(String initialStateName) {
        this.initialState = new State(initialStateName);
        this.states = new HashMap<String,State>();
        this.transitions = new ArrayList<Transition>();
    }
    @Override
    public Set<String> getStates() {
        return this.states.keySet();
    }

    @Override
    public Set<String> getAcceptingStates() {
        Set<String> states = new HashSet<String>();
        for(State state : this.states.values()) {
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

        if(!(this.transitions.contains(transition))) {
            this.transitions.add(transition);
            if(!(this.states.containsKey(transition.fromState()))) {
                this.states.put(transition.fromState(), new State(transition.fromState()));
            }
            if(!(this.states.containsKey(transition.toState()))) {
                this.states.put(transition.toState(), new State(transition.toState()));
            }
        }
    }
    @Override
    public void addAcceptingState(String state) throws FinalizedStateException {

        if(isFinalized()) {
            throw new FinalizedStateException("Can't add accepting state to finalized automata");
        }

        if(this.states.containsKey(state)) {
            this.states.get(state).setAcceptance(State.Acceptance.ACCEPTING);
        } else {
            this.states.put(state,new State(state, State.Acceptance.ACCEPTING));
        }
    }

    @Override
    public NFA union(NFA other) throws FinalizedStateException {

        // neuen NFA anlegen der später die Vereinigung enthält
        NFA unionNFA = new NFAImpl("initialState");

        // neuen Epsilonübergang anlegen zu den zu vereinigenden NFAs, als Epsilon wurde 'e' verwendet
        Transition epsilonTransitionToNFAOne = new Transition(unionNFA.getInitialState(), 'e', this.getInitialState());
        Transition epsilonTransitionToNFATwo = new Transition(unionNFA.getInitialState(), 'e', other.getInitialState());

        // Epsilonübergang zu unionNFA hinzufügen
        unionNFA.addTransition(epsilonTransitionToNFAOne);
        unionNFA.addTransition(epsilonTransitionToNFATwo);

        // Hinzufügen der States von den beiden NFAs zum unionNFA
        for (Transition transitions : this.transitions){
            unionNFA.addTransition(transitions);
        }
        Collection<String> otherStatesCollection = other.getStates();
        Set<String> otherStates = new HashSet<>(otherStatesCollection);






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
        List<String> currentlyReachedStates = new ArrayList<>();
        currentlyReachedStates.add(initialState.getName());

        while (word.length() > 0) {
            // Liste für die nächsten erreichbaren Zustände
            List<String> newStates = new ArrayList<>();

            for (String state : currentlyReachedStates) {
                // Erreibachre Zustände für das aktuelle Zeichen hinzufügen
                newStates.addAll(getReachableStates(state, word.charAt(0)));
            }

            currentlyReachedStates = newStates;
            // erste Zeichen des Worts entfernen
            word = word.substring(1);
        }

        for (String state : currentlyReachedStates) {
            if (this.states.get(state).getAcceptence() == State.Acceptance.ACCEPTING) {
                return true;
            }
        }

        return false;
    }



    /*
    Diese Methode gibt alle von einem Startzustand über eine spezifische Kante erreichbare Knoten zurück.
    Hierbei werden auch die Epsilon-Kanten berücksichtigt. Somit ist der Abstand auch nicht mehr nur 1,
    denn es können ja auch mehrere Epsilon-Kanten aufeinander folgen.
     */
    private Set<String> getReachableStates(String state, Character c) {
        Set<String> reachableStates = getWithOneCharacterTransitionReachableStates(state, c);
        Set<String> byEpsilonReachableStates = new HashSet<String>();
        int currentsize;
        do { // Do Schleife ist hier wichtig, da wir mindestens einmal durchlaufen müssen
            currentsize = reachableStates.size();
            byEpsilonReachableStates.clear();
            for(String reachedState : reachableStates) {
                byEpsilonReachableStates.addAll(getWithOneCharacterTransitionReachableStates(reachedState, 'e'));
            }
            reachableStates.addAll(byEpsilonReachableStates);
        } while(currentsize < reachableStates.size());
        return reachableStates;
    }
    /*
    Diese Methode gibt alle von einem Startknoten aus über eine spezifische Kante erreichbaren Knoten zurück.
     */
    private Set<String> getWithOneCharacterTransitionReachableStates(String state, Character c) {
        Set<Transition> stateRelatedTransitions = transitionsFrom(state);
        Set<Transition> characterRelatedTransitions = transitionsWith(c);
        Set<Transition> relevantTransitions = new HashSet<Transition>(stateRelatedTransitions);
        relevantTransitions.retainAll(characterRelatedTransitions); // Mengenoperation Geschnitten.
        Set<String> reachedStates = new HashSet<String>();
        for(Transition t : relevantTransitions) {
            reachedStates.add(t.toState());
        }
        return reachedStates;
    }
    /*
    Diese Methode gibt alle Übergänge zu einem bestimmten Startknoten zurück.
     */
    private Set<Transition> transitionsFrom(String state) {
        Set<Transition> transitions = new HashSet<Transition>();
        for(Transition transition : this.transitions) {
            if(transition.fromState().equals(state)) {
                transitions.add(transition);
            }
        }
        return transitions;
    }
    /*
    Diese Methode gibt alle Übergänge mit einem bestimmten Übergang zurück.
     */
    private Set<Transition> transitionsWith(Character c) {
        Set<Transition> transitions = new HashSet<Transition>();
        for (Transition transition : this.transitions) {
            if (transition.readSymbol() == c) {
                transitions.add(transition);
            }
        }
        return transitions;
    }
}
