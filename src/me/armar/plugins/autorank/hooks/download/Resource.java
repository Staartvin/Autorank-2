package me.armar.plugins.autorank.hooks.download;

public class Resource {

    private boolean external;
    private SpigetFile file;

    public Resource(SpigetFile file, boolean external) {
        this.file = file;
        this.external = external;
    }

    public SpigetFile getFile() {
        return file;
    }

    public void setFile(SpigetFile file) {
        this.file = file;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

}
