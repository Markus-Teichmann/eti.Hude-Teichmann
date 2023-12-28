package ab1.tests;

import ab1.NFAFactory;
import ab1.NFAProvider;
import ab1.Transition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleTests {

    private final NFAFactory factory = NFAProvider.provideFactory();

    @Test
    public void acceptingStates1Test() {
        var instance = factory.buildNFA("START");
        instance.addAcceptingState("ACCEPT");

        assertEquals(1, instance.getAcceptingStates().size());
        assertTrue(instance.getAcceptingStates().contains("ACCEPT"));
    }

    @Test
    public void acceptingStates2Test() {
        var instance = factory.buildNFA("START");
        instance.addAcceptingState("ACCEPT");
        instance.addAcceptingState("ACCEPT");
        instance.addAcceptingState("ACCEPT");

        assertEquals(1, instance.getAcceptingStates().size());
        assertTrue(instance.getAcceptingStates().contains("ACCEPT"));
    }

    @Test
    public void acceptingStates3Test() {
        var instance = factory.buildNFA("START");
        instance.addAcceptingState("ACCEPT");
        instance.addAcceptingState("OTHER_ACCEPT");

        assertEquals(2, instance.getAcceptingStates().size());
        assertTrue(instance.getAcceptingStates().contains("ACCEPT"));
        assertTrue(instance.getAcceptingStates().contains("OTHER_ACCEPT"));
    }

    @Test
    public void acceptingStates4Test() {
        var instance = factory.buildNFA("START");
        instance.addTransition(
                Transition.builder()
                        .readSymbol('a')
                        .fromState("START")
                        .toState("ACCEPT")
                        .build()
        );
        instance.addAcceptingState("ACCEPT");

        assertEquals(1, instance.getAcceptingStates().size());
        assertTrue(instance.getAcceptingStates().contains("ACCEPT"));
    }

    @Test
    public void transitions1Test() {
        var instance = factory.buildNFA("START");
        instance.addTransition(
                Transition.builder()
                        .readSymbol('a')
                        .fromState("START")
                        .toState("ACCEPT")
                        .build()
        );

        assertEquals(2, instance.getStates().size());
    }

    @Test
    public void initialStateTest() {
        var instance = factory.buildNFA("START");

        assertEquals("START", instance.getInitialState());
    }

    @Test
    public void myFiniteTest() {
        var instance = factory.buildNFA("START");
        instance.addTransition(
                Transition.builder()
                        .readSymbol('a')
                        .fromState("START")
                        .toState("S1")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol('a')
                        .fromState("S1")
                        .toState("S2")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol(null)
                        .fromState("S2")
                        .toState("S3")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol(null)
                        .fromState("S3")
                        .toState("S2")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol(null)
                        .fromState("S3")
                        .toState("S4")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol(null)
                        .fromState("S5")
                        .toState("S6")
                        .build()
        );
        instance.addAcceptingState("S2");
        assertFalse(instance.isFinite());
    }
    @Test
    public void myToDFATest() {
        var instance = factory.buildNFA("A");
        instance.addTransition(
                Transition.builder()
                        .readSymbol('a')
                        .fromState("A")
                        .toState("B")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol('b')
                        .fromState("A")
                        .toState("C")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol('a')
                        .fromState("A")
                        .toState("A")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol('a')
                        .fromState("B")
                        .toState("A")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol('b')
                        .fromState("B")
                        .toState("B")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol('b')
                        .fromState("C")
                        .toState("A")
                        .build()
        );
        instance.addTransition(
                Transition.builder()
                        .readSymbol('b')
                        .fromState("C")
                        .toState("B")
                        .build()
        );
        instance.addAcceptingState("C");
        //System.out.println(instance);
        instance.complement();
    }
}
