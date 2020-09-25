package com.thelak.database.entity;

import com.thelak.database.entity.auto._Datamap_auth;

public class Datamap_auth extends _Datamap_auth {

    private static Datamap_auth instance;

    private Datamap_auth() {}

    public static Datamap_auth getInstance() {
        if(instance == null) {
            instance = new Datamap_auth();
        }

        return instance;
    }
}
