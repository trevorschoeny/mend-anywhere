package com.trevorschoeny.mendanywhere;

/**
 * Mend Anywhere's settings, in one place so only this class changes when ConfigKit
 * lands. Until then each setting is a plain field at its default. With ConfigKit it becomes:
 * <pre>
 * public static final Settings S = Settings.of("mendanywhere");
 * public static final Setting&lt;Boolean&gt; ENABLED = S.bool("enabled", true).server();
 * public static final Setting&lt;Boolean&gt; SHOW_BUTTON = S.bool("show_button", false);
 * </pre>
 */
public final class MendAnywhereSettings {

    private MendAnywhereSettings() {}

    // On/Off is the server's setting: it decides whether the inventory joins the
    // XP repair pool. Read on the server, at init.
    private static boolean enabled = true;

    // Container mending, all off by default: the inventory's own slots
    // already mend through the MENDING declarations, so these only widen the XP pool to
    // what's nested inside.
    private static boolean inShulkers = false;
    private static boolean inBundles = false;
    private static boolean inEnderChest = false;

    public static boolean enabled() { return enabled; }
    public static boolean inShulkers() { return inShulkers; }
    public static boolean inBundles() { return inBundles; }
    public static boolean inEnderChest() { return inEnderChest; }
}
