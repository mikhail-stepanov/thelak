package com.thelak.database.entity;

import com.thelak.database.entity.auto._Datamap_video;

public class Datamap_video extends _Datamap_video {

    private static Datamap_video instance;

    private Datamap_video() {}

    public static Datamap_video getInstance() {
        if(instance == null) {
            instance = new Datamap_video();
        }

        return instance;
    }
}
