package ab1.impl.gruppe32_hude_teichmann;
import ab1.FinalizedStateException;
import ab1.NFA;
import ab1.Transition;
import ab1.impl.gruppe32_hude_teichmann.graph.Edge;
import ab1.impl.gruppe32_hude_teichmann.graph.Vertex;
import ab1.impl.gruppe32_hude_teichmann.graph.impl.EdgeImpl;
import ab1.impl.gruppe32_hude_teichmann.graph.impl.GraphImpl;
import ab1.impl.gruppe32_hude_teichmann.graph.utils.SetOperations;

import java.util.*;

public class NFAImpl extends GraphImpl implements NFA {
    public NFAImpl(String initialStateName) {
        super(new State(initialStateName));
        this.automatonStates = AutomatonStates.EDITABLE;
    }
    private boolean contains(String name) {
        for(Vertex v : this.getVertices()) {
            if(v.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    private void toDFA() {
        //Startvorbereitungen
        setUnconnected(null);
        Map<State, Collection<Vertex>> newStates = new HashMap<>();

        //Verknüpfungen über Epsilon - Kanten
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

        //Start-Knoten bedenken.
        State startknoten = null;
        for(Vertex v : getStart()) {
            boolean exists = false;
            for(State s : newStates.keySet()) {
                if(SetOperations.contains(newStates.get(s), v)) {
                    startknoten = s;
                    exists = true;
                    break;
                }
            }
            if(!exists) {
                startknoten = new State(v.getName());
                newStates.put(startknoten, getStart());
                break; //Nach konstruktor ist hier sowieso immer nur einer drin!
            }
        }

        //Verknüpfungen über nicht Epsilon-Kanten
        Map<State, Collection<Vertex>> roots = new HashMap<>(newStates);
        Map<State, Collection<? extends Vertex>> leafs = new HashMap<>();
        Collection<Edge> newEdges = new HashSet<>();
        State trap = new State("Falle");
        for(Character c : getAlphabet(getVertices())) {
            if(c != null) {
                newEdges.add(new EdgeImpl(trap, c, trap));
            }
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
                }
            }
            leafs.clear();
        } while (size < newStates.keySet().size());

        //Akzeptanzen festlegen
        for(State superstate : newStates.keySet()) {
            for(Vertex v : newStates.get(superstate)) {
                if(((State) v).getAcceptence() == State.Acceptance.ACCEPTING) {
                    superstate.setAcceptance(State.Acceptance.ACCEPTING);
                }
            }
        }

        //Graph neu Aufbauen:
        State start = startknoten;
        setStart(new HashSet<>(){{add(start);}});
        for(Edge e : newEdges) {
            addEdge(e);
        }
        rename('q');
    }
    private void rename(Character c) {
        int i = 0;
        for(State s : states()) {
            if(!s.getName().equals("Falle")) {
                s.setName(c.toString() + i);
                i++;
            }
        }
    }
    private void renameAll(Character c) {
        int i = 0;
        for(State s : states()) {
            s.setName(c.toString() + i);
            i++;
        }
    }
    private NFAImpl getRenamedClone(Character c) {
        NFAImpl clone = this.clone();
        clone.renameAll(c);
        return clone;
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
        if(this.getAcceptingStates().isEmpty()){
            throw new FinalizedStateException("NFA has no edges");
        }
        NFAImpl thisClone = this.getRenamedClone('q');
        NFAImpl otherClone = ((NFAImpl) other).getRenamedClone('k');
        NFAImpl unionNFA = new NFAImpl("Start");
        unionNFA.addEdge(new EdgeImpl(unionNFA.getState(unionNFA.getInitialState()), null, otherClone.getState(otherClone.getInitialState())));
        unionNFA.addEdge(new EdgeImpl(unionNFA.getState(unionNFA.getInitialState()), null, thisClone.getState(thisClone.getInitialState())));
        unionNFA.rename('q');

        return unionNFA;
    }
    @Override
    public NFA intersection(NFA other) throws FinalizedStateException {
        if(this.getAcceptingStates().isEmpty()){
            throw new FinalizedStateException("NFA has no edges");
        }
        Collection<Character> thisAlphabet = this.getAlphabet(getVertices());
        Collection<Character> otherAlphabet = ((NFAImpl) other).getAlphabet(((NFAImpl) other).getVertices());
        thisAlphabet.retainAll(otherAlphabet);
        if(thisAlphabet.isEmpty()) {
            return new NFAImpl("Falle");
        } else {
            NFAImpl thisClone = getRenamedClone('q');
            NFAImpl thisComplement = (NFAImpl) thisClone.complement();
            thisComplement.renameAll('q');
            NFAImpl otherClone = ((NFAImpl) other).getRenamedClone('k');
            NFAImpl otherComplement = (NFAImpl) otherClone.complement();
            otherComplement.renameAll('k');
            NFAImpl union = (NFAImpl) thisComplement.union(otherComplement);
            NFAImpl unionComplement = (NFAImpl) union.complement();
            return unionComplement;
        }
    }
    @Override
    public NFA concatenation(NFA other) throws FinalizedStateException {
        if(this.getAcceptingStates().isEmpty()){
            throw new FinalizedStateException("NFA has no edges");
        }
        NFAImpl clone = this.clone();
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
        clone.rename('q');
        clone.finalizeAutomaton();
        return clone;
    }
    @Override
    public NFA kleeneStar() throws FinalizedStateException {
        if(this.getEdges(this.getVertices()).isEmpty()){
            throw new FinalizedStateException("NFA has no edges");
        }
        NFAImpl starNFA = new NFAImpl("q0");
        starNFA.getState("q0").setAcceptance(State.Acceptance.ACCEPTING);
        this.getState("START").setName("q1");
        starNFA.addEdge(new EdgeImpl(starNFA.getState(starNFA.getInitialState()), null, this.getState("q1")));
        for (String acceptingStateName : this.getAcceptingStates()) {
            this.addEdge(new EdgeImpl(this.getState(acceptingStateName), null, this.getState("q1")));
        }
        starNFA.rename('q');
        starNFA.finalizeAutomaton();
        return starNFA;
    }
    @Override
    public NFA plusOperator() throws FinalizedStateException {
        if(this.getAcceptingStates().isEmpty()){
            throw new FinalizedStateException("NFA has no edges");
        }
        NFAImpl plusNFA = this.clone();
        plusNFA.kleeneStar();
        plusNFA.rename('q');
        plusNFA.finalizeAutomaton();
        this.finalizeAutomaton();
        return plusNFA;
    }
    @Override
    public NFA complement() throws FinalizedStateException {
        if(this.getAcceptingStates().isEmpty()){
            throw new FinalizedStateException("NFA has no edges");
        }
        NFAImpl clone = this.clone();
        clone.toDFA();
        for (State s : clone.states()) {
            if (s.getAcceptence() == State.Acceptance.ACCEPTING) {
                s.setAcceptance(State.Acceptance.DENYING);
            } else if (s.getAcceptence() == State.Acceptance.DENYING) {
                s.setAcceptance(State.Acceptance.ACCEPTING);
            }
        }
        clone.finalizeAutomaton();
        clone.rename('q');
        return clone;
    }
    private enum AutomatonStates {
        EDITABLE,
        NOT_EDITABLE
    }
    private AutomatonStates automatonStates;
    @Override
    public boolean isFinalized() {
        return this.automatonStates == AutomatonStates.NOT_EDITABLE;
    }
    @Override
    public void finalizeAutomaton() {
        this.automatonStates = AutomatonStates.NOT_EDITABLE;
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
    public NFAImpl clone() {
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
