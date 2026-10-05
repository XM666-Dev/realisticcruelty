package com.xm666.realisticcruelty;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.mclanguageprovider.MinecraftModContainer;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
        var container = new MinecraftModContainer(FMLLoader.getLoadingModList().getModFileById(RealisticCruelty.MODID).getMods().get(0));
        Config.registerConfig(ModConfig.Type.CLIENT, MixinConfig.SPEC, container, "startup");
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var toIndex = mixinClassName.lastIndexOf('$');
        var fromIndex = Math.max(
                mixinClassName.lastIndexOf('.', toIndex - 1),
                mixinClassName.lastIndexOf('$', toIndex - 1)
        ) + 1;
        var path = mixinClassName.substring(fromIndex, toIndex);
        path = StringUtils.removeEnd(path, "Mixin");
        path = StringUtils.uncapitalize(path);
        path += "Enabled";
        var value = MixinConfig.SPEC.getValues().<ForgeConfigSpec.BooleanValue>get(path);
        return value == null || value.get();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
