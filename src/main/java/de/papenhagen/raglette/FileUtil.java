package de.papenhagen.raglette;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Slf4j
public class FileUtil {

    public static List<String> readLocalDocuments() {
        final List<String> hashes = new ArrayList<>();
        final List<String> fileNames = new ArrayList<>();
        final File[] files = new File("/documents/").listFiles();
        final File hashList = new File("/documents/list.txt");

        if (isNull(files)) {
            log.error("no Files found");
            return fileNames;
        }
        //reading the hashes into the list
        try (final Stream<String> stream = Files.lines(hashList.toPath())) {
            stream.forEach(hashes::add);
        } catch (IOException e) {
            log.error("Error on reading hashes of the file list");
        }

        for (final File file : files) {
            if (file.isFile() && file.getName().endsWith(".pdf")) {
                fileNames.add(file.getAbsolutePath());

                final String hash = generateHash(file);
                if (!hashes.contains(hash)) {
                    hashes.add(hash);
                }
            }
        }
        //write back the hashes to the list
        try {
            final FileWriter fileWriter = new FileWriter(hashList);
            for (String hash : hashes) {
                fileWriter.write(hash);
            }
            fileWriter.close();
        } catch (IOException e) {
            log.error("Error on writing hashes of the file list");
        }
        return fileNames;
    }

    public static String generateHash(final File file) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(file.getName().getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, crypt.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error on generation of SHA-1 of file: " + file.getName());
        }

        return "no";
    }
}
