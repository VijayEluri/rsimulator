package org.rsimulator.core.util;

import com.google.inject.Singleton;
import org.rsimulator.core.config.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.rsimulator.core.config.Constants.REQUEST;

/**
 * FileUtilsImpl implements {@link FileUtils}.
 *
 * @author Magnus Bjuvensjö
 */
@Singleton
public class FileUtilsImpl implements FileUtils {
    private static final String ENCODING = "UTF-8";
    private static final String SUFFIX_PREFIX = new StringBuilder().append(REQUEST).append(".").toString();
    private Logger log = LoggerFactory.getLogger(FileUtilsImpl.class);

    public List<Path> findRequests(Path path, String extension) {
        List<Path> requests;

        try (Stream<Path> stream = Files.walk(path)) {
            Predicate<Path> predicate = p -> Files.isRegularFile(p) && p.toFile().getName().endsWith(new StringBuilder().append(SUFFIX_PREFIX).append(extension).toString());
            requests = stream.filter(predicate).collect(Collectors.toList());
            Collections.sort(requests, Comparator.comparing(Path::getNameCount).thenComparing(Path::compareTo)); // Do not want depth first as given by Files.walk
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.debug("Requests: {}", requests);
        return requests;
    }

    @Cache
    public String read(Path path) {
        try {
            return new String(Files.readAllBytes(path), ENCODING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
