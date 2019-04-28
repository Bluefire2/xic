package kc875.utils;

import java.util.*;

public class QueueSet<T> implements Iterable {

    private Queue<T> queue;
    private Set<T> set;

    public QueueSet() {
        this.queue = new LinkedList<>();
        this.set = new HashSet<>();
    }

    public void add(T t) {
        queue.add(t);
        set.add(t);
    }

    public void addAll(Collection<T> c) {
        queue.addAll(c);
        set.addAll(c);
    }

    public void remove(T t) {
        queue.remove(t);
        set.remove(t);
    }

    public Iterator iterator() {
        return new QueueSetIterator(this);
    }

    public Queue<T> getQueue() {
        return queue;
    }

    public Set<T> getSet() {
        return set;
    }
}

class QueueSetIterator implements Iterator {

        Queue queue;

        QueueSetIterator(QueueSet qs) {
            this.queue = qs.getQueue();
        }

        public boolean hasNext() {
            return (queue.peek() != null);
        }

        public Object next() {
            return queue.poll();
        }

        public void remove() {
        }
}

