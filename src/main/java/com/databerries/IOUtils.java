package com.databerries;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

class IOUtils {
    private static List<String> decode(byte[] content) {
        try {
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(content));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));

            String line;
            List<String> locations = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                locations.add(line);
            }
            return locations;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static List<String> readLines(String filename) throws URISyntaxException, IOException {
        InputStream resourceAsStream = Files.newInputStream(Paths.get(filename));
        if (filename.endsWith(".gz")) {
            return decode(org.apache.commons.io.IOUtils.toByteArray(resourceAsStream));
        }
        return org.apache.commons.io.IOUtils.readLines(resourceAsStream, "UTF-8");
    }
}
