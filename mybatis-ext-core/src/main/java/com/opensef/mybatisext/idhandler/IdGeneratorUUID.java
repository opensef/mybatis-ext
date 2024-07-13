package com.opensef.mybatisext.idhandler;

import java.util.UUID;

public class IdGeneratorUUID implements IdHandler<String> {

    @Override
    public String getId() {
        return UUID.randomUUID().toString();
    }

}
