package com.thelak.database.entity;

import com.thelak.database.entity.auto._Datamap_smtp;

public class Datamap_smtp extends _Datamap_smtp {

    private static Datamap_smtp instance;

    private Datamap_smtp() {}

    public static Datamap_smtp getInstance() {
        if(instance == null) {
            instance = new Datamap_smtp();
        }

        return instance;
    }
}
