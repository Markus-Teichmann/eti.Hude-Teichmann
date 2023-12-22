package ab1.impl.GRUPPE;
import java.util.*;
import java.util.function.Function;

public class State {
    private String name;
    public enum Acceptance {
        ACCEPTING,
        DENYING
    }
    private Acceptance acceptance;
    private Map<Character,Set<State>> next;

    public State(String name) {
        this.name = name;
        this.acceptance = Acceptance.DENYING;
        this.next = new HashMap<>();
    }
    public void setAcceptance(Acceptance acceptance) {
        this.acceptance = acceptance;
    }
    public String getName() {
        return this.name;
    }
    public Acceptance getAcceptence() {
        return this.acceptance;
    }
    public Set<State> getNext(Character c) {
        if(this.next.containsKey(c)) {
            return this.next.get(c);
        }
        return new HashSet<State>();
    }
    public Set<State> getLeafs(Set<State> set, Character c, Function<State, Set<State>> f) {
        Set<State> roots = new HashSet<>(set);
        Set<State> leafs = new HashSet<>();
        int size;
        do {
            size = set.size();
            for(State s : roots) {
                leafs.addAll(f.apply(s));
            }
            roots.clear();
            roots.addAll(leafs);
            if(c != null) {
                set.clear();
            }
            set.addAll(leafs);
            leafs.clear();
        } while(set.size() > size);
        return set;
    }
    public Set<State> step(Character c) {
        Set<State> set = new HashSet<State>();
        set.add(this);
        set = this.getLeafs(set, null, (State s) -> s.getNext(null));
        set = this.getLeafs(set, c, (State s) -> s.getNext(c));
        set = this.getLeafs(set, null, (State s) -> s.getNext(null));
        return set;
    }
    public Set<State> getNext() {
        Set<State> set = new HashSet<State>();
        if(!this.next.isEmpty()){
            for(Character c : this.next.keySet()) {
                set.addAll(this.next.get(c));
            }
        }
        return set;
    }
    public Set<State> getAllPossiblyFollowingStates() {
        Set<State> states = new HashSet<State>();
        states.add(this);
        states = this.getLeafs(states, null, (State s) -> s.getNext());
        return states;
    }
    public void addTransition(Character c, State toState) {
        if(this.next.containsKey(c)) {
            this.next.get(c).add(toState);
        } else {
            Set<State> states = new HashSet<State>();
            states.add(toState);
            this.next.put(c, states);
        }
    }
}