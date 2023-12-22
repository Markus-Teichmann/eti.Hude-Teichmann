package ab1.impl.GRUPPE;

public class NoSuchStateException extends Exception {
    public NoSuchStateException(String name) {
        super("There is no State named: " + name);
    }
}
