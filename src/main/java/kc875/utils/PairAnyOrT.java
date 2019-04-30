package kc875.utils;

/**
 * A wrapper that represents any particular instantiation of T, U or any
 * instantiation (like "a", 1 or {set of all strings}, 1 or {all strings}, {all
 * ints}).
 */
// TODO: problem: "any" could be anything in the world; no way to know what
//  type that "any" has ==> so no way to differentiate any of int and any of
//  string. Could do this, but too much work:
//    https://stackoverflow.com/questions/9931611/determine-if-generic-types-are-equal
public class PairAnyOrT<T, U> {
    // Either contains something or null. The latter implies could be anything
    private T fst;
    private U snd;

    /**
     * Specific value of T given by t.
     */
    public PairAnyOrT(T fst, U snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public T getFst() {
        return fst;
    }

    public U getSnd() {
        return snd;
    }

    public boolean fstIsAny() {
        return fst == null;
    }

    public boolean sndIsAny() {
        return snd == null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PairAnyOrT<?, ?>)) {
            return false;
        }
        PairAnyOrT<?, ?> other = (PairAnyOrT<?, ?>) o;
        // if either is (*, *)
        if (this.fstIsAny() && this.sndIsAny())
            return true;
        if (other.fstIsAny() && other.sndIsAny())
            return true;

        // this = (*, a) or (a, *) or (a, b)
        // other = (*, c) or (c, *) or (c, d)
        if (this.fstIsAny())
            return this.snd.equals(other.snd);
        if (other.fstIsAny())
            return other.snd.equals(this.snd);
        // this = (a, *) or (a, b)
        // other = (c, *) or (c, d)
        if (this.sndIsAny())
            return this.fst.equals(other.fst);
        if (other.sndIsAny())
            return other.fst.equals(this.fst);
        // this = (a, b)
        // other = (c, d)
        return this.fst.equals(other.fst) && this.snd.equals(other.snd);
    }

    @Override
    public int hashCode() {
        if (this.fstIsAny() && this.sndIsAny())
            return "(*, *)".hashCode();
        if (!this.fstIsAny() && !this.sndIsAny())
            return fst.hashCode() + snd.hashCode();
        // Don't change this. contains for these objects should default to
        // equals() and so all hashcodes return 0
        return 0;
    }

    @Override
    public String toString() {
        return "(" + (fstIsAny() ? "*" : fst) + ", "
                + (sndIsAny() ? "*" : snd) + ")";
    }
}
