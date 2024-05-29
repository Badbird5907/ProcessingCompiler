package dev.badbird.processing.bullshit;

import java.net.URL;
import java.net.URLClassLoader;

public class MockClassLoader extends URLClassLoader {
    public MockClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public void addURL(URL url) {
        // System.out.println("[HACK] Adding " + url + " to classpath");
        JarLoader.addToClassPath(url);
    }
}
