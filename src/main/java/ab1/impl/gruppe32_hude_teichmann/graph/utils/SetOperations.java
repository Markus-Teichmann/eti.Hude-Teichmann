package ab1.impl.gruppe32_hude_teichmann.graph.utils;

import java.util.Collection;

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
