package com.smokpromotion.SmokProm.domain.repository;



import com.smokpromotion.SmokProm.util.CryptoException;

import java.util.List;

public abstract class AbstractRotatableKey<T> {

    public abstract List<T> getRows(int start, int limit, int keyId);

    public abstract int putRows( List<T> rows);

    public abstract T encrypt(T t, boolean useOldKey) throws CryptoException;

    public abstract T decrypt( T t, boolean useOldKey) throws CryptoException;

}
