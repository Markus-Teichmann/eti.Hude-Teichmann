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
    public Collection<Vertex> getNext(Collection<Character> transitions) {
        if(transitions == null) {
            if(this.next != null) {
                Set<Vertex> next = new HashSet<>();
                for (Character c : this.next.keySet()) {
                    next.addAll(this.next.get(c));
                }
                return next;
            }
            return null;
        } else if (transitions.isEmpty()) {
            return null;
        } else {
            Collection<Vertex> next = new HashSet<>();
            for (Character c : transitions) {
                if(this.next != null && this.next.containsKey(c)) {
                    next.addAll(this.next.get(c));
                }
            }
            return next;
        }
    }
    @Override
    public Collection<Vertex> getPrev(Collection<Character> transitions) {
        if(transitions == null) {
            if(this.prev != null) {
                Set<Vertex> prev = new HashSet<>();
                for (Character c : this.prev.keySet()) {
                    prev.addAll(this.prev.get(c));
                }
                return prev;
            }
            return null;
        } else if (transitions.isEmpty()) {
            return null;
        } else {
            Collection<Vertex> prev = new HashSet<>();
            for (Character c : transitions) {
                if(this.prev != null && this.prev.containsKey(c)) {
                    prev.addAll(this.prev.get(c));
                }
            }
            return prev;
        }
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
    public void setName(String name) {
        this.name = name;
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
        } else if(!next.get(c).contains(v)){
            next.get(c).add(v);
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
        } else if(!prev.get(c).contains(v)){
            prev.get(c).add(v);
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
    @Override
    public boolean equals(Object o) {
        if(o instanceof Vertex) {
            return ((Vertex) o).getName().equals(name);
        } else {
            return super.equals(o);
        }
    }
    @Override
    public String toString() {
        return name + " " + super.toString();
    }
    @Override
    public Vertex clone() {
        return new VertexImpl(name);
    }
}