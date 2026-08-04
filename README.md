# Better Boat Movement

**Jump your boat! Adds the ability to jump with boats and customize some boat behavior for a more dynamic water adventure.**  
Take control of your voyage with new boat-jumping mechanics and personalizable settings!

---

## ✨ Features

- **Boat Jumping**: Make your boat leap out of the water by pressing a key or by approaching obstacles.
- **Customizable Keybind**: Assign your preferred key for jumping with your boat.
- **Adjustable Behavior**: Fine-tune how boat jumping works to best fit your play style.
- **Multiplayer Friendly**: Works perfectly on both servers and singleplayer.

Automatic boosts are evaluated once per authoritative boat tick. A boat boosts when it has a normal horizontal collision, or when its forward swept volume reaches a block within `extraCollisionDetectionRange`. The probe follows horizontal motion (falling back to boat facing at very low speed), so blocks beside or behind the boat do not trigger it. A range of `0` uses only Minecraft's normal horizontal-collision trigger.

Block filters accept block IDs such as `minecraft:stone` and tags such as `#minecraft:ice`. An empty list means unrestricted. Invalid or unknown entries are ignored with a warning rather than crashing startup.

Manual jump is one boost per keypress. It applies only to the local player's controlled boat; the server validates the boat, controlling passenger, gameplay state, and server configuration before accepting the request. The client predicts an accepted jump for responsiveness, while the server remains authoritative.

---

## 🎬 Demo - See the boat jump

*Settings: stepHeight = 0.4, extraCollisionDetectionRange = 2.0*
![Ice Boating preview](https://raw.githubusercontent.com/btwonion/better-boat-movement/refs/heads/master/media/bbm-ice-boating.webp)

---

## ⚙️ Configuration

Configure your boat-jumping experience via `/config/better-boat-movement.json`:

```json5
{
    "version": 7, // just ignore this, this is for migration purposes only
    "config": {
        "stepHeight": 0.35, // The number of blocks you are going to be boosted when triggering a boost.
        "playerEjectTicks": 200.0, // The ticks the game waits before kicking you out of a boat after the player lost control.
        "boosting": {
            "boostStates": [
                "UNDER_WATER",
                "ON_LAND",
                "IN_WATER",
                "UNDER_FLOWING_WATER"
            ], // Sets the preferences under which states the boat should be boosted.
            "allowedSupportingBlocks": [], // Defines the blocks the boat has to lie on to be able to be boosted. If the list is empty, there is no restriction.
            "allowedCollidingBlocks": [], // Defines the blocks the boat has to collide with to be able to be boosted. If the list is empty, there is no restriction.
            "onlyForPlayers": true, // Toggles, whether a boat should only be boosted when carrying a player.
            "extraCollisionDetectionRange": 0.5, // Changes the detection range of a collision. Increasing this will boost a boat x blocks before actually touching the block it approaches. You may encounter weird behavior when changing this value to big numbers.
            "heightTolerance": 0.25 // Allows automatic jumps when an obstacle is slightly higher than stepHeight.
        },
        "keybind": {
            "allowJumpKeybind": false, // Toggles whether a player can jump with a boat via the keybind.
            "keybindJumpHeightMultiplier": 1.2, // Specifies the multiplier applied to stepHeight for a manual jump.
            "onlyKeybindJumpOnGroundOrWater": true // Restricts manual jumping to ground or water.
        }
    }
}
```

Changes made in the local or singleplayer configuration screen apply when saved. A remote server's gameplay configuration is shown read-only and is authoritative. Dedicated-server file changes require a restart.

## Detailed behavior

For the precise automatic-boost, collision, manual-jump, and multiplayer authority rules, see the [gameplay behavior reference](docs/behavior.md).

## Supported versions

The main development matrix currently supports Minecraft 26.1 on Fabric/Quilt and NeoForge. Minecraft 1.21.1 is not maintained by this branch; a fix for an older line must be released from its corresponding maintenance branch.

---

### ℹ️ Note on Multiplayer Safety

Prior to v1.1.1 it is possible to get flagged by anti-cheat on multiplayer servers.  
Please use v1.1.1 or higher to be on the safe side.

---

## 💬 Support & Feedback

- Open an [issue](https://github.com/btwonion/better-boat-movement/issues) for bugs or suggestions.
- Join our [Discord](https://nyon.dev/discord) for help and community.
