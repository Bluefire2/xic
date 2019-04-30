package kc875.utils;

/**
 * A wrapper that represents any particular instantiation of T or any
 * instantiation (like "a" or {set of all strings}).
 */
// TODO: problem: "any" could be anything in the world; no way to know what
//  type that "any" has ==> so no way to differentiate any of int and any of
//  string. Could do this, but too much work:
//    https://stackoverflow.com/questions/9931611/determine-if-generic-types-are-equal
public class AnyOrT<T> {
    // Either contains something or null. The latter implies could be anything
    private T t;

    /**
     * Constructs an 'any' representation of T.
     */
    AnyOrT() {
        t = null;
    }

    /**
     * Specific value of T given by t.
     */
    AnyOrT(T t) {
        this.t = t;
    }

    public boolean isAny() {
        return t == null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AnyOrT<?>)) {
            return false;
        }
        AnyOrT<?> other = (AnyOrT<?>) o;
        if (this.isAny() || other.isAny()) {
            // either is any
            return true;
        }
        return t.equals(other.t);
    }

    @Override
    public int hashCode() {
        return this.isAny() ? "any".hashCode() : t.hashCode();
    }

    @Override
    public String toString() {
        return this.isAny() ? "*" : t.toString();
    }
}
