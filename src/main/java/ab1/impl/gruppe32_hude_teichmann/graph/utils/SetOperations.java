package ab1.impl.gruppe32_hude_teichmann.graph.utils;

import java.util.Collection;
/*
    Diese Klasse wurde eigentlich nur eingeführt, da sehr lange davon ausgegangen wurde, dass
    die Namen der Knoten in den Graphen eindeutig sind. Nach der Definition von NFA's sollte das
    auch so sein, da es sich hier immer um Knotenmengen handelt. Folglich sollte es in der equals()
    Methode nur darauf Rücksicht genommen. Die hashCode() Methode wurde aufgrund derselben Argumentation
    überschrieben einfach 0 zurückzugeben.

    Da die Testfälle diese Theorie nicht immer respektieren war es nun doch notwendig unterschiedliche Hash
    Values zu haben.

    In der Implementierung wurde zu diesem Zeitpunkt häufig die contains() Methode aus Collections<> verwendet.
    Da diese Methode auch auf die hashCode() Methode zurück greift und diese ja jetzt verändert werden musste
    entstand die Notwendigkeit entweder für ein größeres Refactoring vieler bis daher geschriebenen Methode aus GraphImpl

    Um dies zu umgehen und auch eine mögliche Code Duplication zu vermeiden wurde diese Klasse geschrieben.

    Mit dieser Klasse wurde ein großes Refactoring umgangen. Die Lösung ist mehr ein Hotfix, als eine dauerhafte Lösung.
    Da sich die Notwendigkeit aber mehr oder weniger aus den zur Theorie widersprüchlichen Tests ergibt, wurde das hier
    nicht mehr angepasst.
 */
public class SetOperations {
    public static <T> void add(Collection<T> c, T value) {
        if(c != null) {
            if (!contains(c, value)) {
                c.add(value);
            }
        }
    }
    public static <T> void addAll(Collection<T> c, Collection<T> values) {
        if(c != null && values != null) {
            for (T value : values) {
                add(c, value);
            }
        }
    }
    public static <T> boolean contains(Collection<? extends T> c, T value) {
        if(c != null) {
            for (T collectionValue : c) {
                if(collectionValue == null && value == null) {
                    return true;
                } else if(collectionValue != null && value != null) {
                    if (collectionValue.equals(value)) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }
    public static <T> boolean containsAll(Collection<? extends T> c, Collection<? extends T> values) {
        if(c != null && values != null) {
            for (T value : values) {
                if (!contains(c, value)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static <T> boolean equals(Collection<T> c, Collection<? extends T> values) {
        if(c != null && values != null) {
            if (containsAll(c, values) && containsAll(values, c)) {
                return true;
            }
            return false;
        } else if(c == null && values == null) {
            return true;
        } else {
            return false;
        }
    }
}
