package ab1;

import ab1.impl.gruppe32_hude_teichmann.NFAFactoryImpl;

public class NFAProvider {
    public static NFAFactory provideFactory() {
        return new NFAFactoryImpl();
    }
}
