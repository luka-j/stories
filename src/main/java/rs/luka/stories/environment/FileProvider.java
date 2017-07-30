package rs.luka.stories.environment;

import java.io.File;

public interface FileProvider {
    /**
     * Returns the image file denoted by this path. If no such image exists,
     * returns null.
     *
     * It is expected that most of the outputs of this method to be fed
     * back to the {@link DisplayProvider}, so it is left to the environment
     * which provides these interfaces to follow/break conventions at its
     * own discretion. It is important to note, however, that caller
     * shouldn't assume this method returns non-null values.
     *
     * @param path path to the image
     * @return File pointing to the image file
     */
    File getImage(String path);

    File getSourceDirectory(String path);

    boolean imageExists(String path);
}
