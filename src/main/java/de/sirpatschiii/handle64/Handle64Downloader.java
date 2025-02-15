package de.sirpatschiii.handle64;

import de.sirpatschiii.base.Configuration;
import org.slf4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Handle64Downloader {
    private static final Logger logger = Configuration.INSTANCE.getLogger();
    private static final String HANDLE_URL = Configuration.INSTANCE.HANDLE_URL;
    private static final String HANDLE_EXE = Configuration.INSTANCE.HANDLE_EXE;
    private static final String INSTALL_DIR = Configuration.INSTANCE.INSTALL_DIR;

    public static void downloadAndExtractHandle64() {
        Path installPath = Path.of(INSTALL_DIR);
        Path zipPath = installPath.resolve("Handle.zip");
        Path exePath = installPath.resolve(HANDLE_EXE);

        // Create installation directory if not already existent
        try {
            if (!Files.exists(installPath)) {
                Files.createDirectories(installPath);
            }
        } catch (IOException e) {
            logger.error("In the process of creating the tools directory occurred an error!", e);
            throw new RuntimeException(e);
        }

        // Check if handle.exe is already downloaded
        if (Files.exists(exePath)) {
            logger.info("Handle.exe is already installed: {}", exePath);
            return;
        }

        // Download file
        logger.info("Starting downloading handle.exe ...");
        try (InputStream in = new URL(HANDLE_URL).openStream(); FileOutputStream out = new FileOutputStream(zipPath.toFile())) {
            try {
                in.transferTo(out);
            } catch (IOException e) {
                logger.error("In the download process of handle.exe occurred an error!", e);
                throw new RuntimeException(e);
            }
        } catch (MalformedURLException e) {
            logger.error("The syntax of the URL to handle.exe might be incorrect!", e);
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            logger.error("The file output stream could not find the destination file!", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("In the process of downloading handle.exe occurred an error!", e);
            throw new RuntimeException(e);
        }
        logger.info("Download of handle.exe completed successfully.");

        // Unpack zip
        unzipFile(zipPath.toFile(), installPath.toFile());

        // Check if handle.exe is existent now
        if (Files.exists(exePath) && Files.isExecutable(exePath)) {
            logger.info("Handle64.exe extracted correctly.");
        } else {
            logger.error("Handle64.exe couldn't be found in the tools directory!");
        }

        // Delete zip file
        try {
            Files.deleteIfExists(zipPath);
        } catch (IOException e) {
            logger.error("In the deletion process of the zip file occurred an error!", e);
            throw new RuntimeException(e);
        }
    }

    private static void unzipFile(File zipFile, File outputDir) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // Extract handle64.exe
                if (entry.getName().equals(HANDLE_EXE)) {
                    File outputFile = new File(outputDir, entry.getName());
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        zis.transferTo(fos);
                    } catch (FileNotFoundException e) {
                        logger.error("The file output stream could not find the destination file!", e);
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        logger.error("In the process of saving the file from the zip occurred an error!", e);
                        throw new RuntimeException(e);
                    }
                    outputFile.setExecutable(true);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("The file output stream could not find the destination file!", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("In the process of unzipping occurred an error!", e);
            throw new RuntimeException(e);
        }
    }

    public static boolean isHandle64Existent() {
        return Files.exists(Path.of(INSTALL_DIR).resolve(HANDLE_EXE));
    }
}
