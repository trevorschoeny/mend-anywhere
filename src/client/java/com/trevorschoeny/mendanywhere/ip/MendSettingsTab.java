package com.trevorschoeny.mendanywhere.ip;

import com.trevorschoeny.mendanywhere.MendAnywhere;
import com.trevorschoeny.mendanywhere.MendAnywhereSettings;

import com.trevorschoeny.inventoryplus.api.InventoryPlusApi;

import com.trevlar.menukit.api.element.Button;
import com.trevlar.menukit.api.element.Checkbox;
import com.trevlar.menukit.api.element.Divider;
import com.trevlar.menukit.api.element.Flow;
import com.trevlar.menukit.api.element.PanelElement;
import com.trevlar.menukit.api.element.Tabs;
import com.trevlar.menukit.api.element.TextLabel;
import com.trevlar.menukit.api.element.TextRole;
import com.trevlar.menukit.api.element.Toggle;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * The Mend Anywhere tab in Inventory Plus's settings menu: the frame IP's tabs have (title,
 * description, Reset to Defaults, On/Off, a line), then the three container checkboxes,
 * grayed while the mod is off.
 *
 * <p>The frame is laid out by hand, as Pocket Ender Chest's tab is. Move it into MenuKit or
 * IP's API once more mods repeat it.
 */
public final class MendSettingsTab {

    private MendSettingsTab() {}

    private static final int LINE_COLOR = 0xFF8B8B8B;

    /** Call once from client init, before the menu can be opened. */
    public static void register() {
        Tabs.addTo(InventoryPlusApi.SETTINGS_MENU,
                Tabs.tab(MendAnywhere.MOD_ID).label(Component.literal("Mend Anywhere")).body(MendSettingsTab::body));
        // General's Reset everything resets this tab too.
        InventoryPlusApi.registerSettingsReset(MendAnywhereSettings::reset);
    }

    private static List<PanelElement> body() {
        return List.of(
                TextLabel.builder().at(0, 0)
                        .text(Component.literal("Mend Anywhere").withStyle(ChatFormatting.BOLD))
                        .role(TextRole.HEADING).build(),
                TextLabel.builder().at(0, 14)
                        .text(Component.literal("Mending repairs items from XP anywhere in your inventory, "
                                + "not just what you hold or wear."))
                        .role(TextRole.BODY).build(),
                Flow.builder().at(0, 30).gap(10, 4)
                        .add(Button.builder().label(Component.literal("Reset to Defaults")).size(0, 16)
                                .onClick(() -> InventoryPlusApi.confirmInSettings("Reset Mend Anywhere to defaults?",
                                        "Every setting on this tab goes back to how a fresh install has it.",
                                        MendAnywhereSettings::reset))
                                .build())
                        .add(Toggle.builder().size(40, 16)
                                .state(MendAnywhereSettings::enabled, MendAnywhereSettings::setEnabled)
                                .label(() -> Component.literal(MendAnywhereSettings.enabled() ? "On" : "Off")).build())
                        .build(),
                Divider.horizontal().at(0, 50).color(LINE_COLOR).build(),
                Checkbox.builder().at(0, 56).label(Component.literal("Mend items inside shulker boxes"))
                        .state(MendAnywhereSettings::inShulkers, MendAnywhereSettings::setInShulkers)
                        .disabledWhen(() -> !MendAnywhereSettings.enabled()).build(),
                Checkbox.builder().at(0, 72).label(Component.literal("Mend items inside bundles"))
                        .state(MendAnywhereSettings::inBundles, MendAnywhereSettings::setInBundles)
                        .disabledWhen(() -> !MendAnywhereSettings.enabled()).build(),
                Checkbox.builder().at(0, 88).label(Component.literal("Mend items inside your ender chest"))
                        .state(MendAnywhereSettings::inEnderChest, MendAnywhereSettings::setInEnderChest)
                        .disabledWhen(() -> !MendAnywhereSettings.enabled()).build());
    }
}
