package com.apulaz.books.mocks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

public class TypedQueryMock<X> implements TypedQuery<X> {

    private final List<X> resultList;

    private final Runnable updater;

    public TypedQueryMock(List<X> resultList) {
        this.resultList = resultList;
        this.updater = () -> {};
    }

    public TypedQueryMock(Runnable updater) {
        this.resultList = null;
        this.updater = updater;
    }

    @Override
    public List<X> getResultList() {
        return resultList;
    }

    @Override
    public X getSingleResult() {
        return null;
    }

    @Override
    public int executeUpdate() {
        updater.run();
        return 0;
    }

    @Override
    public TypedQuery<X> setMaxResults(int i) {
        return null;
    }

    @Override
    public int getMaxResults() {
        return 0;
    }

    @Override
    public TypedQuery<X> setFirstResult(int i) {
        return null;
    }

    @Override
    public int getFirstResult() {
        return 0;
    }

    @Override
    public TypedQuery<X> setHint(String s, Object o) {
        return null;
    }

    @Override
    public Map<String, Object> getHints() {
        return null;
    }

    @Override
    public <T> TypedQuery<X> setParameter(Parameter<T> parameter, T t) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<Calendar> parameter, Calendar calendar, TemporalType temporalType) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<Date> parameter, Date date, TemporalType temporalType) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(String s, Object o) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(String s, Calendar calendar, TemporalType temporalType) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(String s, Date date, TemporalType temporalType) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(int i, Object o) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(int i, Calendar calendar, TemporalType temporalType) {
        return null;
    }

    @Override
    public TypedQuery<X> setParameter(int i, Date date, TemporalType temporalType) {
        return null;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return null;
    }

    @Override
    public Parameter<?> getParameter(String s) {
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(String s, Class<T> aClass) {
        return null;
    }

    @Override
    public Parameter<?> getParameter(int i) {
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(int i, Class<T> aClass) {
        return null;
    }

    @Override
    public boolean isBound(Parameter<?> parameter) {
        return false;
    }

    @Override
    public <T> T getParameterValue(Parameter<T> parameter) {
        return null;
    }

    @Override
    public Object getParameterValue(String s) {
        return null;
    }

    @Override
    public Object getParameterValue(int i) {
        return null;
    }

    @Override
    public TypedQuery<X> setFlushMode(FlushModeType flushModeType) {
        return null;
    }

    @Override
    public FlushModeType getFlushMode() {
        return null;
    }

    @Override
    public TypedQuery<X> setLockMode(LockModeType lockModeType) {
        return null;
    }

    @Override
    public LockModeType getLockMode() {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }
}
