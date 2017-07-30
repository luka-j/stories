package rs.luka.stories.environment;

import java.io.File;

public class LinuxDebugFiles implements FileProvider {
    public static final File IMAGE_ROOT = new File("/data/Shared/Projects/Stories/images/");
    public static final File BOOKS_ROOT = new File("/data/Shared/Projects/Stories/books/");

    @Override
    public File getImage(String path) {
        return new File(IMAGE_ROOT, path);
    }

    @Override
    public File getSourceDirectory(String path) {
        return new File(BOOKS_ROOT, path);
    }

    @Override
    public boolean imageExists(String path) {
        return getImage(path).isFile();
    }
}
