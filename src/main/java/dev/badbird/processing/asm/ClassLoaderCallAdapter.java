package dev.badbird.processing.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class ClassLoaderCallAdapter extends ClassVisitor {
    public ClassLoaderCallAdapter(ClassWriter cw) {
        super(Opcodes.ASM9, cw);
    }

    // replace calls to ClassLoader.getSystemClassLoader() with getClass().getClassLoader()

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (!name.equals("addJarToClassLoader")) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
        System.out.println("Visiting method " + name);
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitor(Opcodes.ASM9, mv) {
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (opcode == Opcodes.INVOKESTATIC && owner.equals("java/lang/ClassLoader") && name.equals("getSystemClassLoader") && descriptor.equals("()Ljava/lang/ClassLoader;")) {
                    // Replace with getClass().getClassLoader()
                    System.out.println("Replacing call to ClassLoader.getSystemClassLoader() with getClass().getClassLoader()");
                    mv.visitVarInsn(Opcodes.ALOAD, 0); // load "this" onto the stack
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false); // call getClass()
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false); // call getClassLoader() on getClass()
                } else {
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }
            }
        };
    }
}
