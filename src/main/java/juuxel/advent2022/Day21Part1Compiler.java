package juuxel.advent2022;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class Day21Part1Compiler {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java Day21Part1Compiler <file>");
            return;
        }

        Map<String, Day21.Monkey> monkeys = new HashMap<>();
        for (String line : Files.readAllLines(Path.of(args[0]))) {
            Day21.read(line, monkeys::put);
        }

        ClassNode node = compile(monkeys);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        node.accept(cw);
        Files.write(Path.of("Day21.class"), cw.toByteArray());
    }

    private static ClassNode compile(Map<String, Day21.Monkey> monkeys) {
        ClassNode node = new ClassNode();
        node.name = "Day21";
        node.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | Opcodes.ACC_FINAL;
        node.superName = "java/lang/Object";
        node.version = Opcodes.V1_8;
        node.methods = new ArrayList<>();

        MethodNode main = new MethodNode();
        main.name = "main";
        main.desc = "([Ljava/lang/String;)V";
        main.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
        main.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        putMonkey(monkeys.get("root"), monkeys::get, main);
        main.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V"));
        main.instructions.add(new InsnNode(Opcodes.RETURN));
        node.methods.add(main);

        return node;
    }

    private static void putMonkey(Day21.Monkey monkey, Function<String, Day21.Monkey> getter, MethodVisitor mv) {
        if (monkey instanceof Day21.MathMonkey mm) {
            putMonkey(getter.apply(mm.a()), getter, mv);
            putMonkey(getter.apply(mm.b()), getter, mv);
            mv.visitInsn(getOpcode(mm.operator()));
        } else {
            mv.visitLdcInsn(((Day21.ConstantMonkey) monkey).value());
        }
    }

    private static int getOpcode(Day21.Operator operator) {
        return switch (operator) {
            case ADD -> Opcodes.LADD;
            case SUBTRACT -> Opcodes.LSUB;
            case MULTIPLY -> Opcodes.LMUL;
            case DIVIDE -> Opcodes.LDIV;
        };
    }
}
