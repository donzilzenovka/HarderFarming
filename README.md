# Harder Farming — Minecraft 1.7.10

Forge mod using the same RetroFuturaGradle 1.4.9 / Java 8 project setup as EntityGovernor.

## Experimental gameplay prototype

The first active prototype samples surface columns near players every configurable interval and tests direct-farmland crops for stage-dependent mortality. Stage 0 has the highest risk; stage 7 is immune. Dead crops become a persistent, breakable grey-brown withered plant without harvest drops. The client-only inspector displays growth stage and sampled-check mortality risk when looking at supported plants.

**Current crop support:** vanilla wheat, carrots and potatoes, plus modded blocks extending `BlockCrops` on vanilla farmland. Agricraft/IC2 crop-stick tile entities, nether wart, mushrooms, and other non-`BlockCrops` plants are not processed. Metadata stages are assumed to be 0..7; disable `moddedBlockCrops` if a mod uses incompatible metadata.

**Known limitations:** sampling and crop growth are stochastic, so the configured lifetime survival is an approximate expectation rather than a guarantee. Sampling only looks at the top four blocks in a random surface column within 16 blocks of a player; underground or covered crops may be missed. The condition block now stores original crop registry name and metadata in a synchronized tile entity and uses the captured crop icon tinted grey-brown. Custom crop geometry and renderers are not yet preserved; generic crossed planes are used. Existing dead plants from earlier prototype builds may have no recorded identity. The inspector shows estimated risk and living/withered condition, not persistent plant health. A real crop registry, broader substrate support, texture-preserving rendering and precise mortality scheduling remain future work.

## Testing

Build with `gradlew build` using a compatible Gradle/JDK setup. Install the resulting jar without the original Hardcore Farming mod. Config generated at `config/harderfarming.cfg`; `diagnostics.debugHud` defaults to true for this prototype.

For a quick test, plant wheat/carrots/potatoes on vanilla farmland in an open area near the player. For accelerated death testing, set `mortality.useLifetimeTarget=false` and increase `mortality.seedlingRiskPerCheck` (for example to 0.5); restore `useLifetimeTarget=true` for normal balancing. Test that crop sticks and nether wart remain unaffected.

**Build status:** source committed; no Gradle compilation was run in the assistant environment. Treat as an unverified prototype and report compilation or runtime errors.

## Plant-condition tile entity experiment

The dead plant is now a condition block with persistent NBT fields `OriginalBlock`, `OriginalMetadata`, `Condition`, `Health`, and `ConditionAge`. `Condition=2` is dead, `Condition=1` is reserved for sick plants. The recovery method restores the original block and growth metadata when explicitly invoked on a sick plant; no treatment gameplay or automatic recovery exists yet. The renderer uses the original block's `getIcon(0, metadata)` atlas icon, drawn on crossed quads and tinted grey-brown. Custom crop renderers and tile-entity-backed crops require later adapters. **Uncompiled change:** run Gradle locally and report build errors before testing in a valuable world; back up the world first.

## Original crop render orientation (experimental)

The geometry/bounding-box snapshot changes introduced in bfc84b5 have been rolled back. The tile entity retains original crop registry name, metadata and render type, but no longer captures or serializes per-crop bounds. The tile renderer uses four offset upright planes (`#`) for render type 6 (vanilla crops), two crossed planes (`X`) for type 1, and a crossed fallback for other/custom types. Sprite transparency determines visible growth-stage height. The condition block's selection bounds revert to the earlier fixed size, so oversized targeting may return. **Not locally compiled or game-tested.**

## Selection bounds and desaturation experiment

The original crop selection AABB is captured immediately before block replacement and saved in the condition tile entity. Only the selection/collision logic uses these bounds; the sprite renderer still uses the original render type (# for type 6, X fallback) and full texture UVs. The dead renderer now adds a neutral grey-brown translucent overlay using the original texture's alpha mask, reducing visible green saturation. Legacy withered tile entities without bounds use the old fixed selection box. This OpenGL path is experimental and has not been compiled or visually tested here; check alpha cutouts and other mods' rendering after rebuilding.

## Lifetime survival calibration (experimental)

The default `mortality.useLifetimeTarget=true` calculates stage-zero risk from `targetLifetimeSurvival=0.85` and `expectedGrowthMinutes=30`. The expected check count is duration in ticks divided by `checkIntervalTicks`, multiplied by `samplesPerPlayer/1089` (a 33x33 square). The weighted exposure assumes equal time in stages 0–6 and applies the configured quadratic decline; stage 7 remains immune. `seedlingRiskPerCheck` is an advanced override **only** when `useLifetimeTarget=false`. Existing config files gain the new settings on load. Overlapping players' sample attempts are thinned according to the number of player sampling squares covering each column, preventing approximate multiplayer risk multiplication. The HUD shows the target and calibrated per-check risk.

**Calibration caveats:** actual growth time, fertilizer, lighting, loaded time, crop stage durations, coverage, and underground planting can all alter realized survival. The model is a statistical approximation; it does not track each plant's age or guarantee an exact survival fraction. Client HUD values on dedicated servers currently use the client's local configuration and may differ from server-side settings. No sick-state transitions have been enabled yet. This change has not been compiled or tested in-game by the assistant.
