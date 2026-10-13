package com.trevorschoeny.mendanywhere;

import com.trevlar.slotkit.api.group.SlotGroupCategory;
import com.trevlar.slotkit.api.window.BehaviorKeys;
import com.trevlar.slotkit.api.window.SlotOperations;
import com.trevlar.slotkit.api.window.TriBool;

import net.fabricmc.api.ModInitializer;

/**
 * Mend Anywhere: XP repairs a damaged Mending item anywhere in the main inventory or
 * the hotbar, not only what the player holds or wears.
 *
 * <p>The whole mod is two declarations. SlotKit already widens vanilla's XP orb pool to
 * every slot whose {@code MENDING} behaviour resolves true, so turning that behaviour on
 * for the two inventory categories is all it takes. The repair still uses vanilla's own
 * pick-one-item and XP math, one item per orb step, before the XP reaches the level.
 *
 * <p>Declared by category, never by another mod's id, so any slot a mod adds in either
 * category is covered too (Hotbar Pockets' pockets are {@code PLAYER_INVENTORY}). A
 * group that declares {@code mending(false)} still wins, since a group outranks its
 * category.
 *
 * <p>Server side only: a declaration builds no slots, so there is no {@code slotkit}
 * block in fabric.mod.json and nobody is ever refused. A vanilla client on a server
 * with this mod gets the wider mending and just sees the durability change.
 */
public final class MendAnywhere implements ModInitializer {

    public static final String MOD_ID = "mendanywhere";

    @Override
    public void onInitialize() {
        // Off leaves the pool exactly as vanilla's. ponytail: read once at init; a live
        // toggle needs ConfigKit's server setting, so it comes with that move.
        if (!MendAnywhereSettings.enabled()) return;
        SlotOperations.inherent(SlotGroupCategory.PLAYER_INVENTORY, BehaviorKeys.MENDING, TriBool.TRUE);
        SlotOperations.inherent(SlotGroupCategory.PLAYER_HOTBAR, BehaviorKeys.MENDING, TriBool.TRUE);
    }
}
