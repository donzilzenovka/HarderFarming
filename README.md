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

## Diagnostic HUD (prototype requirement)

Add a client-only, configurable, default-off crop inspector when crosshair targets a plant. Show registry name, crop adapter, substrate/planting mechanism, eligible/excluded reason, actual and normalized growth stage, live/dead condition, and current death probability per check. Clearly label calculated probability versus measured health: the initial prototype has no persistent health statistic. For withered crops show original crop identity, captured growth stage, and permanent death state. Avoid rendering every frame's world scan or server-side overlay work. If authoritative server state is needed, use a targeted rate-limited packet; do not assume client-side calculations are authoritative. Keep the panel compact and toggleable, with no permanent survival HUD clutter.
