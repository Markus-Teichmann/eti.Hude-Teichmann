package ab1.impl.GRUPPE;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Map;

public class State {
    private String name;
    public enum Acceptance {
        ACCEPTING,
        DENYING
    }
    private Acceptance acceptance;
    public State(String name) {
        this.name = name;
        this.acceptance = Acceptance.DENYING;
    }
    public State(String name, Acceptance acceptance) {
        this.name = name;
        this.acceptance = Acceptance.ACCEPTING;
    }
    public void setAcceptance(Acceptance acceptance){
        this.acceptance = acceptance;
    }
    public String getName() {
        return this.name;
    }
    public Acceptance getAcceptence() {
        return this.acceptance;
    }
=======
public class State {
>>>>>>> 3f508f93a4ba0df1082ee0f83607a7a317144ba6
}
