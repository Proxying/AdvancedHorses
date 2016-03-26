package com.cherryio.utils;

import com.cherryio.AdvancedHorses;

/**
 * Created by Kieran on 25-Mar-16.
 */
public class Config<T> {

    private String property;

    public Config(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    @SuppressWarnings("unchecked")
    public T getValue() {
        return (T) AdvancedHorses.getInstance().getConfig().get(property);
    }
}
