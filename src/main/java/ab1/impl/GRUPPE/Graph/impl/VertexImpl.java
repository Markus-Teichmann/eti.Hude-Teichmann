package ab1.impl.GRUPPE.Graph.impl;

import ab1.impl.GRUPPE.Graph.Vertex;

import java.util.*;

public class VertexImpl implements Vertex {
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
    public Collection<Character> getOutgoingEdges() {
        if(next != null) {
            return next.keySet();
        }
        return null;
    }

    @Override
    public Collection<Character> getIncommingEdges() {
        if(prev != null){
            return prev.keySet();
        }
        return null;
    }

    @Override
    public void addNext(Character c, Vertex v) {
        if(next == null) {
            next = new HashMap<Character, Set<Vertex>>();
        }
        if(!next.containsKey(c)) {
            Set<Vertex> set = new HashSet<Vertex>();
            set.add(v);
            next.put(c, set);
            //v.addPrev(c, this);
        } else if(!next.get(c).contains(v)){
            next.get(c).add(v);
            //v.addPrev(c, this);
        }
    }

    @Override
    public void addPrev(Character c, Vertex v) {
        if(prev == null) {
            prev = new HashMap<Character, Set<Vertex>>();
        }
        if(!prev.containsKey(c)) {
            Set<Vertex> set = new HashSet<Vertex>();
            set.add(v);
            prev.put(c, set);
            //v.addNext(c, this);
        } else if(!prev.get(c).contains(v)){
            prev.get(c).add(v);
            //v.addNext(c, this);
        }
    }

    @Override
    public void removeNext(Character c, Vertex v) {
        if(next.containsKey(c)) {
            if(!next.get(c).isEmpty()) {
                next.get(c).remove(v);
            }
            if(next.get(c).isEmpty()){
                next.remove(c,next.get(c));
            }
        }
    }

    @Override
    public void removePrev(Character c, Vertex v) {
        if(prev.containsKey(c)) {
            if(!prev.get(c).isEmpty()) {
                prev.get(c).remove(v);
            }
            if(prev.get(c).isEmpty()){
                prev.remove(c,prev.get(c));
            }
        }
    }

}
