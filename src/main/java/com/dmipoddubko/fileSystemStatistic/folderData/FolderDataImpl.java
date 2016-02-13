package com.dmipoddubko.fileSystemStatistic.folderData;

public class FolderDataImpl implements FolderData {
    private String name;
    private String path;
    private String type;
    private long size;
    private int id;

    public FolderDataImpl(String name, String path, String type, long size) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.size = size;
    }

    public FolderDataImpl(String name, String path, String type, long size, int id) {
        this(name, path, type, size);
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public int getId() {
        return id;
    }
}
