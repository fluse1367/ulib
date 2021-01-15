package eu.software4you.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    @Deprecated
    public static void deleteFileFromExistingZip(File zipFile, String file) throws IOException {
        String[] files = {file};
        deleteFilesFromExistingZip(zipFile, files);
    }

    @Deprecated
    public static void deleteFilesFromExistingZip(File zipFile, String[] files) throws IOException {
        // get a temp file
        File tempFile = new File(zipFile.getName() + StringUtils.randomString(20));//File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();

        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk) {
            throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[1024];

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean del = false;
            for (String s : files) {
                if (name.equals(s)) {
                    del = true;
                    break;
                }
            }
            if (!del) {
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(name));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        out.close();
        tempFile.delete();
    }

    @Deprecated
    public static void addFileToExistingZip(File zipFile, File file, String destDirInZip) throws IOException {
        File[] files = {file};
        addFilesToExistingZip(zipFile, files, destDirInZip);
    }

    @Deprecated
    public static void addFilesToExistingZip(File zipFile, File[] files, String destDirInZip) throws IOException {
        // get a temp file
        File tempFile = new File(zipFile.getName() + StringUtils.randomString(20));//File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();

        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk) {
            throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[1024];

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean notInFiles = true;
            for (File f : files) {
                if (f.getName().equals(name)) {
                    notInFiles = false;
                    break;
                }
            }
            if (notInFiles) {
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(name));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
        for (int i = 0; i < files.length; i++) {
            InputStream in = new FileInputStream(files[i]);
            // Add ZIP entry to output stream.
            String dest = "";
            if (!destDirInZip.equals("") && !destDirInZip.endsWith("/")) {
                dest = destDirInZip + "/";
            }
            out.putNextEntry(new ZipEntry(dest + files[i].getName()));
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
        }
        // Complete the ZIP file
        out.close();
        tempFile.delete();
    }

    @Deprecated
    public static void addFilesToExistingZip(File zipFile, File[] files, String[] destDirInZip) throws IOException {
        // get a temp file
        File tempFile = new File(zipFile.getName() + StringUtils.randomString(20));//File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();

        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk) {
            throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[1024];

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean notInFiles = true;
            for (File f : files) {
                if (f.getName().equals(name)) {
                    notInFiles = false;
                    break;
                }
            }
            if (notInFiles) {
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(name));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
        for (int i = 0; i < files.length; i++) {
            InputStream in = new FileInputStream(files[i]);
            // Add ZIP entry to output stream.
            String dest = "";
            if (!destDirInZip[i].equals("") && !destDirInZip[i].endsWith("/")) {
                dest = destDirInZip[i] + "/";
            }
            out.putNextEntry(new ZipEntry(dest + files[i].getName()));
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
        }
        // Complete the ZIP file
        out.close();
        tempFile.delete();
    }

    @Deprecated
    public static void addFileToExistingZipExact(File zipFile,
                                                 InputStream file, String destFileInZip) throws IOException {
        // get a temp file
        File tempFile = new File(zipFile.getName() + StringUtils.randomString(20));//File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();

        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk) {
            throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[1024];

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(name));
            // Transfer bytes from the ZIP file to the output file
            int len;
            while ((len = zin.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
        InputStream in = file;
        // Add ZIP entry to output stream.
        out.putNextEntry(new ZipEntry(destFileInZip));
        // Transfer bytes from the file to the ZIP file
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        // Complete the entry
        out.closeEntry();
        in.close();
        // Complete the ZIP file
        out.close();
        tempFile.delete();
    }

    public static ArrayList<File> listDir(File dir) {
        return listDir(dir, true);
    }

    public static ArrayList<File> listDir(File dir, boolean deep) {
        ArrayList<File> filesArray = new ArrayList<File>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                filesArray.add(files[i]);
                if (files[i].isDirectory() && deep) {
                    for (File h : listDir(files[i])) {
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

    public static void createNewFile(File file) {
        if (file.exists())
            return;
        if (file.getParentFile() != null && !file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new Error("Parent directories could not be created");
        try {
            if (!file.createNewFile())
                throw new Error("File could not be created");
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Deprecated
    public static boolean fileExists(String file) {
        return new File(file).exists();
    }

    @Deprecated
    public static boolean downloadFile(URL source, File dest) {
        try {
            ReadableByteChannel in = Channels.newChannel(source.openStream());
            FileOutputStream out = new FileOutputStream(dest);
            out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
            out.close();
            in.close();
        } catch (IOException e) {
            return false;
        }
        return true;
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

    @Deprecated
    public static String downloadWebsiteSource(URL url) {
        URL u;
        InputStream is = null;
        DataInputStream dis;
        String s;
        StringBuffer sb = new StringBuffer();
        try {
            u = url;
            is = u.openStream();
            dis = new DataInputStream(new BufferedInputStream(is));
            while ((s = dis.readLine()) != null) {
                sb.append(s + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
            }
        }
        return sb.toString();
    }

    public static File getClassFile(Class<?> clazz) {
        try {
            return new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public static File createZipFrom(File source, File dest) {
        if (dest.exists()) dest.delete();
        try {
            dest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<File> files = new ArrayList<>(Arrays.asList(source));

        if (source.isDirectory()) files = listDir(source);

        File[] fs = new File[files.size()];
        String[] fst = new String[files.size()];

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            fs[i] = file;
            fst[i] = file.getPath().substring(0, file.getPath().lastIndexOf(File.separator));
        }
        try {
            addFilesToExistingZip(dest, fs, fst);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dest;
    }

    @Deprecated
    public static String[] list(File dir) {
        if (dir.isDirectory()) {
            return dir.list();
        } else {
            return null;
        }
    }

    public static void saveResource(Class<?> clazz, String resourcePath, boolean replace) {
        saveResource(clazz, resourcePath, "./", replace);
    }

    public static void saveResource(Class<?> clazz, String resourcePath, boolean replace, boolean alert) {
        saveResource(clazz, resourcePath, "./", replace, alert);
    }

    @Deprecated
    public static void saveResource(Class<?> clazz, String resourcePath, String saveDir, boolean replace) {
        saveResource(clazz, resourcePath, new File(saveDir), replace);
    }

    public static void saveResource(Class<?> clazz, String resourcePath, File saveDir, boolean replace) {
        saveResource(clazz, resourcePath, saveDir, replace, true);
    }

    @Deprecated
    public static void saveResource(Class<?> clazz, String resourcePath, String saveDir, boolean replace, boolean alert) {
        saveResource(clazz, resourcePath, new File(saveDir), replace, alert);
    }

    public static void saveResource(Class<?> clazz, String resourcePath, File saveDir, boolean replace, boolean alert) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResourceStream(clazz, resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getClassFile(clazz).getName());
        }

        File outFile = new File(saveDir, resourcePath.substring(resourcePath.lastIndexOf('/') + 1));

        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else if (alert) {
                System.err.println("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            System.err.println("Could not save " + outFile.getName() + " to " + outFile);
            ex.printStackTrace();
        }
    }

    public static InputStream getResourceStream(Class<?> clazz, String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        try {
            URL url = clazz.getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}
