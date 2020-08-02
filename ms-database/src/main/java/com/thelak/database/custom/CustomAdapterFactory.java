package com.thelak.database.custom;

import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.server.DbAdapterDetector;
import org.apache.cayenne.configuration.server.DefaultDbAdapterFactory;
import org.apache.cayenne.dba.AutoAdapter;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.JdbcAdapter;
import org.apache.cayenne.di.Inject;

import javax.sql.DataSource;
import java.util.List;

public class CustomAdapterFactory extends DefaultDbAdapterFactory {

    public CustomAdapterFactory(@Inject(Constants.SERVER_ADAPTER_DETECTORS_LIST) List<DbAdapterDetector> detectors) {
        super(detectors);
    }

    @Override
    public DbAdapter createAdapter(DataNodeDescriptor nodeDescriptor, DataSource dataSource) throws Exception {

        DbAdapter adapter = super.createAdapter(nodeDescriptor, dataSource);
        if (!(adapter instanceof AutoAdapter))
            return adapter;

        AutoAdapter autoAdapter = (AutoAdapter)adapter;

        DbAdapter unwraped = autoAdapter.unwrap();
        if (!(unwraped instanceof JdbcAdapter))
            return adapter;

        ((JdbcAdapter)unwraped).setPkGenerator(new com.thelak.database.custom.StrongPkGenerator((JdbcAdapter)unwraped));
        return adapter;
    }
}
