# Mend Anywhere

Mend Anywhere lets Mending repair your gear from experience wherever it is in your inventory, not only what you're holding or wearing. A spare pickaxe in your hotbar or a sword deep in your inventory mends from the same orbs as your armor. You can also let it reach inside shulker boxes, bundles, and your ender chest.

It runs on the server. In singleplayer your game is the server, so installing it there is enough, and on a multiplayer server players don't need it.

### Features

#### Mending everywhere you carry it

- Items with Mending in your hotbar and main inventory mend from experience orbs, the same way your armor and held items do.
- Each orb picks one damaged item with Mending at random from everything it can reach, so worn gear and spares share the experience.
- Experience that doesn't go into a repair still reaches your experience bar.

#### Inside containers

- Items inside shulker boxes and bundles you carry can mend too, at any depth, and so can items in your ender chest.
- Each of these has its own setting, and all three are off by default.

#### With Inventory Plus

- The settings are on a Mend Anywhere tab in Inventory Plus's settings menu.

### Requirements

- [Fabric API](https://modrinth.com/mod/fabric-api)
- [SlotKit](https://modrinth.com/mod/slotkit) 2.2.0 or newer, below 3.0.0, which decides which slots Mending reaches and finds items inside shulker boxes and bundles.
- [MenuKit](https://modrinth.com/mod/menukit) 7.5.0 or newer, below 8.0.0, which SlotKit is built on and which draws the settings tab.

### Pairs well with

- [Inventory Plus](https://modrinth.com/mod/inventory-plus-trevlar), my mod for restocking, sorting, and locking slots. With it, Mend Anywhere gets a settings tab.
- [Hotbar Pockets](https://modrinth.com/mod/hotbar-pockets), my mod that adds extra slots behind your hotbar. Items in its pockets mend too.

### Compatibility

It runs on Minecraft 26.3 with Fabric. Install it on the server, or in singleplayer on your own game. Players joining a server with it don't need it, and nobody is turned away for not having it.

### How to use

There's nothing to set up. Pick up experience as usual.

- Each experience orb picks one damaged item with Mending at random from your armor, your hands, your hotbar, and your main inventory, and repairs it the way vanilla Mending does.
- Items without Mending, and items at full durability, aren't picked.
- Items in other mods' slots that count as part of your inventory, like Hotbar Pockets' pockets, are included.
- With the container settings on, damaged items with Mending inside the shulker boxes and bundles you carry, at any depth, and in your ender chest join the pick.

#### Settings

The settings are on the Mend Anywhere tab in Inventory Plus's settings menu. Without Inventory Plus there's no settings menu, and the defaults apply. They're saved in `config/mendanywhere/config.json`. On a multiplayer server, the server's own file decides.

| Setting on the Mend Anywhere tab | Default |
| --- | --- |
| Mend Anywhere (the switch) | On |
| Mend items inside shulker boxes | Off |
| Mend items inside bundles | Off |
| Mend items inside your ender chest | Off |

- Turning Mend Anywhere off puts Mending back to vanilla, and the three container settings are grayed out.
- Reset to Defaults asks first, then puts every setting on the tab back.

### License

MIT.
