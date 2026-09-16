# Paper module guidance

## Scope and architecture

- This module is the standalone Paper, Folia, and Purpur plugin. Keep its implementation under `paper/src/main` and do not depend on mod-loader classes.
- Use the Bukkit/Paper API and collision shapes for world inspection. Keep movement policy and calculations independent from event and entity access where practical so they remain easy to unit test.
- Preserve Folia compatibility: do not introduce global-thread assumptions or unsafe cross-region entity access.

## Networking compatibility

- Before completing any change, compare every shared payload in `PaperPayloads.kt` with the matching implementation under `mod/src/main/kotlin/dev/nyon/bbm/network`.
- Channel identifiers, field order, wire types, UUID representation, encoding/decoding, defaults, and validation behavior must match exactly on both sides.
- When a shared protocol changes, update Paper and mod implementations and their tests together. Never weaken server-side validation for manual jump requests or configuration authority.

## Movement and configuration

- The logical server is authoritative. Vanilla clients must receive safe server-side behavior, while modded clients may only predict from synchronized validated settings.
- Account for Paper event timing: vehicle movement events run after collision resolution and may expose velocity with blocked components already removed.
- When changing gameplay configuration, keep `PaperGameplayConfig`, validation, migration, persistence, payload encoding, and the equivalent mod configuration synchronized.

## Build and test

- Run `./gradlew :paper:build` for Paper changes.
- Add focused tests under `paper/src/test` for protocol layouts, validation, and movement calculations whenever practical.
- Do not commit files generated under `paper/build` or server run directories.
