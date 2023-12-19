package ab1;

public class FinalizedStateException extends RuntimeException {

    public FinalizedStateException(){
        super("Can't do operation if state is finalized.");
    }

    public FinalizedStateException(String errorMessage){
        super(errorMessage);
    }
}
