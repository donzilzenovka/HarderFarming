package com.zenovka.harderfarming;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import java.util.Locale;

/** Read-only world statistics. Does not alter crops or reset saved counters. */
public final class CommandHarderFarming extends CommandBase {
    @Override public String getCommandName() { return "harderfarming"; }
    @Override public String getCommandUsage(ICommandSender sender) { return "/harderfarming stats"; }
    @Override public int getRequiredPermissionLevel() { return 0; }

    @Override public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1 || !"stats".equalsIgnoreCase(args[0])) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }
        World world = sender.getEntityWorld();
        CropHealthStats s = CropHealthStats.get(world);
        long resolved = s.recovered + s.died + s.treated + s.boostedRecovered + s.boostedDied;
        long unresolved = Math.max(0L, s.illnesses - resolved);
        sender.addChatMessage(new ChatComponentText("HarderFarming - dimension " + world.provider.dimensionId));
        sender.addChatMessage(new ChatComponentText("Illness episodes: " + s.illnesses
                + " | Natural recoveries: " + s.recovered + " | Deaths: " + s.died + " | Treated: " + s.treated));
        sender.addChatMessage(new ChatComponentText("Boost applications: " + s.boosted
                + " | Boosted recoveries: " + s.boostedRecovered
                + " | Boosted deaths: " + s.boostedDied));
        sender.addChatMessage(new ChatComponentText("Unresolved episodes: " + unresolved
                + " (includes crops removed while sick; not a live sick-plant count)"));
        if (s.recovered + s.died > 0) {
            sender.addChatMessage(new ChatComponentText(String.format(Locale.ROOT,
                    "Natural recovery among untreated resolved episodes: %.2f%% (%d/%d)",
                    s.recovered * 100.0 / (s.recovered + s.died), s.recovered, s.recovered + s.died)));
        } else {
            sender.addChatMessage(new ChatComponentText("Recovery among resolved episodes: N/A"));
        }
        sender.addChatMessage(new ChatComponentText(
                "Episode statistics only; crop lifetime survival cannot be inferred without a planted-cohort count."));
    }
}
