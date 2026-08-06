# Better Boat Movement

**Jump your boat! Adds the ability to jump with boats and customize some boat behavior for a more dynamic water adventure.**  
Take control of your voyage with new boat-jumping mechanics and personalizable settings!

---

## ✨ Features

- **Obstacle-Aware Jumping**: Boats automatically jump reachable obstacles using their actual collision shapes instead of trying to boost over walls that are too tall.
- **Optional Manual Jumping**: Enable the jump keybind to jump once per keypress; it defaults to <kbd>H</kbd> and can be reassigned.
- **Adjustable Behavior**: Fine-tune how boat jumping works to best fit your play style.
- **Multiplayer Friendly**: Servers validate movement and synchronize their gameplay settings to connected players.

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
        "stepHeight": 0.35, // Sets the upward velocity applied by an automatic boost.
        "playerEjectTicks": 200.0, // Sets how many ticks the game waits before ejecting a player who has lost control of a boat.
        "boosting": {
            "boostStates": [
                "UNDER_WATER",
                "ON_LAND",
                "IN_WATER",
                "UNDER_FLOWING_WATER"
            ], // Sets the preferences under which states the boat should be boosted.
            "allowedSupportingBlocks": [], // Restricts boosts to boats resting on these block IDs or #tags. An empty list allows every block.
            "allowedCollidingBlocks": [], // Restricts boosts to the nearest block ID or #tag hit by the boat's path. An empty list allows every block.
            "onlyForPlayers": true, // Restricts automatic boosts to boats carrying a player.
            "extraCollisionDetectionRange": 0.5, // Looks this many blocks farther along the boat's path so it can boost before contact.
            "heightTolerance": 0.25 // Allows an obstacle to exceed the boat's predicted rise by this many blocks and still trigger a boost.
        },
        "keybind": {
            "allowJumpKeybind": false, // Enables manual boat jumping with the configurable keybind.
            "keybindJumpHeightMultiplier": 1.2, // Specifies the multiplier applied to stepHeight for a manual jump.
            "onlyKeybindJumpOnGroundOrWater": true // Prevents manual jumping while the boat is in the air.
        }
    }
}
```

Changes made in the local or singleplayer configuration screen apply when saved. A remote server's gameplay configuration is shown read-only and is authoritative. Dedicated-server file changes require a restart.

## Detailed behavior

For the precise automatic-boost, collision, manual-jump, and multiplayer authority rules, see the [gameplay behavior reference](https://raw.githubusercontent.com/btwonion/better-boat-movement/refs/heads/master/docs/behavior.md).

---

### ℹ️ Note on Multiplayer Safety

Prior to v1.1.1 it is possible to get flagged by anti-cheat on multiplayer servers.
Please use v1.1.1 or higher to be on the safe side.

---

## 💬 Support & Feedback

- Open an [issue](https://github.com/btwonion/better-boat-movement/issues) for bugs or suggestions.
- Join our [Discord](https://nyon.dev/discord) for help and community.
