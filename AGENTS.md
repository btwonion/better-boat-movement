# Repository guidance

## Project overview

Better Boat Movement is a Kotlin/Java Minecraft project built for Fabric and NeoForge from one Stonecutter-managed source tree, plus a standalone Paper-family plugin. Keep loader-neutral mod gameplay code under `mod/src/main`, isolate loader registration in `mod/src/main/kotlin/dev/nyon/bbm/platform`, and keep Paper-specific behavior under `paper/src/main`.

## Build and test

- Run `./gradlew build` when changing mixins, metadata, dependencies, or generated loader sources.
- Run `./gradlew :paper:build` when changing the Paper-family plugin.
- Do not commit generated files from `build/` or loader run directories.

## Implementation conventions

- Keep movement decisions deterministic and side-neutral. Minecraft object inspection belongs in probes/controllers; policy classes should remain easy to unit test.
- Treat the logical server as authoritative. Client-side movement prediction must use the synchronized immutable `GameplayConfigSnapshot`.
- Before completing any change, check that every shared network packet matches between the Paper plugin and the mod, including channel identifiers, field order, wire types, encoding/decoding, and validation behavior. Update and test both sides together whenever the protocol changes.
- When adding a gameplay config field, update the mutable config, validated snapshot, `toMutableConfig`, config screen, translations, network snapshot codec, tests, README example, and `docs/behavior.md` where applicable.
- Use collision shapes rather than assuming every block occupies a full cube.
- Keep Fabric- and NeoForge-specific code behaviorally equivalent.

## Documentation

- Keep the README focused on users, installation-facing behavior, and configuration examples.
- Record detailed authority, collision, movement, and networking semantics in `docs/behavior.md`.
- Update English translations for every visible option. Maintain existing translated locales when practical.

## Change safety

- Preserve unrelated working-tree changes.
- Do not weaken server validation for manual jump requests or configuration authority.
