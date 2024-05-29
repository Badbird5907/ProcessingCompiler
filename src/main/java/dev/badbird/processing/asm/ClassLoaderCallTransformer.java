package dev.badbird.processing.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class ClassLoaderCallTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.equals("jycessing/LibraryImporter")) {
            System.out.println("Transforming " + className);
            // replace calls to ClassLoader.getSystemClassLoader() with getClass().getClassLoader() using ASM
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, 0);
            ClassLoaderCallAdapter adapter = new ClassLoaderCallAdapter(cw);
            cr.accept(adapter, 0);
            classfileBuffer = cw.toByteArray();
        }
        return classfileBuffer;
    }
}
