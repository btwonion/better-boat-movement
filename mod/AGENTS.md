# Mod module guidance

## Scope and architecture

- This module contains the shared Fabric and NeoForge mod. Keep loader-neutral gameplay code under `mod/src/main` and loader registration or API integration under `mod/src/main/kotlin/dev/nyon/bbm/platform`.
- Keep Fabric and NeoForge behavior equivalent. Do not solve a shared gameplay problem in only one loader implementation.
- Minecraft object inspection belongs in probes and controllers. Keep decisions deterministic, side-neutral, and independently testable where practical.

## Networking compatibility

- Before completing any change, compare every payload under `mod/src/main/kotlin/dev/nyon/bbm/network` with the matching Paper implementation in `paper/src/main/kotlin/dev/nyon/bbm/paper/PaperPayloads.kt`.
- Channel identifiers, field order, wire types, UUID representation, encoding/decoding, defaults, and validation behavior must match exactly on both sides.
- When a shared protocol changes, update mod and Paper implementations and their tests together. The server remains authoritative for configuration and manual jump validation.

## Movement and configuration

- Client prediction must use only the synchronized immutable `GameplayConfigSnapshot`; do not read mutable local settings for multiplayer movement decisions.
- When adding a gameplay setting, update the mutable and validated configurations, `toMutableConfig`, screen, translations, network codec, tests, README example, `docs/behavior.md`, and the Paper equivalent where applicable.
- Use collision shapes rather than treating blocks as full cubes, and keep movement behavior equivalent between logical client prediction and server authority.

## Build and test

- Run `./gradlew build` when changing mixins, metadata, dependencies, networking, shared gameplay behavior, or generated loader sources.
- Verify every supported Stonecutter target affected by the change; do not edit or commit generated files under `build` or loader run directories.
