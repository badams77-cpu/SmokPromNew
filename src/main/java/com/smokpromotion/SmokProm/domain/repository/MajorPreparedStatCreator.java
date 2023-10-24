package com.smokpromotion.SmokProm.domain.repository;

import org.springframework.jdbc.core.PreparedStatementCreator;

public interface MajorPreparedStatCreator extends PreparedStatementCreator {

    void setGenKey(boolean bol0);

}
