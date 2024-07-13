package com.opensef.mybatisext.idhandler;

public class IdGeneratorLong implements IdHandler<Long> {

    @Override
    public Long getId() {
        return IdGen.getId();
    }

}
