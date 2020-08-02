package com.thelak.database;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private ServerRuntime cayenneRuntime;

    @Autowired
    public DatabaseService(ServerRuntime serverRuntime){
        this.cayenneRuntime = serverRuntime;
    }

    public ObjectContext getContext(){
        return cayenneRuntime.newContext();
    }
}
