# Better Boat Movement

**Jump your boat! Adds the ability to jump with boats and customize some boat behavior for a more dynamic water adventure.**  
Take control of your voyage with new boat-jumping mechanics and personalizable settings!

---

## ✨ Features

- 🦘 **Boat Jumping**: Make your boat leap out of the water by pressing a key or by approaching obstacles.
- 🗝 **Customizable Keybind**: Assign your preferred key for jumping with your boat.
- ⚙️ **Adjustable Behavior**: Fine-tune how boat jumping works to best fit your play style.
- 🌊 **Multiplayer Friendly**: Works perfectly on both servers and singleplayer.

---

## 🎬 Demo - See the boat jump

*Settings: stepHeight = 0.4, extraCollisionDetectionRange = 2.0*
![Ice Boating preview](https://raw.githubusercontent.com/btwonion/better-boat-movement/refs/heads/master/media/bbm-ice-boating.webp)

---

## ⚙️ Configuration

Configure your boat-jumping experience via `/config/better-boat-movement.json`:

```json5
{
    "version": 5, // just ignore that, only for migrations
    "config": {
        "stepHeight": 0.35, // The amount of blocks you are going to be boosted when triggering a boost
        "playerEjectTicks": 200.0, // The ticks the game waits before kicking you out of a boat after the player lost control
        "boostUnderwater": true, // Toggles, whether a boat, which is underwater should be boosted upwards
        "boostOnBlocks": true, // Toggles, whether a boat, which is on a block should be boosted upwards when running against an elevation
        "boostOnIce": false, // Toggles, whether a boat, should only be boosted on blocks, when laying on an ice block.
        "boostOnWater": true, // Toggles, whether a boat, which is on water should be boosted upwards when floating against an elevation
        "onlyForPlayers": true, // Toggles, whether a boat should only be boosted when carrying a player
        "extraCollisionDetectionRange": 0.5, // Changes the detection range of a collision. Increasing this will boost a boat x blocks before actually touching the block it approaches.
        "allowJumpKeybind": false, // Toggles, whether a player should be able to jump with a boat via a keybind
        "keybindJumpHeightMultiplier": 1.5, // Specifies the multiplier that will be applied to the jump height when a player uses the keybind to jump
        "onlyKeybindJumpOnGroundOrWater": false // Decides whether you are allowed to jump mid-air by keybind or not.
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
