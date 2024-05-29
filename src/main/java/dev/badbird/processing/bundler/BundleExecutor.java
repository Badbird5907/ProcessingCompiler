package dev.badbird.processing.bundler;

import dev.badbird.processing.bullshit.Launcher;
import lombok.Data;
import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class BundleExecutor implements Runnable {
    private final InputStream zipFile;

    @SneakyThrows
    @Override
    public void run() {
        // extract zip file to temp
        File temp = extract();

        /*
        File bundle = new File(temp, "bundle.json");
        if (!bundle.exists()) {
            throw new IllegalStateException("bundle.json not found in bundle");
        }
        String json = new String(Files.readAllBytes(bundle.toPath()));
        BundleInfo info = Main.getGson().fromJson(json, BundleInfo.class);
         */
        File main = new File(temp, "_output.pyde");
        System.out.println("Launching: " + main.getAbsolutePath());
        if (!main.exists()) {
            throw new IllegalStateException("Main file not found in bundle");
        }
        Launcher.launch(main.getAbsolutePath());
    }

    @SneakyThrows
    private File extract() {
        String tempDir = System.getProperty("java.io.tmpdir");
        File temp = Files.createTempDirectory(Path.of(tempDir), "bundle").toFile();
        temp.deleteOnExit();
        File zip = new File(temp, "bundle.zip");
        FileOutputStream fos = new FileOutputStream(zip);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = zipFile.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }
        fos.close();
        ZipFile zipFile = new ZipFile(zip);
        File ababababab = new File(temp, "fml"); // fml totally stands for file module loader :)
        zipFile.extractAll(ababababab.getAbsolutePath());
        return ababababab;
    }
}
