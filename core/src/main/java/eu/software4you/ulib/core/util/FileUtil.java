package eu.software4you.ulib.core.util;

import eu.software4you.ulib.core.reflect.ReflectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileUtil {

    /**
     * Lists the files in the given directory.
     *
     * @param dir  the directory to list the files of
     * @param deep if listing should happen recursively
     * @return a collection containing the listed files
     */
    @NotNull
    public static Collection<File> listDir(@NotNull File dir, boolean deep) {
        if (!dir.isDirectory())
            return Collections.singleton(dir);

        var files = new LinkedList<File>();
        for (File file : dir.listFiles()) {
            files.add(file);
            if (!file.isDirectory() || !deep) {
                continue;
            }

            files.addAll(listDir(file, true));
        }
        return ReflectUtil.getCallerClass() != FileUtil.class ? List.copyOf(files) : files;
    }

    /**
     * Attempts to create a new file if it doesn't exist yet. Also attempts to create parent directories if necessary.
     *
     * @param file the file to create
     * @return an {@link Expect} object wrapping the operation result
     */
    @NotNull
    public static Expect<File, IOException> createNewFile(@NotNull File file) {
        if (file.exists())
            return Expect.of(file);
        if (file.getParentFile() != null && !file.getParentFile().exists() && !file.getParentFile().mkdirs())
            return Expect.failed(new IOException("Parent directories could not be created"));

        return Expect.compute(() -> {
            if (!file.createNewFile())
                throw new IOException("File could not be created");
            return file;
        });
    }

    /**
     * Attempts to delete a file. If the file is a directory the directory is recursively deleted.
     *
     * @param file        the file to delete
     * @param stopOnError if the operation should stop on error
     * @return an empty optional if all files could be deleted, otherwise an optional wrapping a collection containing the files that couldn't be deleted
     */
    @NotNull
    public static Optional<Collection<File>> delete(@NotNull File file, boolean stopOnError) {
        if (!file.exists())
            return Optional.empty();

        if (file.isFile() || file.isDirectory() && file.list().length == 0) {
            return file.delete() ? Optional.empty() : Optional.of(Collections.singleton(file));
        }
        if (!file.isDirectory())
            throw new IllegalStateException();

        var fail = new LinkedList<File>();
        for (File f : ArrayUtil.concat(file.listFiles(), file)) {
            var res = delete(f, stopOnError);
            if (res.isPresent()) {
                fail.addAll(res.get());
                if (stopOnError)
                    break;
            }
        }
        return fail.isEmpty() ? Optional.empty() : Optional.of(ReflectUtil.getCallerClass() != FileUtil.class ? List.copyOf(fail) : fail);
    }

    /**
     * Obtains the file the specified class is located in.
     *
     * @param clazz the class to search the file for
     * @return an optional wrapping the file, or an empty optional if the file cannot be obtained
     */
    @NotNull
    public static Expect<File, ?> getClassFile(@Nullable Class<?> clazz) {
        return Expect.compute(() -> clazz.getProtectionDomain().getCodeSource().getLocation().toURI())
                .map(File::new);
    }
}
