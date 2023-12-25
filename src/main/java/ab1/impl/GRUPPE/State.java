package ab1.impl.GRUPPE;
import ab1.impl.GRUPPE.Graph.impl.VertexImpl;

public class State extends VertexImpl {
    public enum Acceptance {
        ACCEPTING,
        DENYING
    }
    private Acceptance acceptance;
    public State(String name) {
        super(name);
        this.acceptance = Acceptance.DENYING;
    }
    public void setAcceptance(Acceptance acceptance) {
        this.acceptance = acceptance;
    }

    public Acceptance getAcceptence() {
        return this.acceptance;
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof State) {
            if(!(((State) o).getAcceptence() == this.getAcceptence())) {
                return false;
            }
        }
        return super.equals(o);
    }
    @Override
    public String toString() {
        return super.toString() + ": " + acceptance;
    }
}