package com.decadroid.utils;

import androidx.annotation.NonNull;

import io.reactivex.Single;

public class Optional<T> {
    public interface Supplier<T> {
        @NonNull
        T get();
    }
    public interface Mapper<T, R> {
        @NonNull
        R map(T input);
    }

    private T mValue;

    private Optional() {
        mValue = null;
    }

    private Optional(T value) {
        if (value == null) {
            throw new NullPointerException("value must not be null");
        }
        mValue = value;
    }

    public static <T> Optional<T> absent() {
        return new Optional<>();
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    public static <T> Optional<T> ofWithNull(T value) {
        return value == null ? absent() : of(value);
    }

    public T get() {
        if (mValue == null) {
            throw new NullPointerException("value is absent");
        }
        return mValue;
    }

    public boolean isPresent() {
        return mValue != null;
    }

    public T orNull() {
        return mValue;
    }

    public Optional<T> or(Supplier<T> supplier) {
        return of(mValue != null ? mValue : supplier.get());
    }

    public Optional<T> orOptional(Supplier<Optional<T>> supplier) {
        if (mValue != null) {
            return of(mValue);
        }
        return supplier.get();
    }

    public Single<Optional<T>> orSingle(Supplier<Single<T>> supplier) {
        if (isPresent()) {
            return Single.just(of(mValue));
        }
        return supplier.get().map(Optional::of);
    }

    public Single<Optional<T>> orOptionalSingle(Supplier<Single<Optional<T>>> supplier) {
        if (isPresent()) {
            return Single.just(of(mValue));
        }
        return supplier.get();
    }

    public <R> Optional<R> map(Mapper<T, R> mapper) {
        if (mValue == null) {
            return absent();
        }
        return of(mapper.map(mValue));
    }

    public <R> Single<Optional<R>> flatMap(Mapper<T, Single<R>> mapper) {
        if (mValue == null) {
            return Single.just(absent());
        }
        return mapper.map(mValue).map(Optional::of);
    }
}