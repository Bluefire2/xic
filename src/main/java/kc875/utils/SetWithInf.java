package kc875.utils;

import com.google.common.collect.Sets;

import java.util.*;
import java.util.function.Predicate;

/**
 * A wrapper on Set, providing support for infinite sets.
 */
public class SetWithInf<E> implements Iterable<E> {

    private Set<E> includeSet;
    private Set<E> excludeSet;
    private boolean isInf;

    public SetWithInf(Set<E> includeSet) {
        this.includeSet = includeSet;
        this.excludeSet = new HashSet<>();
        isInf = false;
    }

    public SetWithInf(E... es) {
        this(new HashSet<>(Arrays.asList(es)));
    }

    public SetWithInf() {
        this(new HashSet<>());
    }

    public static <T> SetWithInf<T> infSet() {
        SetWithInf<T> set = new SetWithInf<>();
        set.isInf = true;
        return set;
    }

    /**
     * Returns a SetWithInf of T elements with includeSet as the wrapped set
     * in the returned wrapper.
     *
     * @param includeSet set to wrap around.
     */
    private static <T> SetWithInf<T> infSet(Set<T> includeSet) {
        SetWithInf<T> set = new SetWithInf<>(includeSet);
        set.isInf = true;
        return set;
    }

    public boolean isInf() {
        return this.isInf;
    }

    public Set<E> getIncludeSet() {
        return includeSet;
    }

    /**
     * If the set is infinite, then throws IllegalAccessError.
     */
    public int size() {
        if (this.isInf) throw new IllegalAccessError("inf set");
        else return this.includeSet.size();
    }

    public boolean isEmpty() {
        return !this.isInf && this.includeSet.isEmpty();
    }

    public boolean contains(E e) {
        // Either set is inf, return true if e not excluded
        // Or set is not inf, check if included
        return (this.isInf && !this.excludeSet.contains(e))
                || (!this.isInf && this.includeSet.contains(e));
    }

    public boolean containsAll(Collection<? extends E> c) {
        return c.stream().allMatch(this::contains);
    }

    public void add(E e) {
        this.includeSet.add(e);
        this.excludeSet.remove(e);
    }

    public void addAll(Collection<? extends E> c) {
        c.forEach(this::add);
    }

    public void addAll(E... es) {
        addAll(Arrays.asList(es));
    }

    public void remove(E e) {
        this.includeSet.remove(e);
        this.excludeSet.add(e);
    }

    /**
     * Removes elements from the set that satisfy the filter, i.e., filtered
     * elements are removed.
     */
    public void removeIf(Predicate<? super E> filter) {
        Iterator<E> iter = this.includeSet.iterator();
        while (iter.hasNext()) {
            E e = iter.next();
            if (filter.test(e)) {
                iter.remove();
                this.excludeSet.add(e);
            }
        }
    }

    public void removeAll(Collection<? extends E> c) {
        c.forEach(this::remove);
    }

    public void removeAll(E... es) {
        removeAll(Arrays.asList(es));
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private final Iterator<E> setIter = includeSet.iterator();

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
    private static <E> SetWithInf<E> newSetWithInf(
            boolean isInf, Set<E> incSet, Set<E> excSet
    ) {
        SetWithInf<E> set = new SetWithInf<>();
        set.isInf = isInf;
        set.includeSet = new HashSet<>(incSet);
        set.excludeSet = new HashSet<>(excSet);
        return set;
    }

    public SetWithInf<E> union(SetWithInf<E> other) {
        Set<E> incSet = Sets.union(this.includeSet, other.includeSet);
        Set<E> excSet = Sets.intersection(this.excludeSet, other.excludeSet);

        SetWithInf<E> set = newSetWithInf(
                this.isInf || other.isInf, incSet, new HashSet<>()
        );
        set.removeAll(excSet);
        return set;
    }

    public SetWithInf<E> intersect(SetWithInf<E> other) {
        Set<E> incSet = this.isInf || other.isInf
                ? Sets.union(this.includeSet, other.includeSet)
                : Sets.intersection(this.includeSet, other.includeSet);
        Set<E> excSet = Sets.union(this.excludeSet, other.excludeSet);

        SetWithInf<E> set = newSetWithInf(
                this.isInf && other.isInf, incSet, new HashSet<>()
        );
        set.removeAll(excSet);
        return set;
    }

    public SetWithInf<E> diff(SetWithInf<E> other) {
        // if inf set is subtracted, result is an empty set
        if (other.isInf) return new SetWithInf<>();

        // other is not inf
        // add all elements from this.include
        SetWithInf<E> set = newSetWithInf(
                this.isInf, this.includeSet, this.excludeSet
        );
        // remove all elements from set if contained in other.include
        set.removeIf(e -> other.includeSet.contains(e));
        // don't care about other.exclude since they might (or not) be in
        // this.include
        return set;
    }

    @Override
    public String toString() {
        if (this.isInf)
            return "[*] + "
                    + this.includeSet.toString() + " - "
                    + this.excludeSet.toString();
        return this.includeSet.toString() + " - " + this.excludeSet.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SetWithInf<?>)) return false;
        SetWithInf<?> other = (SetWithInf<?>) o;
        return (this.isInf && other.isInf)
                || (!this.isInf && !other.isInf && this.includeSet.equals(other.includeSet));
    }

    public List<E> toList() {
        List<E> lst = new ArrayList<>();
        for (E e : this) {
            lst.add(e);
        }
        return lst;
    }
}
