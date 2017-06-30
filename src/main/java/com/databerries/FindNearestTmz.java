package com.databerries;

import com.databerries.tree.KdTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.databerries.IOUtils.readLines;

public class FindNearestTmz {
    private static final Logger LOG = LoggerFactory.getLogger(FindNearestTmz.class);

    private static final String TIMEZONE_UNDEFINED = "null";

    public static void main(String[] args) throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        if (args.length != 2) {
            LOG.error("java -jar nearest-tz.jar source_file.{csv|gz} threshold_in_km");
            return;
        }
        String inputFile = args[0];
        int maxDistanceInKm = Integer.parseInt(args[1]);

        LOG.info("Reading file");
        final List<String> lines = readLines(inputFile);

        LOG.info("Feeding kdtree");
        final KdTree<Location> kdTree = new KdTree<>(filterUndefinedLocation(lines));

        int numberOfBatch = Runtime.getRuntime().availableProcessors();
        int batchSize = lines.size() / numberOfBatch;
        LOG.info("Number of job : {}, number of element / job {}.", numberOfBatch, batchSize);
        ExecutorService executorService = Executors.newFixedThreadPool(batchSize);

        List<Future<List<String>>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfBatch; i++) {
            int from = i * batchSize;
            int to = from + batchSize;
            futures.add(executorService.submit(() -> {
                List<String> lineProcessed = new ArrayList<>(batchSize);
                for (int j = from; j < to; j++) {
                    String line = lines.get(j);
                    if (!hasTimezone(line)) {
                        String[] splitLine = line.split(",");
                        List<KdTree.XYZDistancePoint> locations = kdTree.nearestNeighbourSearch(1, Location.create(Double.valueOf(splitLine[0]), Double.valueOf(splitLine[1]), ""));
                        if (!locations.isEmpty()) {
                            KdTree.XYZDistancePoint location = locations.get(0);
                            if (location.getDistance() < maxDistanceInKm) {
                                lineProcessed.add(line.replaceFirst("null", ((Location) location.getPoint()).getTimezone()));
                            }
                        }
                    } else {
                        lineProcessed.add(line);
                    }
                }
                return lineProcessed;
            }));
        }
        LOG.info("waiting job done {}.", futures.size());
        Path parentDirectory = Paths.get(inputFile).getParent();
        LOG.info("output directory {}.", parentDirectory);
        for (int i = 0; i < futures.size(); i++) {
            Path outputPath = Paths.get(parentDirectory.toString(), "output_" + i);
            Files.write(outputPath,
                    futures.get(i).get(),
                    Charset.defaultCharset());
            LOG.info("Task {} done , data dumped in file : {}.", i, outputPath);
        }
        executorService.shutdown();
        LOG.info("DONE");
    }

    private static Location strToLocation(String e) {
        String[] coordinates = e.split(",");
        return Location.create(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]), coordinates[2]);
    }

    private static List<Location> filterUndefinedLocation(List<String> lines) {
        return lines.stream()
                .filter(FindNearestTmz::hasTimezone)
                .map(FindNearestTmz::strToLocation)
                .collect(Collectors.toList());
    }

    private static boolean hasTimezone(String timezone) {
        return !timezone.contains(TIMEZONE_UNDEFINED);
    }
}
