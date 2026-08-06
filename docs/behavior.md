# Gameplay semantics

This document is the behavioral contract for movement and networking changes.

## Automatic boost

- The controller evaluates an authoritative boat once from `AbstractBoat.floatBoat` on each logical-side tick.
- A trigger is either Minecraft's normal `horizontalCollision`, or a block collision returned by the forward obstacle probe when `extraCollisionDetectionRange` is greater than zero.
- At range zero, only `horizontalCollision` can trigger. The probe may still identify the contacted block for filtering, but cannot create an early trigger.
- The probe sweeps the current boat box through horizontal velocity plus the configured forward range. Horizontal velocity determines direction; boat facing is used only at near-zero velocity.
- The probe uses each block's collision shape. Blocks merely beside, behind, above, or below the swept boat volume do not trigger.
- The probe follows the contacted collision shape upward through its contiguous stack. It measures obstacle height from the bottom of the hull to the top of that stack.
- Automatic boosts are rejected when the measured obstacle height exceeds the predicted rise plus `heightTolerance`. The prediction applies the current status's vanilla `floatBoat` acceleration, buoyancy, and damping for the current tick, then gravity-only airborne motion. `heightTolerance` defaults to `0.25` blocks and permits small clearance differences from partial collision shapes and hull positioning.
- The reachability guard applies only to obstacle-triggered automatic boosts. Manual keybind jumps have no target obstacle and are unaffected.
- A successful automatic boost replaces vertical velocity with `stepHeight`, preserving horizontal velocity.
- `boostStates`, `onlyForPlayers`, supporting-block filters, and colliding-block filters are evaluated independently for the current tick. No collision fact persists to another tick.

## Block filters

- Empty supporting or colliding filters mean unrestricted.
- Entries may be block IDs or `#`-prefixed block tags.
- Supporting filters apply only in `ON_LAND` and require an allowed block collision shape immediately beneath the hull.
- Colliding filters apply to the nearest `BlockState` whose collision shape was hit by the forward sweep; a permitted block behind a disallowed nearer block cannot trigger a boost.
- Invalid IDs entered in the configuration screen are ignored when the list is applied. Invalid IDs in a manually edited file or unknown block IDs may abort configuration or cache loading. Empty or unknown tags contribute no blocks.
- Tag-derived caches are immutable and invalidated by loader tag-reload events.

## Manual jump

- The default key is H. `allowJumpKeybind` is disabled by default.
- A keypress produces at most one local prediction for the local player's currently controlled boat. Holding the key does not repeatedly jump every tick.
- The impulse is additive and equals `stepHeight * keybindJumpHeightMultiplier`.
- If `onlyKeybindJumpOnGroundOrWater` is enabled, both prediction and authority reject a boat that is in air.
- The client sends only the target boat UUID. The server requires that the sender is riding and controlling that exact boat, applies its own configuration, and rate-limits duplicate requests.
- Client prediction is cosmetic responsiveness; server state is authoritative.

## Configuration authority

- Dedicated and integrated logical servers use a validated snapshot of their persistent configuration. Numeric values are finite and bounded before gameplay consumes them: `stepHeight` 0–4, `playerEjectTicks` 0–10000, `extraCollisionDetectionRange` 0–16, `heightTolerance` 0–4, and `keybindJumpHeightMultiplier` 0–8.
- A multiplayer client uses the immutable configuration snapshot received from the server for prediction.
- `heightTolerance` is synchronized as part of the server's validated gameplay snapshot.
- Saving from the singleplayer configuration screen refreshes both the integrated server snapshot and the client prediction snapshot immediately.
- Remote snapshots are cleared on disconnect. A remote server's settings are read-only in the client config screen.
- Persistence version and network protocol version are independent.

## Supported lines

This branch builds Minecraft 26.1 for Fabric/Quilt and NeoForge. It does not publish a 1.21.1 artifact. Older maintained lines must receive the obstacle-probe change through their own maintenance branch.
