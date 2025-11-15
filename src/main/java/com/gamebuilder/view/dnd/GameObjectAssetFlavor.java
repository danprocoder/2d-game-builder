package com.gamebuilder.view.dnd;

import java.awt.datatransfer.DataFlavor;

public class GameObjectAssetFlavor extends DataFlavor {
    public GameObjectAssetFlavor() {
        super("application/x-game-object-asset;class=com.gamebuilder.model.asset.GameObjectAsset",
                "Game Object Asset Type");
    }
}
