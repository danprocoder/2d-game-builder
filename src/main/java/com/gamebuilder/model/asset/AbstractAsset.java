package com.gamebuilder.model.asset;

import java.util.UUID;

public abstract class AbstractAsset {
    protected String id;
    protected String name;

    public AbstractAsset() {
        this.id = UUID.randomUUID().toString();
    }

    public AbstractAsset(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public abstract String toXml();
}
