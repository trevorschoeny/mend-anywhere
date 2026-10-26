package com.trevorschoeny.mendanywhere.checks;

import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import testgallery.SceneWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Checks for the standalone scene (test-gallery/scenes/mend-anywhere/standalone.json). */
public class MendingChecks implements FabricClientGameTest {

    private ClientGameTestContext ctx;
    private SceneWorld.Opened scene;
    private final List<String> failures = new ArrayList<>();

    @Override
    public void runTest(ClientGameTestContext ctx) {
        this.ctx = ctx;
        try (SceneWorld.Opened scene = SceneWorld.open(ctx, "mend-anywhere", "standalone")) {
            this.scene = scene;
            run("mendanywhere:main_inventory", this::mainInventory);
            run("mendanywhere:no_skip", this::noSkip);
            run("mendanywhere:no_mending", this::noMending);
            run("mendanywhere:hotbar_other_slot", this::hotbarOtherSlot);
            run("mendanywhere:armor", this::armor);
            run("mendanywhere:offhand", this::offhand);
        }
        if (!failures.isEmpty()) throw new AssertionError("mend anywhere checks failed:\n- " + String.join("\n- ", failures));
    }

    /** Runs one station's check, recording a failure instead of stopping. */
    private void run(String id, Runnable check) {
        JsonObject station = scene.station(id);
        try {
            scene.standAt(station, 30);
            command("clear @p");
            check.run();
        } catch (AssertionError e) {
            failures.add(id + ": " + e.getMessage());
        } finally {
            ctx.takeScreenshot("mend-anywhere-check-" + id.substring(id.indexOf(':') + 1));
        }
    }

    /** Station "mendanywhere:main_inventory": the scene's arrival pickaxe sits in inventory.0. */
    private void mainInventory() {
        giveXp(20);
        expect(inventory(0).getDamageValue() < 1000, "the main inventory's Mending pickaxe wasn't repaired");
    }

    /**
     * Station "mendanywhere:no_skip": a held Mending sword and an inventory Mending pickaxe,
     * both damaged. Several orbs should repair both, not spend every orb on just one.
     */
    private void noSkip() {
        command("item replace entity @p weapon.mainhand with minecraft:diamond_sword[damage=1000,enchantments={\"minecraft:mending\":1}]");
        command("item replace entity @p inventory.0 with minecraft:diamond_pickaxe[damage=1000,enchantments={\"minecraft:mending\":1}]");
        giveXp(10);   // ten orbs: enough that both items get picked at least once
        expect(mainHand().getDamageValue() < 1000, "the held Mending sword was skipped over several orbs");
        expect(inventory(0).getDamageValue() < 1000, "the inventory Mending pickaxe was skipped over several orbs");
    }

    /** Station "mendanywhere:no_mending": a damaged pickaxe with no Mending enchant doesn't repair. */
    private void noMending() {
        command("item replace entity @p inventory.0 with minecraft:diamond_pickaxe[damage=1000]");
        int before = onServer(p -> p.totalExperience);
        giveXp(1);
        expect(inventory(0).getDamageValue() == 1000, "a pickaxe without Mending was repaired from XP");
        expect(onServer(p -> p.totalExperience) > before, "the orb's XP didn't reach the player's level");
    }

    /**
     * Station "mendanywhere:hotbar_other_slot": a damaged Mending item in hotbar slot 1,
     * not the selected slot (0), repairs from XP.
     */
    private void hotbarOtherSlot() {
        command("item replace entity @p hotbar.1 with minecraft:diamond_pickaxe[damage=1000,enchantments={\"minecraft:mending\":1}]");
        giveXp(20);
        expect(hotbar(1).getDamageValue() < 1000, "the hotbar's Mending pickaxe (not the selected slot) wasn't repaired");
    }

    /**
     * Station "mendanywhere:armor": worn armor still repairs from XP. Vanilla's own
     * equipped-gear path, not one of Mend Anywhere's declarations; a sanity check that
     * widening the pool to the inventory and hotbar didn't regress it.
     */
    private void armor() {
        command("item replace entity @p armor.chest with minecraft:diamond_chestplate[damage=1000,enchantments={\"minecraft:mending\":1}]");
        giveXp(20);
        expect(armorChest().getDamageValue() < 1000, "the worn Mending chestplate wasn't repaired");
    }

    /**
     * Station "mendanywhere:offhand": an offhand Mending item still repairs from XP.
     * Vanilla's own equipped-gear path; a sanity check, as {@link #armor}.
     */
    private void offhand() {
        command("item replace entity @p weapon.offhand with minecraft:diamond_axe[damage=1000,enchantments={\"minecraft:mending\":1}]");
        giveXp(20);
        expect(offhandItem().getDamageValue() < 1000, "the offhand Mending axe wasn't repaired");
    }

    // Helpers

    /** Summons {@code count} orbs, one at a time so each gets a tick to be picked up. */
    private void giveXp(int count) {
        for (int i = 0; i < count; i++) {
            command("execute at @p run summon experience_orb ~ ~ ~ {Value:20}");
            ctx.waitTicks(5);
        }
    }

    private ItemStack mainHand() { return onServer(ServerPlayer::getMainHandItem); }

    /** The main inventory's own slot {@code n} (not the hotbar); inventory.0 is internal index 9. */
    private ItemStack inventory(int n) { return onServer(p -> p.getInventory().getItem(9 + n)); }

    /** Hotbar slot {@code n} (0-8); hotbar.0 is internal index 0. */
    private ItemStack hotbar(int n) { return onServer(p -> p.getInventory().getItem(n)); }

    private ItemStack armorChest() {
        return onServer(p -> p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST));
    }

    private ItemStack offhandItem() { return onServer(ServerPlayer::getOffhandItem); }

    private <T> T onServer(Function<ServerPlayer, T> f) {
        return scene.world().getServer().computeOnServer(s -> f.apply(s.getPlayerList().getPlayers().get(0)));
    }

    private void command(String c) {
        scene.world().getServer().runCommand(c);
        ctx.waitTicks(2);
    }

    private void expect(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
