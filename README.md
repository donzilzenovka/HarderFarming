# Harder Farming

Forge 1.7.10 crop mortality mod, built using EntityGovernor's RetroFuturaGradle 1.4.9 / Java 8 structure.

## Intended behaviour

Apply stage-dependent failure to **agricultural crops planted directly in soil/farmland**, whether vanilla or modded. Seedlings are most vulnerable; risk decreases to zero at maturity.

**Exclude all crop-stick and crop-host cultivation** (including Agricraft and IC2 crop systems), even when the plant species could also grow directly in soil. Also exclude fungi such as nether wart and mushrooms, and non-agricultural vegetation.

Crop adapters must supply (1) eligibility based on actual planting mechanism and substrate, (2) normalized growth progress 0..1, and (3) safe removal of a failed crop without damaging farmland. Unknown plants are exempt until supported rather than risk accidental destruction. Provide configurable inclusions/exclusions.

## Development status

**Not yet functional.** Forge skeleton and configuration exist, but no crop death checks are active. The old vanilla whitelist was removed. The config still has provisional vanilla-oriented switches; replace these when implementing direct-plant adapters and efficient server-side sampling.

Do not ship as a gameplay release until integration and tests are complete.
