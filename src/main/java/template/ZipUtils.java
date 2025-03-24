package template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.progress.ProgressMonitor;

public class ZipUtils {

	private ZipUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * A constants for buffer size used to read/write data
	 */
	private static final int BUFFER_SIZE = 4096; // parameter

	private static Logger logger = Logger.getLogger(ZipUtils.class.getName());

	public static void addFiles(String zipPath, String[] files) throws IOException {

		List<File> listFiles = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			listFiles.add(new File(files[i]));
		}

		try (ZipFile zipFile = new ZipFile(zipPath)) {
			zipFile.setRunInThread(true);

			zipFile.addFiles(listFiles);
		}

	}

	public static void addFilesWithStream(String zipPath, String[] files) throws IOException {

		byte[] buff = new byte[BUFFER_SIZE];
		int readLen;

		File outputZipFile = new File(zipPath);

		List<File> filesToAdd = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			filesToAdd.add(new File(files[i]));
		}

		try (ZipOutputStream zos = initializeZipOutputStream(outputZipFile)) {

			for (File fileToAdd : filesToAdd) {
				zos.putNextEntry(new ZipEntry(fileToAdd.getName()));

				try (InputStream inputStream = new FileInputStream(fileToAdd)) {
					while ((readLen = inputStream.read(buff)) != -1) {
						zos.write(buff, 0, readLen);
					}
				}
				zos.closeEntry();
			}
		}
	}

	private static ZipOutputStream initializeZipOutputStream(File outputZipFile) throws IOException {

		FileOutputStream fos = new FileOutputStream(outputZipFile);

		return new ZipOutputStream(fos);
	}




	
	public static void extract(String zipPath, String outputFolder) throws IOException {

		try (ZipFile zipFile = new ZipFile(zipPath)) {
			zipFile.setRunInThread(true);

			zipFile.extractAll(outputFolder);
		}

	}

	public void extractWithStream(String zipPath) throws IOException {
		File zipFile = new File(zipPath);

		ZipEntry zipEntry;
		int readLen;
		byte[] readBuffer = new byte[4096];

		InputStream inputStream = new FileInputStream(zipFile);

		try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				File extractedFile = new File(zipEntry.getName());

				try (OutputStream outputStream = new FileOutputStream(extractedFile)) {
					while ((readLen = zipInputStream.read(readBuffer)) != -1) {
						outputStream.write(readBuffer, 0, readLen);
					}
				}
			}
		}

	}

	public static Iterable<FileHeader> getEntries(String zipPath) throws IOException {

		//List<String> filesList = new ArrayList<>();

		try (ZipFile zipFile = new ZipFile(zipPath)) {

			zipFile.getFileHeaders();
			//.stream().forEach(fileHeader -> filesList.add(fileHeader.getFileName()));
			return zipFile.getFileHeaders();
		}

		
	}

	/**
	 * 
	 */
	public static List<String> getZipFileList(String file) {

		File zip = new File(file);

		return getZipFileList(zip);

	}

	private static List<String> getZipFileList(File file) {

		List<String> filesList = new ArrayList<>();

		try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(file)) {

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				filesList.add(entry.getName());

				logger.log(Level.INFO, "ZipEntry: {0} ", entry.getName());
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

		return filesList;
	}
	
	public static ProgressMonitor getProgress(String zipPath) throws IOException {
		try (ZipFile zipFile = new ZipFile(zipPath)) {
			// return object with FileName, CurrentTask, Result and PercentageDone
			return zipFile.getProgressMonitor();
		}
	}
}
