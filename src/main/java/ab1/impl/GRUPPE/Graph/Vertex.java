package ab1.impl.GRUPPE.Graph;

import java.util.Collection;

public interface Vertex {
    //public Collection<Vertex> getNext();
    //public Collection<Vertex> getNext(Character c);
    public Collection<Vertex> getNext(Collection<Character> transitions);
    //public Collection<Vertex> getPrev();
    //public Collection<Vertex> getPrev(Character c);
    public Collection<Vertex> getPrev(Collection<Character> transitions);
    public String getName();
    public Collection<Character> getOutgoingEdges();
    public Collection<Character> getIncommingEdges();
    public void setName(String name);
    public void addNext(Character c, Vertex v);
    public void addPrev(Character c, Vertex v);
    public void removeNext(Character c, Vertex v);
    public void removePrev(Character c, Vertex v);
    public Vertex clone();

}
