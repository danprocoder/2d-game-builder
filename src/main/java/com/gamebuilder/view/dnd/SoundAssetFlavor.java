package com.gamebuilder.view.dnd;

import java.awt.datatransfer.DataFlavor;

public class SoundAssetFlavor extends DataFlavor {
    public SoundAssetFlavor() {
        super("application/x-sound-asset;class=com.gamebuilder.model.asset.Asset",
                "Sound Asset Type");
    }
}
