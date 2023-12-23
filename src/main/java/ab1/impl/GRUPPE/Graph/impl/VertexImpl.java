package ab1.impl.GRUPPE.Graph.impl;

import ab1.impl.GRUPPE.Graph.Vertex;

import java.util.*;

public class VertexImpl implements Vertex {
    //Später nennen wir diese Klasse State, denn darum gehts ja eigentlich...
    //Und das ist ja nicht irgendeine implementierung, sondern hier geht es ja
    //um die Zustände, die können dann auch mehr als ein gewöhnliches Element.
    private Map<Character, Set<Vertex>> prev;
    private Map<Character, Set<Vertex>> next;
    private String name;

    public VertexImpl(String name) {
        this.name = name;
        this.prev = null;
        this.next = null;
    }
    @Override
    public Collection<Vertex> getNext() {
        if(this.next != null) {
            Set<Vertex> next = new HashSet<Vertex>();
            for (Character c : this.next.keySet()) {
                next.addAll(this.next.get(c));
            }
            return next;
        }
        return null;
    }

    @Override
    public Collection<Vertex> getNext(Character c) {
        if(next != null && next.containsKey(c)) {
            return next.get(c);
        }
        return null;
    }
    @Override
    public Collection<Vertex> getPrev() {
        if(this.prev != null) {
            Set<Vertex> prev = new HashSet<Vertex>();
            for (Character c : this.prev.keySet()) {
                prev.addAll(this.prev.get(c));
            }
            return prev;
        }
        return null;
    }

    @Override
    public Collection<Vertex> getPrev(Character c) {
        if(prev != null && prev.containsKey(c)) {
            return prev.get(c);
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void addNext(Character c, Vertex e) {
        if(next == null) {
            next = new HashMap<Character, Set<Vertex>>();
        }
        if(!next.containsKey(c)) {
            Set<Vertex> set = new HashSet<Vertex>();
            set.add(e);
            next.put(c, set);
            e.addPrev(c, this);
        } else if(!next.get(c).contains(e)){
            next.get(c).add(e);
            e.addPrev(c, this);
        }
    }

    @Override
    public void addPrev(Character c, Vertex e) {
        if(prev == null) {
            prev = new HashMap<Character, Set<Vertex>>();
        }
        if(!prev.containsKey(c)) {
            Set<Vertex> set = new HashSet<Vertex>();
            set.add(e);
            prev.put(c, set);
            e.addNext(c, this);
        } else if(!prev.get(c).contains(e)){
            prev.get(c).add(e);
            e.addNext(c, this);
        }
    }

}
