package com.trevorschoeny.mendanywhere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Mend Anywhere's settings, the one place they live: plain fields, loaded from and saved to
 * {@code config/mendanywhere/config.json}, the same shape Pocket Ender Chest's settings use.
 * Every setter saves.
 *
 * <p>{@link #enabled} gates its slot declarations through a veto ({@link MendAnywhere}), so
 * toggling it takes effect immediately, not just on the next load. The container flags are
 * read live by {@code MendingCandidates}, so they always take effect immediately.
 *
 * <p>Changed from the Mend Anywhere tab in Inventory Plus's settings menu when IP is installed.
 * Without IP there's no menu and the defaults (or the file) apply.
 */
public final class MendAnywhereSettings {

    private MendAnywhereSettings() {}

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // The defaults, named once so load and reset agree.
    private static final boolean DEFAULT_ENABLED = true;
    private static final boolean DEFAULT_IN_SHULKERS = false;
    private static final boolean DEFAULT_IN_BUNDLES = false;
    private static final boolean DEFAULT_IN_ENDER_CHEST = false;

    // On/Off: whether the inventory and hotbar join the XP repair pool at all.
    private static volatile boolean enabled = DEFAULT_ENABLED;

    // Container mending, all off by default: the inventory's own slots already mend
    // through the MENDING declarations, so these only widen the XP pool to what's nested inside.
    private static volatile boolean inShulkers = DEFAULT_IN_SHULKERS;
    private static volatile boolean inBundles = DEFAULT_IN_BUNDLES;
    private static volatile boolean inEnderChest = DEFAULT_IN_ENDER_CHEST;

    public static boolean enabled() { return enabled; }
    public static boolean inShulkers() { return inShulkers; }
    public static boolean inBundles() { return inBundles; }
    public static boolean inEnderChest() { return inEnderChest; }

    public static void setEnabled(boolean v)      { enabled = v; save(); }
    public static void setInShulkers(boolean v)   { inShulkers = v; save(); }
    public static void setInBundles(boolean v)    { inBundles = v; save(); }
    public static void setInEnderChest(boolean v) { inEnderChest = v; save(); }

    /** Every setting back to its default, and saves: Reset to Defaults and IP's Reset everything. */
    public static void reset() {
        enabled = DEFAULT_ENABLED;
        inShulkers = DEFAULT_IN_SHULKERS;
        inBundles = DEFAULT_IN_BUNDLES;
        inEnderChest = DEFAULT_IN_ENDER_CHEST;
        save();
    }

    private static Path filePath() {
        return FabricLoader.getInstance().getConfigDir().resolve(MendAnywhere.MOD_ID).resolve("config.json");
    }

    /**
     * Reads the file once, at common init. A missing file, or a missing or unreadable key,
     * leaves that setting at its default. Nothing is written until a setting changes.
     */
    public static void load() {
        Path path = filePath();
        if (!Files.exists(path)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            if (root.has("enabled")) enabled = root.get("enabled").getAsBoolean();
            if (root.has("inShulkers")) inShulkers = root.get("inShulkers").getAsBoolean();
            if (root.has("inBundles")) inBundles = root.get("inBundles").getAsBoolean();
            if (root.has("inEnderChest")) inEnderChest = root.get("inEnderChest").getAsBoolean();
        } catch (IOException | JsonSyntaxException | IllegalStateException | ClassCastException
                 | UnsupportedOperationException ex) {
            MendAnywhere.LOGGER.error("[mendanywhere] failed to read {}, using defaults", path, ex);
        }
    }

    private static void save() {
        Path path = filePath();
        try {
            Files.createDirectories(path.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("enabled", enabled);
            root.addProperty("inShulkers", inShulkers);
            root.addProperty("inBundles", inBundles);
            root.addProperty("inEnderChest", inEnderChest);
            Files.writeString(path, GSON.toJson(root));
        } catch (IOException ex) {
            MendAnywhere.LOGGER.error("[mendanywhere] failed to write {}, settings won't persist", path, ex);
        }
    }
}
