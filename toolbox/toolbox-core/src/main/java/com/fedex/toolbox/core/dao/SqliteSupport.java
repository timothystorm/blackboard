package com.fedex.toolbox.core.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class SqliteSupport {
    private final JdbcTemplate               _template;
    private final NamedParameterJdbcTemplate _namedTemplate;

    protected SqliteSupport(final DataSource dataSource) {
        _template = new JdbcTemplate(dataSource);
        _namedTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    protected JdbcTemplate template() {
        return _template;
    }

    protected NamedParameterJdbcTemplate namedTemplate() {
        return _namedTemplate;
    }
}
