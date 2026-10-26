package com.xm666.realisticcruelty.handler;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class KeySet<T> {
    private final ResourceKey<Registry<T>> registryKey;
    private final ArrayList<Object> keys = new ArrayList<>();
    private final HashSet<T> values = new HashSet<>();
    private Registry<T> registry;

    public KeySet(ResourceKey<Registry<T>> registryKey) {
        this.registryKey = registryKey;
        NeoForge.EVENT_BUS.addListener(this::onTagsUpdated);
    }

    public void load(List<? extends String> strings) {
        keys.clear();
        for (var string : strings) {
            var key = KeyParser.parseKey(string);
            if (key == null) continue;

            if (key.isTag()) {
                keys.add(TagKey.create(registryKey, key.location()));
                continue;
            }

            keys.add(ResourceKey.create(registryKey, key.location()));
        }
        if (registry == null) return;

        update(registry);
    }

    private void update(Registry<T> registry) {
        values.clear();
        for (var key : keys) {
            if (key instanceof TagKey<?>) {
                var tagKey = (TagKey<T>) key;
                for (var value : registry.getTagOrEmpty(tagKey)) {
                    values.add(value.value());
                }
                continue;
            }

            var resourceKey = (ResourceKey<T>) key;
            values.add(registry.get(resourceKey));
        }
    }

    public boolean contains(T value) {
        return values.contains(value);
    }

    private void onTagsUpdated(TagsUpdatedEvent event) {
        var registryAccess = event.getRegistryAccess();
        var registry = registryAccess.registryOrThrow(registryKey);
        update(registry);
        this.registry = registry;
    }
}
