package com.thelak.database.entity;

import com.thelak.database.entity.auto._Datamap_article;

public class Datamap_article extends _Datamap_article {

    private static Datamap_article instance;

    private Datamap_article() {}

    public static Datamap_article getInstance() {
        if(instance == null) {
            instance = new Datamap_article();
        }

        return instance;
    }
}
