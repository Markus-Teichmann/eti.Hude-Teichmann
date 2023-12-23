package ab1.impl.GRUPPE.Graph;

import java.util.Collection;

public interface Vertex {
    public Collection<Vertex> getNext();
    public Collection<Vertex> getNext(Character c);
    public Collection<Vertex> getPrev();
    public Collection<Vertex> getPrev(Character c);
    public String getName();
    public void addNext(Character c, Vertex e);
    public void addPrev(Character c, Vertex e);
}
