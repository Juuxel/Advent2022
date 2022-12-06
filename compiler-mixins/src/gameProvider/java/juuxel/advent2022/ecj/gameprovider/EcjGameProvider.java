package juuxel.advent2022.ecj.gameprovider;

import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.util.Arguments;

import java.io.PrintWriter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

// fabric.skipMcProvider
public final class EcjGameProvider implements GameProvider {
    private Arguments arguments;

    @Override
    public String getGameId() {
        return "ecj";
    }

    @Override
    public String getGameName() {
        return "Eclipse Compiler for Java";
    }

    @Override
    public String getRawGameVersion() {
        return "1.0.0";
    }

    @Override
    public String getNormalizedGameVersion() {
        return "1.0.0";
    }

    @Override
    public Collection<BuiltinMod> getBuiltinMods() {
        return Set.of();
    }

    @Override
    public String getEntrypoint() {
        return "unused";
    }

    @Override
    public Path getLaunchDirectory() {
        return Path.of(".");
    }

    @Override
    public boolean isObfuscated() {
        return false;
    }

    @Override
    public boolean requiresUrlClassLoader() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean locateGame(FabricLauncher launcher, String[] args) {
        arguments = new Arguments();
        arguments.parse(args);
        return true;
    }

    @Override
    public void initialize(FabricLauncher launcher) {
    }

    @Override
    public GameTransformer getEntrypointTransformer() {
        return new GameTransformer();
    }

    @Override
    public void unlockClassPath(FabricLauncher launcher) {
    }

    @Override
    public void launch(ClassLoader loader) {
        Class<?> batchCompiler;
        Class<?> compilationProgress;
        try {
            batchCompiler = loader.loadClass("org.eclipse.jdt.core.compiler.batch.BatchCompiler");
            compilationProgress = loader.loadClass("org.eclipse.jdt.core.compiler.CompilationProgress");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load classes", e);
        }

        MethodHandle compile;
        try {
            MethodType methodType = MethodType.methodType(boolean.class, String[].class, PrintWriter.class, PrintWriter.class, compilationProgress);
            compile = MethodHandles.lookup().findStatic(batchCompiler, "compile", methodType);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not locate BatchCompiler.compile", e);
        }

        try {
            compile.invokeExact(arguments.toArray(), new PrintWriter(System.out), new PrintWriter(System.err), null);
        } catch (Throwable t) {
            throw new RuntimeException("ECJ has crashed!", t);
        }
    }

    @Override
    public Arguments getArguments() {
        return arguments;
    }

    @Override
    public String[] getLaunchArguments(boolean sanitize) {
        return arguments.toArray();
    }
}
