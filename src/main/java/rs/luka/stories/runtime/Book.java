package rs.luka.stories.runtime;

import rs.luka.stories.environment.DisplayProvider;
import rs.luka.stories.environment.FileProvider;

import java.io.File;
import java.util.List;

public class Book {
    private String name;
    private FileProvider files;
    private DisplayProvider display;
    private List<Chapter> chapters;

    public Book(String name, FileProvider files, DisplayProvider display) {
        this.name = name;
        this.files = files;
        this.display = display;
    }

    public File getImage(String path) {
        return files.getImage(generateImageFile(path));
    }

    public boolean imageExists(String path) {
        return files.imageExists(generateImageFile(path));
    }

    private String generateImageFile(String path) {
        return name + File.separator + path;
    }
}
