package org.mulesoft.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.progress.ProgressMonitor;

public class Zip4jUtils {

  private Zip4jUtils() {
    throw new IllegalStateException("Utility class");
  }

  private static Logger logger = Logger.getLogger(Zip4jUtils.class.getName());

  public static void addFiles(String zipPath, String[] files) throws IOException {
    logger.log(Level.INFO, "{0} - {1}", new Object[] {Zip4jUtils.class.getName(), zipPath});
    List<File> listFiles = new ArrayList<>();
    for (int i = 0; i < files.length; i++) {
      listFiles.add(new File(files[i]));
      logger.log(Level.INFO, "{0} - {1}", new Object[] {Zip4jUtils.class.getName(), files[i]});
    }
    try (ZipFile zipFile = new ZipFile(zipPath)) {
      logger.log(Level.INFO, "{0} - Before .setRunInThread", Zip4jUtils.class.getName());
      zipFile.setRunInThread(true);
      logger.log(Level.INFO, "{0} - Before .addFiles", Zip4jUtils.class.getName());
      zipFile.addFiles(listFiles);
      logger.log(Level.INFO, "{0} - After .addFiles", Zip4jUtils.class.getName());
    }
  }

  public static void extract(String zipPath, String outputFolder) throws IOException {
    try (ZipFile zipFile = new ZipFile(zipPath)) {
      zipFile.setRunInThread(true);
      zipFile.extractAll(outputFolder);
    }
  }

  public static Iterable<FileHeader> getEntries(String zipPath) throws IOException {
    try (ZipFile zipFile = new ZipFile(zipPath)) {
      return zipFile.getFileHeaders();
    }
  }

  public static ProgressMonitor getProgress(String zipPath) throws IOException {
    try (ZipFile zipFile = new ZipFile(zipPath)) {
      return zipFile.getProgressMonitor();
    }
  }
}
