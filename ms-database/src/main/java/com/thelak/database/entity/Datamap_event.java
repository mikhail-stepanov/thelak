package com.thelak.database.entity;

import com.thelak.database.entity.auto._Datamap_event;

public class Datamap_event extends _Datamap_event {

    private static Datamap_event instance;

    private Datamap_event() {}

    public static Datamap_event getInstance() {
        if(instance == null) {
            instance = new Datamap_event();
        }

        return instance;
    }
}
