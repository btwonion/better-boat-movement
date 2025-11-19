- restructure config (version 7):
  - new structure: 
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