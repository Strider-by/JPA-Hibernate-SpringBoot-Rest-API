package com.epam.esm.repository.util;

import javax.persistence.Query;

public class QueryParametersSetter {

    private Query query;

    private QueryParametersSetter(Query query) {
        this.query = query;
    }

    public static QueryParametersSetter of(Query query) {
        QueryParametersSetter qps = new QueryParametersSetter(query);
        return qps;
    }

    public QueryParametersSetter set(String paramName, Object paramValue) {
        query.setParameter(paramName, paramValue);
        return this;
    }

    public QueryParametersSetter setIf(boolean condition, String paramName, Object paramValue) {
        if (condition) {
            query.setParameter(paramName, paramValue);
        }
        return this;
    }

}
