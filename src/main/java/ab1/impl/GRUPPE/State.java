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
    /*
        Diese Methode ermöglicht es von einem oder mehreren Knoten über alle sich ergebenden Folgeknoten zu iterieren.

        @param set enthält den einen oder die mehreren Ausgangsknoten.
        @param c beschreibt den Buchstaben der Übergangsfunktion. null ist dabei die Epsilon-Kante.
                Sollte c nicht null sein, so wird set.clear() ausgeführt, da nicht Epsilon-Kanten nicht optional sind.
                Sie werden immer gegangen und wir verlassen den Ausgangsknoten, wenn wir dieser Kante folgen.
                Somit kann der Ausgangsknoten nicht teil des nächsten Iterationsschritts sein und muss entfernt werden.
        @param f ist eine Funktion die einen Parameter vom Typ State erwartet und ein Set<State> liefert.
                Diese Methode f wird auf alle Ausgangsknoten angewendet. Die Idee ist, dass f die benachbarten Knoten
                zurückgibt. Entweder abhängig von der Übergangskante oder eben auch beliebig.
     */
    public Set<State> getIterable(Set<State> set, Character c, Function<State, Set<State>> f) {
        Set<State> roots = new HashSet<>(set);
        Set<State> leafs = new HashSet<>();
        int size;
        do {
            size = set.size();
            for(State s : roots) {
                if(null != f.apply(s)) {
                    leafs.addAll(f.apply(s));
                }
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
    public Set<State> getNext() {
        Set<State> set = new HashSet<State>();
        if(!this.next.isEmpty()){
            for(Character c : this.next.keySet()) {
                set.addAll(this.next.get(c));
            }
        }
        return set;
    }
    public Set<State> getNext(Character c) {
        Set<State> set = new HashSet<State>();
        set.add(this);
        set = this.getIterable(set, null, (State s) -> s.next.get(null));
        set = this.getIterable(set, c, (State s) -> s.next.get(c));
        set = this.getIterable(set, null, (State s) -> s.next.get(null));
        return set;
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