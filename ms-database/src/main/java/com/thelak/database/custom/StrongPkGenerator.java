package com.thelak.database.custom;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.dba.JdbcAdapter;
import org.apache.cayenne.dba.postgres.PostgresPkGenerator;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbKeyGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.UUID;

public class StrongPkGenerator extends PostgresPkGenerator {

    public StrongPkGenerator(JdbcAdapter adapter) {
        super(adapter);
        pkStartValue = 1;
        pkCacheSize = 1;
    }

    @Override
    protected String createSequenceString(DbEntity ent) {
        // note that PostgreSQL 7.4 and newer supports INCREMENT BY and START WITH
        // however 7.3 doesn't like BY and WITH, so using older more neutral
        // syntax that works with all tested versions.
        return "CREATE SEQUENCE IF NOT EXISTS" + sequenceName(ent) + " INCREMENT 1 START " + pkStartValue;
    }

    @Override
    protected long longPkFromDatabase(DataNode node, DbEntity entity) throws Exception {

        DbKeyGenerator pkGenerator = entity.getPrimaryKeyGenerator();
        String pkGeneratingSequenceName;
        if (pkGenerator != null && DbKeyGenerator.ORACLE_TYPE.equals(pkGenerator.getGeneratorType())
                && pkGenerator.getGeneratorName() != null) {
            pkGeneratingSequenceName = pkGenerator.getGeneratorName();
        } else {
            pkGeneratingSequenceName = sequenceName(entity);
        }
        String sql = selectNextValQuery(pkGeneratingSequenceName);

        try (Connection con = node.getDataSource().getConnection()) {
            try (Statement st = con.createStatement()) {
                String createGeneratorSql = createSequenceString(entity);
                adapter.getJdbcEventLogger().log(createGeneratorSql);
                st.execute(createGeneratorSql);
            }

            try (Statement st = con.createStatement()) {
                adapter.getJdbcEventLogger().log(sql);

                try (ResultSet rs = st.executeQuery(sql)) {
                    if (!rs.next()) {
                        throw new CayenneRuntimeException("Error generating pk for DbEntity %s", entity.getName());
                    }
                    return rs.getLong(1);
                }
            }
        }
    }

    public Object generatePk(DataNode node, DbAttribute pk) throws Exception {
        if (pk.getType() == Types.OTHER){
            return UUID.randomUUID();
        }
        return super.generatePk(node, pk);
    }
}
