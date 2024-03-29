# Better Boat Movement
> Adds quality of life improvements to boats

## Features
- configure step height avoid getting stuck in front of blocks, that are slightly higher
- configure player ejection time when player loses control of the boat
- boost boat upwards when underwater

## Configuration

<details>
<summary>/config/better-boat-movement.json</summary>

```json5
{
  "version": 1, // just ignore that, only for migrations
  "config": {
    "stepHeight": 0.3, // the height the boat should travel upwards
    "playerEjectTicks": 200.0, // defines the ticks that should pass before ejecting a player, when the player lost control over the boat
    "boostUnderwater": true, // toggles, whether a boat, which is underwater should be boosted upwards with half of the step height
    "wallHitCooldownTicks": 5, // defines the ticks that should pass before moving the boat up a block
    "onlyForPlayers": true // toggles, whether the boosts configured below should only work if the boat carries a player.
  }
}
```
</details>

## Note
Prior to v1.1.1 it is possible to get flagged by anti-cheat on multiplayer servers. 
Please use v1.1.1 or higher to be on the safe side.

### Other
⚠️ The development version is always the latest stable release of Minecraft.
Therefore, new features will only be available for the current and following Minecraft versions.

If you need help with any of my mods just join my [discord server](https://nyon.dev/discord)
