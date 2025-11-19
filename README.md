# Better Boat Movement

**Jump your boat! Adds the ability to jump with boats and customize some boat behavior for a more dynamic water adventure.**  
Take control of your voyage with new boat-jumping mechanics and personalizable settings!

---

## ✨ Features

- **Boat Jumping**: Make your boat leap out of the water by pressing a key or by approaching obstacles.
- **Customizable Keybind**: Assign your preferred key for jumping with your boat.
- **Adjustable Behavior**: Fine-tune how boat jumping works to best fit your play style.
- **Multiplayer Friendly**: Works perfectly on both servers and singleplayer.

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
                "UNDER_FLOWING_WATER"
            ], // Sets the preferences under which states the boat should be boosted.
            "allowedSupportingBlocks": [], // Defines the blocks the boat has to lie on to be able to be boosted. If the list is empty, there is no restriction.
            "allowedCollidingBlocks": [], // Defines the blocks the boat has to collide with to be able to be boosted. If the list is empty, there is no restriction.
            "onlyForPlayers": true, // Toggles, whether a boat should only be boosted when carrying a player.
            "extraCollisionDetectionRange": 0.5 // Changes the detection range of a collision. Increasing this will boost a boat x blocks before actually touching the block it approaches. You may encounter weird behavior when changing this value to big numbers.
        },
        "keybind": {
            "allowJumpKeybind": true, // Toggles, whether a player should be able to jump with a boat via a keybind.
            "keybindJumpHeightMultiplier": 1.5, // Specifies the multiplier that will be applied to the jump height when a player uses the keybind to jump.
            "onlyKeybindJumpOnGroundOrWater": false // Decides whether you are allowed to jump midair by keybind or not. CAUTION: This option combined with a high keybind jump multiplier will result in extreme boosts as the boat will be boosted every tick you hold the keybind.
        }
    }
}
```

Changes require a server or game restart to take effect.

---

### ℹ️ Note on Multiplayer Safety

Prior to v1.1.1 it is possible to get flagged by anti-cheat on multiplayer servers.  
Please use v1.1.1 or higher to be on the safe side.

---

## 💬 Support & Feedback

- Open an [issue](https://github.com/btwonion/better-boat-movement/issues) for bugs or suggestions.
- Join our [Discord](https://nyon.dev/discord) for help and community.
