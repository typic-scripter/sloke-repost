package cc.sleek.client.config;

import java.io.File;

public class Config {

    private File file;
    private String author;
    private String version;
    private String lastUpdated; // doing this in unix time

    public Config(File file, String author, String version, String lastUpdated) {
        this.file = file;
        this.author = author;
        this.version = version;
        this.lastUpdated = lastUpdated;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
