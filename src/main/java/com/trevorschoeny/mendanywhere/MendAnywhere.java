package com.trevorschoeny.mendanywhere;

import com.trevlar.slotkit.api.group.SlotGroupCategory;
import com.trevlar.slotkit.api.item.ItemRef;
import com.trevlar.slotkit.api.item.ItemScope;
import com.trevlar.slotkit.api.item.ItemSources;
import com.trevlar.slotkit.api.slot.MendingCandidates;
import com.trevlar.menukit.api.panel.ModeScope;
import com.trevlar.slotkit.api.window.BehaviorKeys;
import com.trevlar.slotkit.api.window.SlotOperations;
import com.trevlar.slotkit.api.window.TriBool;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.ItemStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Mend Anywhere: XP repairs a damaged Mending item anywhere in the main inventory or
 * the hotbar, not only what the player holds or wears. Behind off-by-default switches,
 * it also reaches inside shulker boxes, bundles, and the ender chest.
 *
 * <p>The inventory and hotbar are two declarations. SlotKit already widens vanilla's XP
 * orb pool to every slot whose {@code MENDING} behaviour resolves true, so turning that
 * behaviour on for the two inventory categories is all it takes. The repair still uses
 * vanilla's own pick-one-item and XP math, one item per orb step, before the XP reaches
 * the level.
 *
 * <p>Declared by category, never by another mod's id, so any slot a mod adds in either
 * category is covered too (Hotbar Pockets' pockets are {@code PLAYER_INVENTORY}). A
 * group that declares {@code mending(false)} still wins, since a group outranks its
 * category.
 *
 * <p>Containers are a separate path: {@code MendingCandidates} hands SlotKit's XP orb
 * pool extra candidates found by {@code ItemSources.walk}. The walk already covers the
 * inventory's own slots, which mend through the {@code MENDING} declarations above, so
 * only what's nested, and the ender chest's own slots, are added here to avoid handing
 * the same item to the pool twice.
 *
 * <p>A declaration builds no slots, so there is no {@code slotkit} block in fabric.mod.json
 * and nobody is ever refused. A vanilla client on a server with this mod gets the wider
 * mending and just sees the durability change. The only client code is the settings tab
 * ({@code com.trevorschoeny.mendanywhere.ip}), which exists only with Inventory Plus
 * installed.
 */
public final class MendAnywhere implements ModInitializer {

    public static final String MOD_ID = "mendanywhere";
    static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MendAnywhereSettings.load();

        // Declared unconditionally so the category rung always exists; the On/Off setting
        // is enforced live by the veto below instead of by skipping the declaration, so
        // toggling it in the settings menu takes effect without a relaunch.
        SlotOperations.inherent(ModeScope.BOTH, SlotGroupCategory.PLAYER_INVENTORY, BehaviorKeys.MENDING, TriBool.TRUE);
        SlotOperations.inherent(ModeScope.BOTH, SlotGroupCategory.PLAYER_HOTBAR, BehaviorKeys.MENDING, TriBool.TRUE);
        SlotOperations.veto(ModeScope.BOTH, (slot, operation) ->
                operation == BehaviorKeys.MENDING && !MendAnywhereSettings.enabled()
                        && (slot.category() == SlotGroupCategory.PLAYER_INVENTORY
                                || slot.category() == SlotGroupCategory.PLAYER_HOTBAR));

        MendingCandidates.register(player -> {
            if (!MendAnywhereSettings.enabled()) return List.of();
            ItemScope scope = ItemScope.inventory()
                    .enderChest(MendAnywhereSettings.inEnderChest())
                    .shulkers(MendAnywhereSettings.inShulkers())
                    .bundles(MendAnywhereSettings.inBundles());
            List<MendingCandidates.Candidate> out = new ArrayList<>();
            ItemSources.walk(player, scope, ref -> {
                if (ref.depth() > 0 || ref.source() == ItemRef.Source.ENDER_CHEST) {
                    out.add(new MendingCandidates.Candidate() {
                        public ItemStack stack() { return ref.stack(); }
                        public void onRepaired() { ref.commit(); }
                    });
                }
                return true;
            });
            return out;
        });
    }
}
