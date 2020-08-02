package com.thelak.database;

import com.thelak.database.custom.CustomAdapterFactory;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.LifecycleListener;
import org.apache.cayenne.configuration.server.DbAdapterFactory;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.reflect.LifecycleCallbackRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
public class CayenneConfiguration {

    @Bean
    public ServerRuntime serverRuntime(@Autowired DataSource dataSource){
        ServerRuntime serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-thelak-core.xml")
                .addModule(binder -> binder.bind(DbAdapterFactory.class).to(CustomAdapterFactory.class))
                .dataSource(dataSource)
                .build();

        LifecycleCallbackRegistry registry = serverRuntime.getDataDomain().getEntityResolver().getCallbackRegistry();
        registry.addDefaultListener(new MyListener());

        return serverRuntime;
    }

    public class MyListener implements LifecycleListener {

        @Override
        public void postAdd(Object entity) {

        }

        public void prePersist(Object entity) {
            try{
                if (entity instanceof CayenneDataObject){
                    ((CayenneDataObject) entity).writeProperty("createdDate", LocalDateTime.now());
                }
            }catch (Exception ex){

            }
        }

        @Override
        public void postPersist(Object entity) {

        }

        @Override
        public void preRemove(Object entity) {

        }

        @Override
        public void postRemove(Object entity) {

        }

        @Override
        public void preUpdate(Object entity) {
            try{
                if (entity instanceof CayenneDataObject){
                    ((CayenneDataObject) entity).writeProperty("modifiedDate", LocalDateTime.now());
                }
            }catch (Exception ex){

            }
        }

        @Override
        public void postUpdate(Object entity) {

        }

        @Override
        public void postLoad(Object entity) {

        }
    }
}
