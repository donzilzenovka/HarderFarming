# Harder Farming

Forge 1.7.10 crop mortality mod, built using EntityGovernor's RetroFuturaGradle 1.4.9 / Java 8 structure.

## Intended behaviour

Apply stage-dependent crop failure to **soil-grown agricultural plants across mods**, not just vanilla blocks. Seedlings are most vulnerable; risk decreases toward zero at maturity. Eligible systems include vanilla crops, Pam's HarvestCraft, Agricraft crop sticks, and IC2 crops. Nether wart, mushrooms, mycelium-based fungi and non-agricultural plants are excluded.

A crop adapter must provide: (1) eligibility and fungus exclusion; (2) normalized growth progress 0..1; (3) a safe method to kill the plant without destroying its substrate or violating the crop host's internal state. Adapter-specific exclusions and overrides should be configurable.

## Development status

**Not yet functional.** The Forge skeleton and configuration exist, but no crop death checks are active. The previous vanilla-only whitelist has been removed. Do not ship this branch as a gameplay release until the mod-specific crop adapters, efficient server-side sampling, and tests are implemented.

The existing `config/harderfarming.cfg` still contains provisional vanilla-oriented settings; they will be replaced as adapters are added.
