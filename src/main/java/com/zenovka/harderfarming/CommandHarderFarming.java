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
        long resolved = s.recovered + s.died;
        long unresolved = Math.max(0L, s.illnesses - resolved);
        sender.addChatMessage(new ChatComponentText("HarderFarming - dimension " + world.provider.dimensionId));
        sender.addChatMessage(new ChatComponentText("Illness episodes: " + s.illnesses
                + " | Natural recoveries: " + s.recovered + " | Deaths: " + s.died));
        sender.addChatMessage(new ChatComponentText("Unresolved episodes: " + unresolved
                + " (includes crops removed while sick; not a live sick-plant count)"));
        if (resolved > 0) {
            sender.addChatMessage(new ChatComponentText(String.format(Locale.ROOT,
                    "Recovery among resolved episodes: %.2f%% (%d/%d)",
                    s.recovered * 100.0 / resolved, s.recovered, resolved)));
        } else {
            sender.addChatMessage(new ChatComponentText("Recovery among resolved episodes: N/A"));
        }
        sender.addChatMessage(new ChatComponentText(
                "Episode statistics only; crop lifetime survival cannot be inferred without a planted-cohort count."));
    }
}
