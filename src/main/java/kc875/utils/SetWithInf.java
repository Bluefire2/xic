package kc875.utils;

import com.google.common.collect.Sets;

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
    private boolean isInf = false;

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

    // TODO
    public SetWithInf<E> intersect(SetWithInf<E> other) {
        return null;
    }

    // TODO
    public SetWithInf<E> diff(SetWithInf<E> other) {
        return null;
    }
}
