package kc875.utils;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A wrapper on Set, providing support for infinite sets.
 */
public class SetWithInf<E> {
    // Class invariants:
    // - An element s cannot be in both includeSet and excludeSet at the same
    //   time.
    // - A set can be infinite and still have non-empty excludeSet. If a set
    //   is infinite, then we don't care about the includeSet.
    private Set<E> set;
    private boolean isInf;

    public SetWithInf(Set<E> set) {
        this.set = set;
        isInf = false;
    }

    public SetWithInf() {
        this(new HashSet<>());
    }

    public static<T> SetWithInf<T> infSet() {
        SetWithInf<T> set = new SetWithInf<>();
        set.isInf = true;
        return set;
    }

    /**
     * Returns a SetWithInf of T elements with includeSet as the wrapped set
     * in the returned wrapper.
     * @param includeSet set to wrap around.
     */
    private static<T> SetWithInf<T> infSet(Set<T> includeSet) {
        SetWithInf<T> set = new SetWithInf<>(includeSet);
        set.isInf = true;
        return set;
    }

    public boolean isEmpty() {
        return !this.isInf && this.set.isEmpty();
    }

    public boolean contains(E e) {
        return this.isInf || this.set.contains(e);
    }

    public boolean containsAll(Collection<? extends E> c) {
        return this.isInf || this.set.containsAll(c);
    }

    public boolean add(E e) {
        return this.set.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        return this.set.addAll(c);
    }

    public SetWithInf<E> union(SetWithInf<E> other) {
        Set<E> unionSet = Sets.union(this.set, other.set).immutableCopy();
        if (this.isInf || other.isInf) {
            // return an inf set but with the wrapped sets combined
            return infSet(unionSet);
        } else {
            // both are non-inf sets
            return new SetWithInf<>(unionSet);
        }
    }

    public SetWithInf<E> intersect(SetWithInf<E> other) {
        SetWithInf<E> fst, snd;
        if (this.set.size() < other.set.size()) {
            fst = this;
            snd = other;
        } else {
            // switch order for faster intersection operation; see doc
            // https://google.github.io/guava/releases/27.1-jre/api/docs/
            fst = other;
            snd = this;
        }

        Set<E> interSet = Sets.intersection(fst.set, snd.set).immutableCopy();
        if (fst.isInf && snd.isInf) {
            // both are inf, return an inf set
            return infSet(interSet);
        } else if (fst.isInf) {
            // fst is inf, but snd isn't; return snd
            return snd;
        } else if (snd.isInf) {
            // snd is inf, but fst isn't; return fst
            return fst;
        } else {
            // both are non-inf, return the intersection
            return new SetWithInf<>(interSet);
        }
    }

    // TODO
    public SetWithInf<E> diff(SetWithInf<E> other) {
        Set<E> diffSet = Sets.difference(this.set, other.set).immutableCopy();
        return null;
    }
}
