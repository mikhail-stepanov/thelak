package com.thelak.database.entity;

import com.thelak.database.entity.auto._Datamap_category;

public class Datamap_category extends _Datamap_category {

    private static Datamap_category instance;

    private Datamap_category() {}

    public static Datamap_category getInstance() {
        if(instance == null) {
            instance = new Datamap_category();
        }

        return instance;
    }
}
