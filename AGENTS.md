# Repository guidance

## Project scope

Better Boat Movement contains a shared Fabric and NeoForge mod plus a standalone Paper-family plugin.

- For work under `mod/`, follow `mod/AGENTS.md`.
- For work under `paper/`, follow `paper/AGENTS.md`.
- For changes spanning both modules, follow both module guidance files and keep shared behavior and protocols compatible.

Do not commit generated files from `build/` or runtime directories.

## Documentation

- Keep the README focused on users, installation-facing behavior, and configuration examples.
- Record detailed authority, collision, movement, and networking semantics in `docs/behavior.md`.
- Update English translations for every visible option. Maintain existing translated locales when practical.

## Change safety

- Preserve unrelated working-tree changes.
- Do not weaken server validation for manual jump requests or configuration authority.
