# Repository guidance

## Project overview

Better Boat Movement is a Kotlin/Java Minecraft mod built for Fabric and NeoForge from one Stonecutter-managed source tree. Keep loader-neutral gameplay code under `src/main`, and isolate loader registration in `src/main/kotlin/dev/nyon/bbm/platform`.

## Build and test

- Run `./gradlew test` after gameplay, configuration, migration, or networking changes. This executes the test suite against both supported loaders.
- Run `./gradlew build` when changing mixins, metadata, dependencies, or generated loader sources.
- Do not commit generated files from `build/` or loader run directories.

## Implementation conventions

- Keep movement decisions deterministic and side-neutral. Minecraft object inspection belongs in probes/controllers; policy classes should remain easy to unit test.
- Treat the logical server as authoritative. Client-side movement prediction must use the synchronized immutable `GameplayConfigSnapshot`.
- When adding a gameplay config field, update the mutable config, validated snapshot, `toMutableConfig`, config screen, translations, network snapshot codec, tests, README example, and `docs/behavior.md` where applicable.
- Preserve network codec encode/decode field order and cover new fields with a round-trip test.
- Use collision shapes rather than assuming every block occupies a full cube.
- Keep Fabric- and NeoForge-specific code behaviorally equivalent.

## Documentation

- Keep the README focused on users, installation-facing behavior, and configuration examples.
- Record detailed authority, collision, movement, and networking semantics in `docs/behavior.md`.
- Update English translations for every visible option. Maintain existing translated locales when practical.

## Change safety

- Preserve unrelated working-tree changes.
- Add focused regression tests for bug fixes, especially movement geometry, config validation/migration, and payload codecs.
- Do not weaken server validation for manual jump requests or configuration authority.
