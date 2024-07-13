package com.opensef.mybatisext.idhandler;

public class IdGeneratorNone implements IdHandler<Object> {

    @Override
    public Object getId() {
        return null;
    }

}
