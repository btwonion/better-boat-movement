# Better Boat Movement

> Adds quality of life improvements to boats

## Features

- configure step height to avoid getting stuck in front of blocks, that are slightly higher
- configure player ejection time when player loses control of the boat
- boost boat upwards when underwater

## Configuration

<details>
<summary>/config/better-boat-movement.json</summary>

```json5
{
    "version": 4, // just ignore that, only for migrations
    "config": {
        "stepHeight": 0.35, // The amount of blocks you are going to be boosted when triggering a boost
        "playerEjectTicks": 200.0, // The ticks the game waits before kicking you out of a boat after the player lost control
        "boostUnderwater": true, // Toggles, whether a boat, which is underwater should be boosted upwards
        "boostOnBlocks": false, // Toggles, whether a boat, which is on a block should be boosted upwards when running against an elevation
        "boostOnIce": false, // Toggles, whether a boat, should only be boosted on blocks, when laying on an ice block.
        "boostOnWater": true, // Toggles, whether a boat, which is on water should be boosted upwards when floating against an elevation
        "onlyForPlayers": true, // Toggles, whether a boat should only be boosted when carrying a player,
        "extraCollisionDetectionRange": 0.5 // Changes the detection range of a collision. Increasing this will boost a boat x blocks before actually touching the block it approaches.
    }
}
```

</details>

## Note

Prior to v1.1.1 it is possible to get flagged by anti-cheat on multiplayer servers.
Please use v1.1.1 or higher to be on the safe side.

### Other

If you need help with any of my mods just join my [discord server](https://nyon.dev/discord).
