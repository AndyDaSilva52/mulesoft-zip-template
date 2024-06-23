package org.mulesoft.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This utility compresses a list of files to standard ZIP format file. It is able to compress all
 * sub files and sub directories, recursively.
 *
 *
 */
public class ZipUtility {

  static Logger logger = Logger.getLogger("ZipUtility.class");

  /**
   * A constants for buffer size used to read/write data
   */
  private static final int BUFFER_SIZE = 1024; // parameter

  /**
   * Compresses files represented in an array of paths
   *
   * @param files a String array containing file paths
   * @param destZipFile The path of the destination zip file
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void zip(String[] files, String destZipFile) throws IOException {

    List<File> listFiles = new ArrayList<File>();

    for (int i = 0; i < files.length; i++) {
      listFiles.add(new File(files[i]));
    }

    File zip = new File(destZipFile);

    try {
      ZipUtility.zipFiles(listFiles, zip);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Zips a collection of files to a destination zip output stream.
   *
   * @param files A collection of files and directories
   * @param outputStream The output stream of the destination zip file
   * @throws FileNotFoundException
   * @throws IOException
   */
  private static void zipFiles(List<File> files, OutputStream outputStream) throws IOException {

    ZipOutputStream zos = new ZipOutputStream(outputStream);

    for (File file : files) {
      if (file.isDirectory()) { // if it's a folder
        addFolderToZip("", file, zos);
      } else {
        addFileToZip("", file, zos);
      }
    }

    zos.finish();
  }

