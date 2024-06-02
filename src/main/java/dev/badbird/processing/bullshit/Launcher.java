package dev.badbird.processing.bullshit;

import dev.badbird.processing.Main;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Launcher {
    @SneakyThrows
    public static void launch(String main, String installPath, String pyJarPath) {
        String url = "file:///" + pyJarPath.replace(" ", "%20").replace("\\", "/"); // weird encoding stuff
        System.out.println("Launching with: " + url + " | " + installPath);
        URL[] urls = new URL[]{new URL(url)};
        JarLoader.addToClassPath(new File(installPath + "\\core\\library\\core.jar"));
        URLClassLoader classLoader = new MockClassLoader(urls, Main.class.getClassLoader());
        System.out.println("Running on java version: " + System.getProperty("java.version"));
        // call jycessing.Runner
        try {
            Class<?> runner = classLoader.loadClass("jycessing.Runner");
            List<String> ags = new ArrayList<>(List.of(main));
            runner.getMethod("main", String[].class).invoke(null, new Object[]{ags.toArray(new String[0])});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
