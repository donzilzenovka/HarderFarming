# Harder Farming — Minecraft 1.7.10

Forge mod using the same RetroFuturaGradle 1.4.9 / Java 8 project setup as EntityGovernor.

## Experimental gameplay prototype

The first active prototype samples surface columns near players every configurable interval and tests direct-farmland crops for stage-dependent mortality. Stage 0 has the highest risk; stage 7 is immune. Dead crops become a persistent, breakable grey-brown withered plant without harvest drops. The client-only inspector displays growth stage and sampled-check mortality risk when looking at supported plants.

**Current crop support:** vanilla wheat, carrots and potatoes, plus modded blocks extending `BlockCrops` on vanilla farmland. Agricraft/IC2 crop-stick tile entities, nether wart, mushrooms, and other non-`BlockCrops` plants are not processed. Metadata stages are assumed to be 0..7; disable `moddedBlockCrops` if a mod uses incompatible metadata.

**Known limitations:** sampled checks do not provide a constant per-plant check rate and risk is **not** calibrated to 20% lifetime mortality. Sampling only looks at the top four blocks in a random surface column within 16 blocks of a player; underground or covered crops may be missed. The condition block now stores original crop registry name and metadata in a synchronized tile entity and uses the captured crop icon tinted grey-brown. Custom crop geometry and renderers are not yet preserved; generic crossed planes are used. Existing dead plants from earlier prototype builds may have no recorded identity. The inspector shows estimated risk and living/withered condition, not persistent plant health. A real crop registry, broader substrate support, texture-preserving rendering and precise mortality scheduling remain future work.

## Testing

Build with `gradlew build` using a compatible Gradle/JDK setup. Install the resulting jar without the original Hardcore Farming mod. Config generated at `config/harderfarming.cfg`; `diagnostics.debugHud` defaults to true for this prototype.

For a quick test, plant wheat/carrots/potatoes on vanilla farmland in an open area near the player. Increase `mortality.seedlingRiskPerCheck` (for example to 0.5) and set `mortality.checkIntervalTicks` to 1 temporarily; restore realistic values after checking that withering works. Test that crop sticks and nether wart remain unaffected.

**Build status:** source committed; no Gradle compilation was run in the assistant environment. Treat as an unverified prototype and report compilation or runtime errors.

## Plant-condition tile entity experiment

The dead plant is now a condition block with persistent NBT fields `OriginalBlock`, `OriginalMetadata`, `Condition`, `Health`, and `ConditionAge`. `Condition=2` is dead, `Condition=1` is reserved for sick plants. The recovery method restores the original block and growth metadata when explicitly invoked on a sick plant; no treatment gameplay or automatic recovery exists yet. The renderer uses the original block's `getIcon(0, metadata)` atlas icon, drawn on crossed quads and tinted grey-brown. Custom crop renderers and tile-entity-backed crops require later adapters. **Uncompiled change:** run Gradle locally and report build errors before testing in a valuable world; back up the world first.