  /**
   * Adds a directory to the current zip output stream
   *
   * @param folder the directory to be added
   * @param parentFolder the path of parent directory
   * @param zos the current zip output stream
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void zipDirectory(File folder, String parentFolder, ZipOutputStream zos)
      throws IOException {

    for (File file : folder.listFiles()) {

      if (file.isDirectory()) {
        // Recursive
        zipDirectory(file, parentFolder + "/" + file.getName(), zos);
        continue;
      }

      zos.putNextEntry(new ZipEntry(
          /**
           * parentFolder + "/" +
           */
          file.getName()));

      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

      long bytesRead = 0L;

      byte[] bytesIn = new byte[BUFFER_SIZE];
      int read = 0;

      while ((read = bis.read(bytesIn)) != -1) {

        zos.write(bytesIn, 0, read);

        bytesRead += read;
      }

      zos.closeEntry();
      bis.close();
    }

  }

  /**
   * Adds a directory to the current zip
   *
   * @param path the path of the parent folder in the zip
   * @param folder the directory to be added
   * @param zos the current zip output stream
   * @throws FileNotFoundException
   * @throws IOException
   */
  private static void addFolderToZip(String path, File folder, ZipOutputStream zos)
      throws IOException {

    String currentPath =
        StringUtils.isNotEmpty(path) ? path + "/" + folder.getName() : folder.getName();

    for (File file : folder.listFiles()) {
      if (file.isDirectory()) {
        addFolderToZip(currentPath, file, zos);
      } else {
        addFileToZip(currentPath, file, zos);
      }
    }
  }

  /**
   * Adds a file to the current zip output stream
   *
   * @param file the file to be added
   * @param zos the current zip output stream
   * @throws FileNotFoundException
   * @throws IOException
   */
  private static void zipFile(File file, ZipOutputStream zos) throws IOException {

    // String currentPath = StringUtils.isNotEmpty(path)
    // ? path + "/" + file.getName()
    // : file.getName();

    zos.putNextEntry(new ZipEntry(file.getName()));

    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

    long bytesRead = 0L;

    byte[] bytesIn = new byte[BUFFER_SIZE];
    int read = 0;

    while ((read = bis.read(bytesIn)) != -1) {
      zos.write(bytesIn, 0, read);
      bytesRead += read;
    }

    zos.closeEntry();
    bis.close();
  }

  /**
   * Adds a file to the current zip output stream
   *
   * @param path the path of the parent folder in the zip
   * @param file the file to be added
   * @param zos the current zip output stream
   * @throws FileNotFoundException
   * @throws IOException
   */
  private static void addFileToZip(String path, File file, ZipOutputStream zos) throws IOException {
    String currentPath =
        StringUtils.isNotEmpty(path) ? path + "/" + file.getName() : file.getName();

    zos.putNextEntry(new ZipEntry(currentPath));

    InputStream is = new BufferedInputStream(new FileInputStream(file));
    try {
      IOUtils.copy(is, zos);
    } finally {
      IOUtils.closeQuietly(is);
    }
    zos.closeEntry();
  }

  /**
   * Zips a collection of files to a destination zip file.
   *
   * @param files A collection of files and directories
   * @param zipFile The path of the destination zip file
   * @throws FileNotFoundException
   * @throws IOException
   */
  private static void zipFiles(List<File> files, File zipFile) throws IOException {
    OutputStream os = new BufferedOutputStream(new FileOutputStream(zipFile));
    try {
      zipFiles(files, os);
    } finally {
      IOUtils.closeQuietly(os);
    }
  }

  /**
   * Unzips a zip from an input stream into an output folder.
   *
   * @param inputStream the zip input stream
   * @param outputFolder the output folder where the files
   * @throws IOException
   */
  private static void unZipFiles(InputStream inputStream, File outputFolder) throws IOException {
    ZipInputStream zis = new ZipInputStream(inputStream);
    ZipEntry ze = zis.getNextEntry();

    while (ze != null) {
      File file = new File(outputFolder, ze.getName());
      OutputStream os = new BufferedOutputStream(FileUtils.openOutputStream(file));

      try {
        IOUtils.copy(zis, os);
      } finally {
        IOUtils.closeQuietly(os);
      }

      zis.closeEntry();
      ze = zis.getNextEntry();
    }
  }

  /**
   * Unzips a zip file into an output folder.
   *
   * @param zipFile the zip file path
   * @param outputFolder the output folder where the files will be extracted
   * @throws IOException
   */
  public static void unZipFiles(String zipFile, String outputFolder) throws IOException {

    File outFolder = new File(outputFolder);
    File zip = new File(zipFile);

    unZipFiles(zip, outFolder);

  }

  /**
   * Unzips a zip file into an output folder.
   *
   * @param zipFile the zip file
   * @param outputFolder the output folder where the files
   * @throws IOException
   */
  private static void unZipFiles(File zipFile, File outputFolder) throws IOException {
    InputStream is = new BufferedInputStream(new FileInputStream(zipFile));
    try {
      unZipFiles(is, outputFolder);
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  /**
   *
   * @param zipFile
   * @param files
   * @throws IOException
   */
  public static void addFilesToExisintgZip(String zipFile, String[] files) throws IOException {

    List<File> listFiles = new ArrayList<>();

    for (int i = 0; i < files.length; i++) {
      listFiles.add(new File(files[i]));
    }

    File zip = new File(zipFile);

    try {
      ZipUtility.addFilesToExistingZip(zip, listFiles);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param zipFile
   * @param files
   * @throws IOException
   */
  private static void addFilesToExistingZip(File zipFile, List<File> files) throws IOException {
    // get a temp file
    File tempFile = File.createTempFile(zipFile.getName(), null);
    // delete it, otherwise you cannot rename your existing zip to it.
    Files.delete(tempFile.toPath());

    boolean renameOk = zipFile.renameTo(tempFile);
    if (!renameOk) {
      throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to "
          + tempFile.getAbsolutePath());
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
    for (int i = 0; i < files.toArray().length; i++) {
      InputStream in = new FileInputStream(files.get(i));
      // Add ZIP entry to output stream.
      out.putNextEntry(new ZipEntry(files.get(i).getName()));
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

    Files.delete(tempFile.toPath());
  }

  public static List<String> getZipFileList(String file) throws IOException {

    File zip = new File(file);

    return getZipFileList(zip);

  }

  private static List<String> getZipFileList(File file) {

    List<String> filesList = new ArrayList<>();

    try {
      ZipFile zipFile = new ZipFile(file);

      Enumeration<? extends ZipEntry> entries = zipFile.entries();

      // System.out.println("Entries:");

      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        filesList.add(entry.getName());

        logger.info("ZipEntry: " + entry.getName());
        // System.out.println(entry.getName());
      }

      zipFile.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return filesList;
  }
}
