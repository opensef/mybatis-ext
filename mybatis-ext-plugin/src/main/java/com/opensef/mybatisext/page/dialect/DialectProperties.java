package com.opensef.mybatisext.page.dialect;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "page.dialect")
public class DialectProperties {

    /**
     * 方言
     */
    private String name;

    private Map<String, DialectInfo> dynamic;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, DialectInfo> getDynamic() {
        return dynamic;
    }

    public void setDynamic(Map<String, DialectInfo> dynamic) {
        this.dynamic = dynamic;
    }

    public static class DialectInfo {

        /**
         * 方言
         */
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
