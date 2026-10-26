package com.xm666.realisticcruelty.handler;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class KeyMap<T, V> {
    private final ResourceKey<Registry<T>> registryKey;
    private final ArrayList<Pair<Object, V>> keys = new ArrayList<>();
    private final HashMap<T, V> values = new HashMap<>();
    private Registry<T> registry;

    public KeyMap(ResourceKey<Registry<T>> registryKey) {
        this.registryKey = registryKey;
        NeoForge.EVENT_BUS.addListener(this::onTagsUpdated);
    }

    public void load(List<? extends String> strings, Function<String, V> parser) {
        keys.clear();
        for (var string : strings) {
            var key = KeyParser.parseValueKey(string, parser);
            if (key == null) continue;

            if (key.isTag()) {
                keys.add(new Pair<>(TagKey.create(registryKey, key.location()), key.value()));
                continue;
            }

            keys.add(new Pair<>(ResourceKey.create(registryKey, key.location()), key.value()));
        }
        if (registry == null) return;

        update(registry);
    }

    public void update(Registry<T> registry) {
        values.clear();
        for (var key : keys) {
            if (key.key instanceof TagKey<?>) {
                var tagKey = (TagKey<T>) key.key;
                for (var value : registry.getTagOrEmpty(tagKey)) {
                    values.put(value.value(), key.value);
                }
                continue;
            }

            var resourceKey = (ResourceKey<T>) key.key;
            values.put(registry.get(resourceKey), key.value);
        }
    }

    public V getOrDefault(T key, V defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    private void onTagsUpdated(TagsUpdatedEvent event) {
        var registryAccess = event.getRegistryAccess();
        var registry = registryAccess.registryOrThrow(registryKey);
        update(registry);
        this.registry = registry;
    }

    private record Pair<K, V>(K key, V value) {
    }
}
