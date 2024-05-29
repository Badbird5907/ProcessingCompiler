package dev.badbird.processing.bundler;

import com.google.common.io.ByteSource;
import dev.badbird.processing.Main;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class Bundler {
    private final Path mainPath;
    private final File out;

    @SneakyThrows
    public void bundle() {
        File zip = zip();
        inject(zip);
    }

    @SneakyThrows
    public File zip() {
        File main = mainPath.toFile();
        System.out.println("Bundling: " + main);
        File parent = main.getParentFile();
        File[] files = parent.listFiles();
        if (files == null) {
            return null;
        }
        // add all files that do not end with .py to a zip file, recursively
        File bundle = Files.createTempFile("bundle", ".zip").toFile();
        bundle.deleteOnExit();
        try (ZipFile zipFile = new ZipFile(bundle)) {
            List<File> list = Arrays.stream(files).filter(file -> !file.getName().endsWith(".py") && !file.getName().endsWith(".zip") && !file.getName().endsWith(".jar")).toList();
            zipFile.addFiles(list);
            BundleInfo bundleInfo = new BundleInfo(main.getName());
            byte[] bytes = Main.getGson().toJson(bundleInfo).getBytes();
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setFileNameInZip("bundle.json");
            zipFile.addStream(ByteSource.wrap(bytes).openBufferedStream(), zipParameters);
        }
        return bundle;
    }

    @SneakyThrows
    public void inject(File zip) {
        // get the jar file of the main class
        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        // copy the jar file to a temp file
        if (out.exists()) {
            out.delete();
        }
        Files.copy(jarFile.toPath(), out.toPath());
        // add the zip file to the jar file
        ZipFile zipFile = new ZipFile(out);
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setFileNameInZip("bundle.zip");
        zipFile.addFile(zip, zipParameters);
    }
}
