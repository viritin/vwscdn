
package com.vaadin.wscdn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Matti Tahvonen
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private String name;
    private Integer version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
