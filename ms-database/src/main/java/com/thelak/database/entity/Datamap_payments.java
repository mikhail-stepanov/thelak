package com.thelak.database.entity;

import com.thelak.database.entity.auto._Datamap_payments;

public class Datamap_payments extends _Datamap_payments {

    private static Datamap_payments instance;

    private Datamap_payments() {}

    public static Datamap_payments getInstance() {
        if(instance == null) {
            instance = new Datamap_payments();
        }

        return instance;
    }
}
