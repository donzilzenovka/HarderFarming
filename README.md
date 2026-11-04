# Harder Farming

Forge 1.7.10 crop mortality mod, built using EntityGovernor's RetroFuturaGradle 1.4.9 / Java 8 structure.

## Intended behaviour

Apply stage-dependent failure to **agricultural crops planted directly in soil/farmland**, whether vanilla or modded. Seedlings are most vulnerable; risk decreases to zero at maturity.

**Exclude all crop-stick and crop-host cultivation** (including Agricraft and IC2 crop systems), even when the plant species could also grow directly in soil. Also exclude fungi such as nether wart and mushrooms, and non-agricultural vegetation.

Crop adapters must supply (1) eligibility based on actual planting mechanism and substrate, (2) normalized growth progress 0..1, and (3) safe removal of a failed crop without damaging farmland. Unknown plants are exempt until supported rather than risk accidental destruction. Provide configurable inclusions/exclusions.

## Development status

**Not yet functional.** Forge skeleton and configuration exist, but no crop death checks are active. The old vanilla whitelist was removed. The config still has provisional vanilla-oriented switches; replace these when implementing direct-plant adapters and efficient server-side sampling.

Do not ship as a gameplay release until integration and tests are complete.

## Withered crop prototype

When an eligible crop fails, leave a persistent, non-growing withered plant occupying its planting position until broken by hand. Preserve the original crop identity and display its current growth stage, capped at roughly half maturity (never enlarge a young failed seedling). Target an opaque, dusty grey-brown tint around `#887B69`, retaining recognizable crop texture. Do not drop edible harvest on failure; breaking the withered plant clears the plot without damaging soil.

Rendering original modded crop textures may require adapters or fallbacks; this is a design target, not yet implemented. Sick/recoverable plants are explicitly deferred until after the mortality prototype.
