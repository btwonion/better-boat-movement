# Better Boat Movement

> Improves (ice) boating by allowing you to step up blocks

## Features

**Ice Boating preview**
*Settings: stepHeight = 0.4, extraCollisionDetectionRange = 2.0*
![Ice Boating preview](https://raw.githubusercontent.com/btwonion/better-boat-movement/refs/heads/master/media/bbm-ice-boating.webp)

- change boat stepHeight to move up blocks
- change collision detection range to step up blocks without getting stopped at high speeds
- use hotkey to manually jump with the boat (by default: `h`)

## Configuration

<details>
<summary>/config/better-boat-movement.json</summary>

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
        "keybindJumpHeightMultiplier": 1.5 // Specifies the multiplier that will be applied to the jump height when a player uses the keybind to jump
    }
}
```

</details>

## Note

Prior to v1.1.1 it is possible to get flagged by anti-cheat on multiplayer servers.
Please use v1.1.1 or higher to be on the safe side.

### Other

If you need help with any of my mods, join my [discord server](https://nyon.dev/discord).
