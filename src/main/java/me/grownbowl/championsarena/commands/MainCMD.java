package me.grownbowl.championsarena.commands;

import me.grownbowl.championsarena.arena.Arena;
import me.grownbowl.championsarena.matchmaker.MatchMaker;
import me.grownbowl.championsarena.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MainCMD implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            ChatUtil.sendMessage(sender, "&cНедостаточно аргументов!");
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (!sender.isOp()) {
                ChatUtil.sendMessage(sender, "&cНедостаточно прав");
                return true;
            }

            if (args.length == 1) {
                ChatUtil.sendMessage(sender, "&cВведите имя арены!");
                return true;
            }

            new Arena(args[1]);
            ChatUtil.sendMessage(sender, "Арена успешно создана!");
            return true;

        } else if (args[0].equalsIgnoreCase("setFirstSpawn")) {
            if (!sender.isOp()) {
                ChatUtil.sendMessage(sender, "&cНедостаточно прав");
                return true;
            }

            if (args.length == 1) {
                ChatUtil.sendMessage(sender, "&cВведите имя арены!");
                return true;
            }

            Arena arena = Arena.getArenaBy(args[1]);

            if (arena == null) {
                ChatUtil.sendMessage(sender, "&cТакой арены не существует!");
                return true;
            }

            arena.setFirstSpawn(player.getLocation());
            ChatUtil.sendMessage(sender, "Первый спавн успешно установлен!");
            return true;

        } else if (args[0].equalsIgnoreCase("setSecondSpawn")) {
            if (!sender.isOp()) {
                ChatUtil.sendMessage(sender, "&cНедостаточно прав");
                return true;
            }

            if (args.length == 1) {
                ChatUtil.sendMessage(sender, "&cВведите имя арены!");
                return true;
            }

            Arena arena = Arena.getArenaBy(args[1]);

            if (arena == null) {
                ChatUtil.sendMessage(sender, "&cТакой арены не существует!");
                return true;
            }

            arena.setSecondSpawn(player.getLocation());
            ChatUtil.sendMessage(sender, "Второй спавн успешно установлен!");
            return true;

        } else if (args[0].equalsIgnoreCase("launch")) {
            if (!sender.isOp()) {
                ChatUtil.sendMessage(sender, "&cНедостаточно прав");
                return true;
            }

            if (args.length == 1) {
                ChatUtil.sendMessage(sender, "&cВведите имя арены!");
                return true;
            }

            Arena arena = Arena.getArenaBy(args[1]);

            if (arena == null) {
                ChatUtil.sendMessage(sender, "&cТакой арены не существует!");
                return true;
            }

            if (arena.openArena()) {
                ChatUtil.sendMessage(player, "Арена открыта!");

            } else {
                ChatUtil.sendMessage(player, "&cАрена не открыта, т.к не настроена");
            }

            return true;

        } else if (args[0].equalsIgnoreCase("join")) {
            MatchMaker.instance.joinQueue(player);
            return true;

        } else if (args[0].equalsIgnoreCase("leave")) {
            MatchMaker.instance.leaveQueue(player);
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 1) {
            if (sender.isOp()) {
                return Arrays.asList("create", "setFirstSpawn", "setSecondSpawn", "launch", "join", "leave");

            } else {
                return Arrays.asList("join", "leave");
            }
        }

        return null;
    }
}
