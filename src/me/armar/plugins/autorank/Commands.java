package me.armar.plugins.autorank;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.additionalrequirement.AdditionalRequirement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private Autorank plugin;

    public Commands(Autorank plugin) {
	this.plugin = plugin;
    }

    private void noPerm(CommandSender sender) {
	AutorankTools.sendColoredMessage(sender, "You do not have permission to do this.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	String action = args[0];

	if (action.equalsIgnoreCase("help")) {

	    AutorankTools.sendColoredMessage(sender, "-- Autorank Commands --");
	    AutorankTools.sendColoredMessage(sender, "/ar check - Check your own status");
	    AutorankTools.sendColoredMessage(sender, "/ar check [player] - Check [player]'s status");
	    AutorankTools.sendColoredMessage(sender, "/ar leaderboard - Show the leaderboard");
	    AutorankTools.sendColoredMessage(sender, "/ar set [player] [value] - Set [player]'s time to [value]");
	    AutorankTools.sendColoredMessage(sender, "/ar add [player] [value] - Add [value] to [player]'s time");
	    AutorankTools.sendColoredMessage(sender, "/ar rem [player] [value] - Remove [value] from [player]'s time");
	    AutorankTools.sendColoredMessage(sender, "/ar debug - Shows debug information");
	    AutorankTools.sendColoredMessage(sender, "/ar reload - Reload the plugin");

	    return true;
	} else if (action.equalsIgnoreCase("check")) {
	    if (args.length > 1) {

		if (!sender.hasPermission("autorank.checkothers")) {
		    noPerm(sender);
		    return true;
		}

		Player player = plugin.getServer().getPlayer(args[1]);
		if (player == null) {
		    AutorankTools.sendColoredMessage(sender, "Player " + args[1] + " is not online.");
		} else {
		    check(sender, player);
		}
	    } else if (sender instanceof Player) {
		if (!sender.hasPermission("autorank.check")) {
		    noPerm(sender);
		    return true;
		}
		Player player = (Player) sender;
		check(sender, player);
	    } else {
		AutorankTools.sendColoredMessage(sender, "Can't check for console.");
	    }
	    return true;
	} else if (action.equalsIgnoreCase("leaderboard")) {
	    if (!sender.hasPermission("autorank.leaderboard")) {
		noPerm(sender);
		return true;
	    }
	    plugin.getLeaderboard().sendLeaderboard(sender);
	    return true;
	} else if (action.equalsIgnoreCase("set")) {

	    if (!sender.hasPermission("autorank.set")) {
		noPerm(sender);
		return true;
	    }

	    int value = -1;
	    if (args.length > 2)
		try {
		    value = AutorankTools.stringtoInt(args[2]);
		} catch (NumberFormatException e) {
		}

	    if (value >= 0) {
		plugin.setTime(args[1], value);
		AutorankTools.sendColoredMessage(sender, "Changed playtime of " + args[1] + " to " + value + ".");
	    } else {
		AutorankTools.sendColoredMessage(sender, "Invalid format, use /ar set [player] [value]");
	    }

	    return true;
	} else if (action.equalsIgnoreCase("add")) {

	    if (!sender.hasPermission("autorank.add")) {
		noPerm(sender);
		return true;
	    }

	    int value = -1;
	    if (args.length > 2)
		try {
		    value = AutorankTools.stringtoInt(args[2]);
		    value += plugin.getTime(args[1]);
		} catch (NumberFormatException e) {
		}

	    if (value >= 0 && sender instanceof Player) {
		plugin.setTime(args[1], value);
		AutorankTools.sendColoredMessage(sender, "Changed playtime of " + args[1] + " to " + value + ".");
	    } else {
		AutorankTools.sendColoredMessage(sender, "Invalid format, use /ar add [player] [value]");
	    }

	    return true;
	} else if (action.equalsIgnoreCase("remove")) {

	    if (!sender.hasPermission("autorank.remove")) {
		noPerm(sender);
		return true;
	    }

	    int value = -1;
	    if (args.length > 2)
		try {
		    value = -AutorankTools.stringtoInt(args[2]);
		    value += plugin.getTime(args[1]);
		} catch (NumberFormatException e) {
		}

	    if (value >= 0 && sender instanceof Player) {
		plugin.setTime(args[1], value);
		AutorankTools.sendColoredMessage(sender, "Changed playtime of " + args[1] + " to " + value + ".");
	    } else {
		AutorankTools.sendColoredMessage(sender, "Invalid format, use /ar rem [player] [value]");
	    }

	    return true;
	} else if (action.equalsIgnoreCase("debug")) {

	    if (!sender.hasPermission("autorank.debug")) {
		noPerm(sender);
		return true;
	    }

	    AutorankTools.sendColoredMessage(sender, "-- Autorank Debug --");
	    AutorankTools.sendColoredMessage(sender, "RankChanges");
	    for (String change : plugin.getPlayerChecker().toStringArray()) {
		AutorankTools.sendColoredMessage(sender, change);
	    }
	    AutorankTools.sendColoredMessage(sender, "--------------------");

	    return true;
	} else if (action.equalsIgnoreCase("reload")) {

	    if (!sender.hasPermission("autorank.reload")) {
		noPerm(sender);
		return true;
	    }

	    AutorankTools.sendColoredMessage(sender, "Reloaded Autorank");
	    plugin.reload();

	    return true;
	}

	return false;
    }

    private void check(CommandSender sender, Player player) {
	Map<RankChange, List<AdditionalRequirement>> failed = plugin.getPlayerChecker().getFailedRequirementsForApplicableGroup(player);

	Set<RankChange> keySet = failed.keySet();
	String playername = player.getName();

	String[] groups = plugin.getPermissionsHandler().getPlayerGroups(player);
	StringBuilder stringBuilder = new StringBuilder();
	stringBuilder.append(playername + " has played for " + plugin.getTime(playername) + " minutes, ");
	stringBuilder.append(" is in ");
	if (groups.length == 0)
	    stringBuilder.append("no groups.");
	else if (groups.length == 1)
	    stringBuilder.append("group ");
	else
	    stringBuilder.append("groups ");

	boolean first = true;
	for (String group : groups) {
	    if (!first) {
		stringBuilder.append(", ");
	    }
	    stringBuilder.append(group);
	}

	AutorankTools.sendColoredMessage(sender, stringBuilder.toString());

	if (keySet.size() == 0) {
	    AutorankTools.sendColoredMessage(sender, "and doesn't have a next rankup.");
	} else {
	    Iterator<RankChange> it = keySet.iterator();
	    while (it.hasNext()) {
		RankChange rank = it.next();
		List<AdditionalRequirement> reqs = failed.get(rank);

		if (reqs.size() == 0) {
		    AutorankTools.sendColoredMessage(sender, "meets all the requirements for rank " + rank.getRank()
			    + " and will now be ranked up.");
		    plugin.getPlayerChecker().checkPlayer(player);
		} else {
		    AutorankTools.sendColoredMessage(sender, "and doesn't meet these requirements for rank " + rank.getRank() + ":");

		    for (AdditionalRequirement req : reqs) {
			if (req != null)
			    AutorankTools.sendColoredMessage(sender, "     - " + req.getDescription());
		    }
		}

	    }
	}
    }

}
