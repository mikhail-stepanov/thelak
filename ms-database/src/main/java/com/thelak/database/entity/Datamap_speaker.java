package com.thelak.database.entity;

import com.thelak.database.entity.auto._Datamap_speaker;

public class Datamap_speaker extends _Datamap_speaker {

    private static Datamap_speaker instance;

    private Datamap_speaker() {}

    public static Datamap_speaker getInstance() {
        if(instance == null) {
            instance = new Datamap_speaker();
        }

        return instance;
    }
}
