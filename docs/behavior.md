# Gameplay semantics

This document is the behavioral contract for movement and networking changes.

## Automatic boost

- The controller evaluates an authoritative boat once from `AbstractBoat.floatBoat` on each logical-side tick.
- On Paper, Folia, and Purpur, the equivalent controller runs from the server's vehicle-movement event and applies velocity through the Bukkit boat API.
- A trigger is either Minecraft's normal `horizontalCollision`, or a block collision returned by the forward obstacle probe when `extraCollisionDetectionRange` is greater than zero.
- At range zero, only `horizontalCollision` can trigger. The probe may still identify the contacted block for filtering, but cannot create an early trigger.
- Bukkit does not expose Minecraft's `horizontalCollision` field, so the Paper-family target implements the range-zero trigger as a 0.001-block collision-shape contact probe. It does not use the boat's remaining velocity to create an early trigger. Because Paper reports movement after collision resolution, the plugin retains the preceding tick's horizontal motion and restores it when the collision removed a component; this lets vanilla clients carry a head-on automatic jump onto the obstacle just as they do at a corner.
- The probe sweeps the current boat box through horizontal velocity plus the configured forward range. Horizontal velocity determines direction; boat facing is used only at near-zero velocity.
- The probe uses each block's collision shape. Blocks merely beside, behind, above, or below the swept boat volume do not trigger.
- The probe follows the contacted collision shape upward through its contiguous stack. It measures obstacle height from the bottom of the hull to the top of that stack.
- Automatic boosts are rejected when the measured obstacle height exceeds the predicted rise plus `heightTolerance`. The prediction applies the current status's vanilla `floatBoat` acceleration, buoyancy, and damping on every simulated tick, transitions from submerged to surface-water physics at the measured waterline, and uses gravity-only motion after the hull leaves the water. `heightTolerance` defaults to `0.25` blocks and permits small clearance differences from partial collision shapes and hull positioning.
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
- Paper, Folia, and Purpur accept the same `bbm:manual_jump` plugin-message payload, validate the controlling player and boat UUID, and apply the same cooldown and server-side policy. Vanilla clients simply do not expose the optional keybind.

## Configuration authority

- Dedicated and integrated logical servers use a validated snapshot of their persistent configuration. Numeric values are finite and bounded before gameplay consumes them: `stepHeight` 0–4, `playerEjectTicks` 0–10000, `extraCollisionDetectionRange` 0–16, `heightTolerance` 0–4, and `keybindJumpHeightMultiplier` 0–8.
- A multiplayer client uses the immutable configuration snapshot received from the server for prediction.
- `heightTolerance` is synchronized as part of the server's validated gameplay snapshot.
- Saving from the singleplayer configuration screen refreshes both the integrated server snapshot and the client prediction snapshot immediately.
- Remote snapshots are cleared on disconnect. A remote server's settings are read-only in the client config screen.
- Persistence version and network protocol version are independent.
- Paper, Folia, and Purpur send the same `bbm:config_snapshot` payload as the modded servers, so an installed client mod uses the plugin's validated immutable snapshot.

## Paper-family client modes

The Paper, Folia, or Purpur plugin is the only server-side installation required. A server may accept vanilla clients and clients with the Fabric or NeoForge mod at the same time; both use the same authoritative plugin configuration and server movement result.

### Vanilla client

- Automatic boosts are detected and applied by the server plugin. The resulting boat velocity reaches the client through normal entity synchronization.
- `playerEjectTicks`, boost states, block filters, reachability, and every other gameplay restriction are evaluated by the plugin. A vanilla client cannot supply or override configuration.
- The client does not predict Better Boat Movement's boost before the server update arrives. On a higher-latency connection, the start of a boost can therefore feel less immediate than it does with the client mod.
- Manual jumping is unavailable because the vanilla client has neither the keybind nor a source for the validated manual-jump payload.
- There is no Better Boat Movement configuration screen. Server operators configure `plugins/better-boat-movement/better-boat-movement.json` and restart the server to apply file changes.

### Client mod installed

- On join, the plugin sends its validated immutable settings through `bbm:config_snapshot`. The client installs that snapshot as its remote configuration; its own local gameplay settings do not override the server.
- The client-side boat mixin uses the synchronized snapshot to predict automatic boosts. The plugin independently evaluates and applies the authoritative result, so prediction improves responsiveness without transferring authority to the client.
- If `allowJumpKeybind` is enabled by the server, the client exposes and predicts the manual jump. It sends only the controlled boat UUID through `bbm:manual_jump`.
- The plugin accepts a manual jump only when the sender is the controlling passenger of that exact boat, the server setting permits it, the ground-or-water rule passes, and the request cooldown has elapsed. A rejected prediction is corrected by ordinary server synchronization.
- The configuration screen shows the synchronized Paper-family settings as read-only. File changes remain a server-operator action and require a restart.
- The remote snapshot is discarded when the client disconnects, preventing one server's settings from leaking into another connection or singleplayer.

## Supported platforms

- Minecraft 26.1–26.2: Fabric, Quilt, NeoForge, Paper, Folia, and Purpur.
- Minecraft 26.3: Fabric, Quilt, Paper, Folia, and Purpur.
