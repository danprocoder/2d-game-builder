package com.gamebuilder.view.dnd;

import java.awt.datatransfer.DataFlavor;

public class ImageAssetFlavor extends DataFlavor {
    public ImageAssetFlavor() {
        super("application/x-image-asset;class=com.gamebuilder.model.asset.Asset",
                "Image Asset Type");
    }
}
