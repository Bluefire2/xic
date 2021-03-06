/*
 * This class is from https://github.com/npryce/maybe-java
 *
 * The repo is under the Apache License, which can be found at
 * https://github.com/npryce/maybe-java/blob/master/COPYING
 */

package kc875.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Maybe<T> implements Iterable<T> {
    public abstract boolean isKnown();
    public abstract T otherwise(T defaultValue);
    public abstract Maybe<T> otherwise(Maybe<T> maybeDefaultValue);
    public abstract T otherwise(Supplier<T> supplier);
    public abstract <U> Maybe<U> to(Function<? super T, ? extends U> mapping);
    public abstract <U> Maybe<U> toMaybe(Function<? super T, Maybe<U>> mapping);
    public abstract void thenDo(Consumer<T> cons);
    public abstract Maybe<Boolean> query(Predicate<? super T> mapping);
    public abstract T get() throws NoMaybeValueException;

    /**
     * Returns true if fst and snd are the same maybes, i.e., either fst and
     * snd are both unknowns or if both are definitelys with the same value
     * as determined by T.equals().
     * @param fst first maybe.
     * @param snd second maybe.
     */
    public static <T> boolean sameMaybe(Maybe<T> fst, Maybe<T> snd) {
        return !fst.isKnown() && !snd.isKnown()
                || fst.to(f -> snd.to(f::equals).otherwise(false)).otherwise(false);

    }

    public static <T> Maybe<T> unknown() {
        return new Maybe<T>() {
            @Override
            public boolean isKnown() {
                return false;
            }

            public Iterator<T> iterator() {
                return Collections.<T>emptyList().iterator();
            }

            @Override
            public T otherwise(T defaultValue) {
                return defaultValue;
            }

            @Override
            public Maybe<T> otherwise(Maybe<T> maybeDefaultValue) {
                return maybeDefaultValue;
            }

            @Override
            public T otherwise(Supplier<T> supplier) {
                return supplier.get();
            }

            @Override
            public <U> Maybe<U> to(Function<? super T, ? extends U> mapping) {
                return unknown();
            }

            @Override
            public <U> Maybe<U> toMaybe(Function<? super T, Maybe<U>> mapping) {
                return unknown();
            }

            @Override
            public void thenDo(Consumer<T> cons) {
                // do nothing
            }

            @Override
            public Maybe<Boolean> query(Predicate<? super T> mapping) {
                return unknown();
            }

            @Override
            public T get() throws NoMaybeValueException {
                throw new NoMaybeValueException("Maybe does not contain a value");
            }

            @Override
            public String toString() {
                return "unknown";
            }

            @Override
            @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass"})
            public boolean equals(Object obj) {
                return false;
            }

            @Override
            public int hashCode() {
                return 0;
            }
        };
    }

    public static <T> Maybe<T> definitely(final T theValue) {
        return new DefiniteValue<T>(theValue);
    }

    private static class DefiniteValue<T> extends Maybe<T> {
        private final T theValue;

        public DefiniteValue(T theValue) {
            this.theValue = theValue;
        }

        @Override
        public boolean isKnown() {
            return true;
        }

        public Iterator<T> iterator() {
            return Collections.singleton(theValue).iterator();
        }

        @Override
        public T otherwise(T defaultValue) {
            return theValue;
        }

        @Override
        public Maybe<T> otherwise(Maybe<T> maybeDefaultValue) {
            return this;
        }

        @Override
        public T otherwise(Supplier<T> supplier) {
            return theValue;
        }

        @Override
        public <U> Maybe<U> to(Function<? super T, ? extends U> mapping) {
            return definitely(mapping.apply(theValue));
        }

        @Override
        public <U> Maybe<U> toMaybe(Function<? super T, Maybe<U>> mapping) {
            return mapping.apply(theValue);
        }

        @Override
        public void thenDo(Consumer<T> consumer) {
            consumer.accept(theValue);
        }

        @Override
        public Maybe<Boolean> query(Predicate<? super T> mapping) {
            return definitely(mapping.test(theValue));
        }

        @Override
        public T get() throws NoMaybeValueException {
            return theValue;
        }

        @Override
        public String toString() {
            return "definitely " + theValue.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DefiniteValue<?> that = (DefiniteValue<?>) o;

            return theValue.equals(that.theValue);

        }

        @Override
        public int hashCode() {
            return theValue.hashCode();
        }
    }

    public static class NoMaybeValueException extends Exception {
        NoMaybeValueException(String message) {
            super(message);
        }
    }
}