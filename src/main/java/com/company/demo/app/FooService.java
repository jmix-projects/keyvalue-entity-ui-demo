package com.company.demo.app;

import io.jmix.core.entity.KeyValueEntity;
import io.jmix.core.querycondition.Condition;
import io.jmix.core.querycondition.PropertyCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FooService {

    @Autowired
    private JdbcTemplate jdbc;

    public KeyValueEntity load(Long id) {
        KeyValueEntity entity = jdbc.queryForObject("select * from FOO where ID = ?", new FooRowMapper(), id);
        return entity;
    }

    public List<KeyValueEntity> loadList(Condition condition) {
        List<KeyValueEntity> entities;
        String sql = "select * from FOO";
        SimpleSqlConditionProcessor.Result result = SimpleSqlConditionProcessor.process(condition);
        if (result != null) {
            entities = jdbc.query(sql + " where " + result.where(), new FooRowMapper(), result.params());
        } else {
            entities = jdbc.query(sql, new FooRowMapper());
        }
        return entities;
    }

    private static String toSqlOperation(PropertyCondition pc) {
        if (pc.getOperation().equals(PropertyCondition.Operation.CONTAINS)) {
            return "like";
        }
        return pc.getOperation();
    }

    public KeyValueEntity save(KeyValueEntity foo) {
        if (foo.getId() == null) {
            foo.setId(jdbc.queryForObject("call next value for foo_gen", Long.class));
            jdbc.update("insert into FOO (ID, NAME, AMOUNT) values (?, ?, ?)",
                    foo.getId(), foo.getValue("name"), foo.getValue("amount"));
        } else {
            jdbc.update("update FOO set NAME = ?, AMOUNT = ? where ID = ?",
                    foo.getValue("name"), foo.getValue("amount"), foo.getId());
        }
        return foo; // can be also reloaded from DB
    }

    private static class FooRowMapper implements RowMapper<KeyValueEntity> {

        @Override
        public KeyValueEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            KeyValueEntity entity = new KeyValueEntity();
            entity.setIdName("id");
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                entity.setValue(rs.getMetaData().getColumnName(i).toLowerCase(), JdbcUtils.getResultSetValue(rs, i));
            }
            return entity;
        }
    }
}