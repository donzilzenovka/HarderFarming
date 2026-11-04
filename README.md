# Harder Farming — Minecraft 1.7.10

Forge mod using the same RetroFuturaGradle 1.4.9 / Java 8 project setup as EntityGovernor.

## Experimental gameplay prototype

The first active prototype samples surface columns near players every configurable interval and tests direct-farmland crops for stage-dependent mortality. Stage 0 has the highest risk; stage 7 is immune. Dead crops become a persistent, breakable grey-brown withered plant without harvest drops. The client-only inspector displays growth stage and sampled-check mortality risk when looking at supported plants.

**Current crop support:** vanilla wheat, carrots and potatoes, plus modded blocks extending `BlockCrops` on vanilla farmland. Agricraft/IC2 crop-stick tile entities, nether wart, mushrooms, and other non-`BlockCrops` plants are not processed. Metadata stages are assumed to be 0..7; disable `moddedBlockCrops` if a mod uses incompatible metadata.

**Known limitations:** sampled checks do not provide a constant per-plant check rate and risk is **not** calibrated to 20% lifetime mortality. Sampling only looks at the top four blocks in a random surface column within 16 blocks of a player; underground or covered crops may be missed. The withered plant uses the vanilla dead-bush sprite tinted grey-brown, not the original crop sprite or recorded identity. The inspector shows estimated risk and living/withered condition, not persistent plant health. A real crop registry, broader substrate support, texture-preserving rendering and precise mortality scheduling remain future work.

## Testing

Build with `gradlew build` using a compatible Gradle/JDK setup. Install the resulting jar without the original Hardcore Farming mod. Config generated at `config/harderfarming.cfg`; `diagnostics.debugHud` defaults to true for this prototype.

For a quick test, plant wheat/carrots/potatoes on vanilla farmland in an open area near the player. Increase `mortality.seedlingRiskPerCheck` (for example to 0.5) and set `mortality.checkIntervalTicks` to 1 temporarily; restore realistic values after checking that withering works. Test that crop sticks and nether wart remain unaffected.

**Build status:** source committed; no Gradle compilation was run in the assistant environment. Treat as an unverified prototype and report compilation or runtime errors.
