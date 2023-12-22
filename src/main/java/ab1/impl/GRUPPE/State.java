package ab1.impl.GRUPPE;
import java.util.*;
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
    public Set<State> step(Character c) {
        Set<State> set = new HashSet<State>();
        set.add(this);
        Set<State> roots = new HashSet<State>(set);
        Set<State> leafs = new HashSet<State>();
        int size;
        do {
            size = set.size();
            for(State s : roots) {
                leafs.addAll(s.getNext(null));
            }
            roots.clear();
            roots.addAll(leafs);
            set.addAll(leafs);
            leafs.clear();
        } while(set.size() > size);
        roots.clear();
        roots.addAll(set);
        for(State s : roots) {
            leafs.addAll(s.getNext(c));
        }
        roots.clear();
        roots.addAll(leafs);
        set.clear();
        set.addAll(leafs);
        leafs.clear();
        do {
            size = set.size();
            for(State s : roots) {
                leafs.addAll(s.getNext(null));
            }
            roots.clear();
            roots.addAll(leafs);
            set.addAll(leafs);
            leafs.clear();
        } while(set.size() > size);
        /*
        System.out.println("Anzahl verschiedener Übergänge: " + this.next.size());
        if(!this.next.isEmpty()) {
            System.out.println("Gesuchter Buchstabe: " + c);
            if (this.next.containsKey(c)) {
                System.out.println("Anzahl der Knoten mit Buchstaben " + c + ": " + this.next.get(c).size());
                set = this.next.get(c);
            }
            if (this.next.containsKey(null)) {
                System.out.println("Anzahl der Knoten über Epsilon: " + this.next.get(null).size());
                set.addAll(this.next.get(null));
            }
        }
         */
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
        Set<State> leafs = new HashSet<State>();
        Set<State> roots = new HashSet<State>(states); //Deep-Copy hier extrem wichtig.
        int size;
        do {
            size = states.size();
            for(State s : roots) {
                leafs.addAll(s.getNext());
            }
            roots.clear(); //Deep-Copy hier extrem wichtig.
            roots.addAll(leafs);
            states.addAll(leafs);
            leafs.clear();
        } while(states.size() > size);
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