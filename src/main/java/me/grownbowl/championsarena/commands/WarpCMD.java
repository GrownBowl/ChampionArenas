package me.grownbowl.championsarena.commands;

import me.grownbowl.championsarena.warp.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            System.out.println(Warp.warps);

            for (Warp warp: Warp.warps) {
                if (warp.getWarpId().equals(args[0])) {
                    player.teleport(warp.getLocation());
                    player.sendMessage("Вы были телепортированы на варп " + warp.getWarpName());
                    return true;
                }
            }

            player.sendMessage("Такого варпа не существует");
            return true;
        }

        return false;
    }
}
