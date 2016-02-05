package org.diylc.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BootUtils {

    public static String findJavaExecutable(String javaHome) {
        List<Path> javaExecutables = new ArrayList<>();

        try (Stream<Path> stream = Files.find(Paths.get(javaHome, "bin"), 1, (path, attr) -> {
            File file = path.toFile();
            return file.getName().startsWith("java") && file.canExecute() && file.isFile();
        })) {
            javaExecutables = stream.collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("No Java VM executables found in " + javaHome);
        }

        String javaExecutable = null;

        if (!javaExecutables.isEmpty()) {
            javaExecutable = javaExecutables.get(0).toString();
        }

        if (javaExecutables.size() > 1) {
            for (Path path : javaExecutables) {
                if (path.toFile().getName().startsWith("javaw.")) {
                    javaExecutable = path.toString();
                }
            }
        }

        return javaExecutable;
    }

    public static void installRestarer(Runnable restarter) {
        Runtime.getRuntime().addShutdownHook(new Thread(restarter));
    }
}
