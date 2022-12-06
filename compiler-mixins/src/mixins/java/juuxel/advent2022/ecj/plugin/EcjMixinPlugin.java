package juuxel.advent2022.ecj.plugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class EcjMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        switch (mixinClassName) {
            case "juuxel/advent2022/mixin/StatementMixin" -> {
                for (MethodNode method : targetClass.methods) {
                    if (method.name.equals("<init>")) {
                        method.access |= Opcodes.ACC_PUBLIC;
                    }
                }
            }

            case "juuxel/advent2022/mixin/GotoStatementMixin" -> {
                targetClass.superName = "org/eclipse/jdt/core/dom/Statement";
            }
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }
}
