package com.xm666.realisticcruelty.handler;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.HashSet;

public record TagObject<T>(HashSet<ResourceKey<T>> resourceKeys, HashSet<TagKey<T>> tagKeys) {
    public TagObject() {
        this(new HashSet<>(), new HashSet<>());
    }

    public void add(String string, ResourceKey<? extends Registry<T>> registry) {
        var isTag = string.charAt(0) == '#';
        var location = new ResourceLocation(isTag ? string.substring(1) : string);
        if (isTag) {
            tagKeys.add(TagKey.create(registry, location));
        } else {
            resourceKeys.add(ResourceKey.create(registry, location));
        }
    }

    public void clear() {
        resourceKeys.clear();
        tagKeys.clear();
    }

    public boolean is(Holder<T> holder) {
        for (var resourceKey : resourceKeys) {
            if (holder.is(resourceKey)) return true;
        }

        for (var tagKey : tagKeys) {
            if (holder.is(tagKey)) return true;
        }

        return false;
    }
}
