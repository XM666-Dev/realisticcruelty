package com.xm666.realisticcruelty.handler;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class KeyParser {
    public static Key parseKey(String string) {
        if (string == null || string.isBlank()) return null;

        var isTag = string.charAt(0) == '#';
        var location = ResourceLocation.tryParse(isTag ? string.substring(1) : string);
        if (location == null) return null;

        return new Key(isTag, location);
    }

    public static <T> ValueKey<T> parseValueKey(String string, Function<String, T> parser) {
        if (string == null || string.isBlank()) return null;

        var delimiter = string.indexOf(',', 1);
        if (delimiter == -1) return null;

        var isTag = string.charAt(0) == '#';
        var location = ResourceLocation.tryParse(string.substring(isTag ? 1 : 0, delimiter));
        if (location == null) return null;

        var value = parser.apply(string.substring(delimiter + 1));
        if (value == null) return null;

        return new ValueKey<>(isTag, location, value);
    }

    public static Integer parseInteger(String string) {
        try {
            return Integer.decode(string);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Integer parseItemId(String string) {
        var location = ResourceLocation.tryParse(string);
        if (location == null) return null;

        var item = BuiltInRegistries.ITEM.get(location);
        var id = BuiltInRegistries.ITEM.getId(item);
        if (id == -1) return null;

        return id;
    }

    public static boolean validateKey(Object object) {
        return object instanceof String string && parseKey(string) != null;
    }

    public static <T> boolean validateValueKey(Object object, Function<String, T> parser) {
        return object instanceof String string && parseValueKey(string, parser) != null;
    }

    public record Key(boolean isTag, ResourceLocation location) {
    }

    public record ValueKey<T>(boolean isTag, ResourceLocation location, T value) {
    }
}
