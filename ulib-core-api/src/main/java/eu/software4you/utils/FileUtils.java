package eu.software4you.utils;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileUtils {

    public static ArrayList<File> listDir(File dir) {
        return listDir(dir, true);
    }

    public static ArrayList<File> listDir(File dir, boolean deep) {
        ArrayList<File> filesArray = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                filesArray.add(file);
                if (file.isDirectory() && deep) {
                    for (File h : listDir(file)) {
                        if (!filesArray.contains(h)) {
                            filesArray.add(h);
                        }
                    }
                }
            }
        }
        return filesArray;
    }

    public static void copyFile(File in, File out) throws IOException {
        try (FileChannel inChannel = new FileInputStream(in).getChannel(); FileChannel outChannel = new FileOutputStream(out).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
    }

    public static void createNewFile(String newFile) {
        createNewFile(new File(newFile));
    }

    @SneakyThrows
    public static void createNewFile(File file) {
        if (file.exists())
            return;
        if (file.getParentFile() != null && !file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new RuntimeException("Parent directories could not be created");
        if (!file.createNewFile())
            throw new RuntimeException("File could not be created");
    }

    public static boolean remove(File file) {
        return remove(file, true);
    }

    public static boolean remove(File file, boolean stopOnError) {
        if (file.exists())
            if (file.isDirectory()) {
                for (File f : file.listFiles())
                    if (!remove(f) && stopOnError)
                        return false;
            } else {
                return file.delete();
            }
        return false;
    }

    public static File getClassFile(Class<?> clazz) {
        try {
            return new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            return null;
        }
    }
}
