package kc875.utils;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A wrapper on Set, providing support for infinite sets.
 */
public class SetWithInf<E> implements Iterable<E> {

    private Set<E> set;
    private boolean isInf;

    public SetWithInf(Set<E> set) {
        this.set = new HashSet<>(set);
        isInf = false;
    }

    public SetWithInf() {
        this(new HashSet<>());
    }

    public static <T> SetWithInf<T> infSet() {
        SetWithInf<T> set = new SetWithInf<>();
        set.isInf = true;
        return set;
    }

    public boolean isInf() {
        return this.isInf;
    }

    public Set<E> getSet() {
        return set;
    }

    /**
     * If the set is infinite, then throws IllegalAccessError.
     */
    public int size() {
        if (this.isInf) throw new IllegalAccessError("inf set");
        else return this.set.size();
    }

    public boolean isEmpty() {
        return !this.isInf && this.set.isEmpty();
    }

    public boolean contains(E e) {
        // Either set is inf, return true
        // Or set is not inf, check if included
        return this.isInf || this.set.contains(e);
    }

    public void add(E e) {
        if (this.isInf)
            throw new IllegalAccessError("can't add to an inf set");
        this.set.add(e);
    }

    public void addAll(Collection<? extends E> c) {
        c.forEach(this::add);
    }

    public void remove(E e) {
        if (this.isInf)
            throw new IllegalAccessError("can't remove from inf set");
        this.set.remove(e);
    }

    public void removeAll(Collection<? extends E> c) {
        c.forEach(this::remove);
    }

    /**
     * Removes elements from the set that satisfy the filter, i.e., filtered
     * elements are removed.
     */
    public void removeIf(Predicate<? super E> filter) {
        if (this.isInf)
            throw new IllegalAccessError("can't remove from inf set");
        this.set.removeIf(filter);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private final Iterator<E> setIter = set.iterator();

            @Override
            public boolean hasNext() {
                return !isInf && setIter.hasNext();
            }

            @Override
            public E next() {
                return setIter.next();
            }
        };
    }

    // Set operations with another set

    /**
     * Preconditions:
     * - this and other should not be infinite.
     */
    public SetWithInf<E> union(SetWithInf<E> other) {
        return new SetWithInf<>(Sets.union(this.set, other.set));
    }

    @Override
    public String toString() {
        if (this.isInf)
            return "[*] + " + this.set.toString();
        return this.set.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SetWithInf<?>)) return false;
        SetWithInf<?> other = (SetWithInf<?>) o;
        return (this.isInf && other.isInf)
                || (!this.isInf && !other.isInf && this.set.equals(other.set));
    }
}
