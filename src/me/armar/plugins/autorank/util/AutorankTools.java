package me.armar.plugins.autorank.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.armar.plugins.autorank.language.Lang;

/*
 * AutorankTools is a bunch of static methods, I put sendColoredMessage
 * there so that if I ever wanted to change the message formatting I can just do that here.
 *
 */

public class AutorankTools {

    public static enum Time {
        DAYS, HOURS, MINUTES, SECONDS
    }

    private static List<String> reqTypes = new ArrayList<String>();
    private static List<String> resTypes = new ArrayList<String>();

    public static boolean containsAtLeast(final Player player, final ItemStack item, final int amount,
            String displayName) {
        // Check if player has at least the x of an item WITH proper displayname

        int count = 0;

        // Otherwise we'll not find any colour codes
        displayName = displayName.replace("&", "§");

        // Check every slot
        for (final ItemStack itemFound : player.getInventory().getContents()) {

            if (itemFound == null)
                continue;

            // Not the same item
            if (!itemFound.getType().equals(item.getType()))
                continue;

            // Check display name
            if (!itemFound.hasItemMeta() || !itemFound.getItemMeta().hasDisplayName())
                continue;

            if (itemFound.getItemMeta().getDisplayName().equals(displayName)) {
                count += itemFound.getAmount();
            }
        }

        return count >= amount;
    }

    public static String createStringFromList(final Collection<?> c) {
        final StringBuilder builder = new StringBuilder("");

        final Object[] array = c.toArray();

        for (int i = 0; i < c.size(); i++) {

            if (i == 0) {
                builder.append(ChatColor.GRAY + array[i].toString() + ChatColor.RESET);
            } else if (i == (c.size() - 1)) {
                builder.append(" and " + ChatColor.GRAY + array[i].toString() + ChatColor.RESET);
            } else {
                builder.append(", " + ChatColor.GRAY + array[i].toString() + ChatColor.RESET);
            }
        }

        return builder.toString();
    }

    /**
     * Calculates the edit distance of two strings (Levenshtein distance)
     * 
     * @param a
     *            First string to compare
     * @param b
     *            Second string to compate
     * @return Levenshtein distance of two strings.
     */
    public static int editDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    public static String findClosestSuggestion(String input, List<String> list) {
        int lowestDistance = Integer.MAX_VALUE;
        String bestSuggestion = null;

        for (String possibility : list) {
            int dist = editDistance(input, possibility);

            if (dist < lowestDistance) {
                lowestDistance = dist;
                bestSuggestion = possibility;
            }
        }

        return bestSuggestion + ";" + lowestDistance;
    }

    /**
     * This will return the correct type of the requirement. As admins might
     * want to use multiple requirements of the same type, they only have to
     * specify the name of it with a unique identifier. E.g. time1, time2 or
     * exp1, exp2, etc.
     * 
     * @param oldName
     *            Name of the requirement to search for.
     * @return correct requirement name or old name if none was found.
     */
    public static String getCorrectReqName(String oldName) {

        // Remove all numbers from string
        oldName = oldName.replaceAll("[^a-zA-Z\\s]", "").trim();

        for (final String type : reqTypes) {
            if (!oldName.contains(type)) {
                continue;
            }
            // Contains word

            if (type.length() == oldName.length()) {

                return type;
            }

            // Did not match correctly, search for next word.
            continue;

        }

        return null;
    }

    /**
     * This will return the correct type of the result. As admins might want to
     * use multiple results of the same type, they only have to specify the name
     * of it with a unique identifier. E.g. command1, command2 or message1,
     * message2, etc.
     * 
     * @param oldName
     *            Name of the result to search for.
     * @return correct result name or old name if none was found.
     */
    public static String getCorrectResName(String oldName) {

        // Remove all numbers from string
        oldName = oldName.replaceAll("[^a-zA-Z\\s]", "").trim();

        for (final String type : resTypes) {
            if (!oldName.contains(type)) {
                continue;
            }

            // Contains word

            if (type.length() == oldName.length()) {
                return type;
            }

            // Did not match correctly, search for next word.
            continue;
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack getFoodItemFromName(String name) {
        // Cannot use switch, is only supported in Java 1.7+

        if (name == null)
            return null;

        name = name.toUpperCase();
        name = name.replace(" ", "_");

        if (name.equals("APPLE")) {
            return new ItemStack(Material.APPLE, 1);
        } else if (name.equals("BAKED_POTATO")) {
            return new ItemStack(Material.BAKED_POTATO, 1);
        } else if (name.equals("BREAD")) {
            return new ItemStack(Material.BREAD, 1);
        } else if (name.equals("CAKE_BLOCK")) {
            return new ItemStack(Material.CAKE_BLOCK, 1);
        } else if (name.equals("CARROT_ITEM")) {
            return new ItemStack(Material.CARROT_ITEM, 1);
        } else if (name.equals("COOKED_CHICKEN")) {
            return new ItemStack(Material.COOKED_CHICKEN, 1);
        } else if (name.equals("COOKED_FISH")) {
            return new ItemStack(Material.COOKED_FISH, 1);
        } else if (name.equals("COOKED_SALMON")) {
            return new ItemStack(Material.COOKED_FISH.getId(), 1, (short) 1);
        } else if (name.equals("COOKED_MUTTON")) {
            return new ItemStack(Material.COOKED_MUTTON, 1);
        } else if (name.equals("GRILLED_PORK")) {
            return new ItemStack(Material.GRILLED_PORK, 1);
        } else if (name.equals("COOKED_RABBIT")) {
            return new ItemStack(Material.COOKED_RABBIT, 1);
        } else if (name.equals("COOKIE")) {
            return new ItemStack(Material.COOKIE, 1);
        } else if (name.equals("GOLDEN_APPLE")) {
            return new ItemStack(Material.GOLDEN_APPLE, 1);
        } else if (name.equals("ENCHANTED_GOLDEN_APPLE")) {
            return new ItemStack(Material.GOLDEN_APPLE.getId(), 1, (short) 1);
        } else if (name.equals("GOLDEN_CARROT")) {
            return new ItemStack(Material.GOLDEN_CARROT, 1);
        } else if (name.equals("MELON")) {
            return new ItemStack(Material.MELON, 1);
        } else if (name.equals("MUSHROOM_SOUP")) {
            return new ItemStack(Material.MUSHROOM_SOUP, 1);
        } else if (name.equals("RABBIT_STEW")) {
            return new ItemStack(Material.RABBIT_STEW, 1);
        } else if (name.equals("RAW_BEEF")) {
            return new ItemStack(Material.RAW_BEEF, 1);
        } else if (name.equals("RAW_CHICKEN")) {
            return new ItemStack(Material.RAW_CHICKEN, 1);
        } else if (name.equals("RAW_FISH")) {
            return new ItemStack(Material.RAW_FISH, 1);
        } else if (name.equals("RAW_SALMON")) {
            return new ItemStack(Material.RAW_FISH.getId(), 1, (short) 1);
        } else if (name.equals("CLOWNFISH")) {
            return new ItemStack(Material.RAW_FISH.getId(), 1, (short) 2);
        } else if (name.equals("PUFFERFISH")) {
            return new ItemStack(Material.RAW_FISH.getId(), 1, (short) 3);
        } else if (name.equals("POISONOUS_POTATO")) {
            return new ItemStack(Material.POISONOUS_POTATO, 1);
        } else if (name.equals("POTATO")) {
            return new ItemStack(Material.POTATO, 1);
        } else if (name.equals("PUMPKIN_PIE")) {
            return new ItemStack(Material.PUMPKIN_PIE, 1);
        } else if (name.equals("MUTTON")) {
            return new ItemStack(Material.MUTTON, 1);
        } else if (name.equals("COOKED_BEEF")) {
            return new ItemStack(Material.COOKED_BEEF, 1);
        } else if (name.equals("RABBIT")) {
            return new ItemStack(Material.RABBIT, 1);
        } else if (name.equals("ROTTEN_FLESH")) {
            return new ItemStack(Material.ROTTEN_FLESH, 1);
        } else if (name.equals("SPIDER_EYE")) {
            return new ItemStack(Material.SPIDER_EYE, 1);
        } else
            return null;
    }

    /**
     * Get the name of this food item.
     * 
     * @param item
     *            ItemStack to get the name of.
     * @return Name of food, or null if not a valid food item.
     */
    public static String getFoodName(final ItemStack item) {
        // Returns null if not a valid food item
        // Got Materials from
        // https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html

        if (item == null)
            return null;

        switch (item.getType()) {
            case APPLE:
                return "APPLE";
            case BAKED_POTATO:
                return "BAKED_POTATO";
            case BREAD:
                return "BREAD";
            case CAKE_BLOCK: // not working atm
                return "CAKE_BLOCK";
            case CARROT_ITEM:
                return "CARROT_ITEM";
            case COOKED_CHICKEN:
                return "COOKED_CHICKEN";
            case COOKED_FISH: {
                if (item.getDurability() == (short) 1) {
                    return "COOKED_SALMON";
                }
                return "COOKED_FISH";
            }
            case COOKED_MUTTON:
                return "COOKED_MUTTON";
            case GRILLED_PORK:
                return "GRILLED_PORK";
            case COOKED_RABBIT:
                return "COOKED_RABBIT";
            case COOKIE:
                return "COOKIE";
            case GOLDEN_APPLE: {
                if (item.getDurability() == (short) 1) {
                    return "ENCHANTED_GOLDEN_APPLE";
                }
                return "GOLDEN_APPLE";
            }
            case GOLDEN_CARROT:
                return "GOLDEN_CARROT";
            case MELON:
                return "MELON";
            case MUSHROOM_SOUP:
                return "MUSHROOM_SOUP";
            case RABBIT_STEW:
                return "RABBIT_STEW";
            case RAW_BEEF:
                return "RAW_BEEF";
            case RAW_CHICKEN:
                return "RAW_CHICKEN";
            case RAW_FISH: {
                if (item.getDurability() == (short) 1) {
                    return "RAW_SALMON";
                } else if (item.getDurability() == (short) 2) {
                    return "CLOWNFISH";
                } else if (item.getDurability() == (short) 3) {
                    return "PUFFERFISH";
                }
                return "RAW_FISH";
            }
            case POISONOUS_POTATO:
                return "POISONOUS_POTATO";
            case POTATO:
                return "POTATO";
            case PUMPKIN_PIE:
                return "PUMPKIN_PIE";
            case MUTTON:
                return "MUTTON"; // raw
            case COOKED_BEEF:
                return "COOKED_BEEF";
            case RABBIT:
                return "RABBIT";
            case ROTTEN_FLESH:
                return "ROTTEN_FLESH";
            case SPIDER_EYE:
                return "SPIDER_EYE";
            default:
                return null;
        }
    }

    public static String getStringFromArgs(final String[] args, final int startArg) {
        final StringBuilder string = new StringBuilder("");

        for (int i = startArg; i < args.length; i++) {

            if (i == startArg) {
                string.append(args[i]);
            } else {
                string.append(" " + args[i]);
            }
        }

        return string.toString();
    }

    /**
     * Split a string with .split() and then get the given element in the array.
     * 
     * @param splitString
     *            String to split
     * @param splitterCharacter
     *            character to split the string with
     * @param element
     *            element to get
     * @return String that was at the given splitted element
     */
    public static String getStringFromSplitString(final String splitString, final String splitterCharacter,
            final int element) {
        final String[] split = splitString.split(splitterCharacter);

        String returnString = null;

        try {
            returnString = split[element];

            if (returnString.trim().equals(""))
                return null;

        } catch (final ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return returnString;
    }

    /**
     * Elaborate method to check whether a player is excluded from ranking.
     * <p>
     * When a player has a wildcard permission but is an OP, it will return
     * false; When a player has a wildcard permission but is not an OP, it will
     * return true; When a player only has autorank.exclude, it will return
     * true;
     * 
     * @param player
     *            Player to check for
     * @return whether a player is excluded from ranking or not.
     */
    public static boolean isExcluded(final Player player) {
        if (player.hasPermission("autorank.askdjaslkdj")) {
            // Op's have all permissions, but if he is a OP, he isn't excluded
            if (player.isOp()) {
                return false;
            }

            // Player uses wildcard permission, so excluded
            return true;
        }

        if (player.hasPermission("autorank.exclude")) {
            return true;
        }

        return false;
    }

    public static String makeProgressString(final Collection<?> c, final String wordBetween,
            final Object currentValue) {
        final Object[] array = c.toArray();

        String extraSpace = " ";

        if (wordBetween == null || wordBetween.equals("")) {
            extraSpace = "";
        }

        String progress = "";

        for (int i = 0; i < c.size(); i++) {

            final String object = array[i].toString();

            if (i == 0) {
                progress += currentValue + extraSpace + wordBetween + "/" + object + extraSpace + wordBetween;
            } else {
                progress += " or " + currentValue + extraSpace + wordBetween + "/" + object + extraSpace + wordBetween;
            }
        }

        return progress;
    }

    public static HashMap<String, Object> makeStatsInfo(Object... strings) {
        HashMap<String, Object> hashmap = new HashMap<>();
        for (int i = 0; i < strings.length; i += 2) {
            Object string = strings[i];

            // Either key or value is null
            if (string == null || strings[i + 1] == null) {
                continue;
            }

            try {
                int value = Integer.parseInt(strings[i + 1].toString());

                if (value < 0) {
                    continue;
                }
            } catch (NumberFormatException e) {
                // It's not a number, so skip the check.
            }

            hashmap.put(string.toString(), strings[i + 1]);
        }

        return hashmap;
    }

    /**
     * Register requirement name so it can be used to get the correct name. If a
     * requirement is not passed through this method, it will not show up in
     * {@link #getCorrectReqName(String)}.
     * 
     * @param type
     *            Requirement name
     */
    public static void registerRequirement(final String type) {
        if (!reqTypes.contains(type)) {
            reqTypes.add(type);
        }
    }

    /**
     * Register result name so it can be used to get the correct name. If a
     * result is not passed through this method, it will not show up in
     * {@link #getCorrectResName(String)}.
     * 
     * @param type
     *            Result name
     */
    public static void registerResult(final String type) {
        if (!resTypes.contains(type)) {
            resTypes.add(type);
        }
    }

    public static void sendColoredMessage(final CommandSender sender, final String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GREEN + msg));
    }

    public static void sendColoredMessage(final Player player, final String msg) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GREEN + msg));
    }

    /**
     * Create a string that shows all elements of the given list <br>
     * The end divider is the last word used for the second last element. <br>
     * Example: a list with {1,2,3,4,5,6,7,8,9,0} and end divider 'or'. <br>
     * Would show: 1, 2, 3, 4, 5, 6, 7, 8, 9 or 0.
     * 
     * @param c
     *            Array to get the elements from.
     * @param endDivider
     *            Last word used for dividing the second last and last word.
     * @return string with all elements.
     */
    public static String seperateList(final Collection<?> c, final String endDivider) {
        final Object[] array = c.toArray();
        if (array.length == 1)
            return array[0].toString();

        if (array.length == 0)
            return null;

        final StringBuilder string = new StringBuilder("");

        for (int i = 0; i < array.length; i++) {

            if (i == (array.length - 1)) {
                string.append(array[i]);
            } else if (i == (array.length - 2)) {
                // Second last
                string.append(array[i] + " " + endDivider + " ");
            } else {
                string.append(array[i] + ", ");
            }
        }

        return string.toString();
    }

    public static double stringtoDouble(final String string) throws NumberFormatException {
        double res = 0;

        if (string != null) {
            try {
                res = Double.parseDouble(string);
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        return res;
    }

    public static int stringtoInt(final String string) throws NumberFormatException {
        int res = 0;

        if (string != null) {
            try {
                res = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        return res;
    }

    /**
     * Convert a string to an integer.
     * 
     * @param string
     *            input; this must be in the format '10d 14h 15m'
     * @param time
     *            the time type of the output
     * @return the integer representing the number of seconds/minutes/hours/days
     */
    public static int stringToTime(String string, final Time time) {
        int res = 0;

        string = string.trim();

        final Pattern pattern = Pattern.compile("((\\d+)d)?((\\d+)h)?((\\d+)m)?");
        final Matcher matcher = pattern.matcher(string);

        matcher.find();

        final String days = matcher.group(2);
        final String hours = matcher.group(4);
        String minutes = matcher.group(6);

        // No day or hours or minute was given, so default to minutes.
        if (days == null && hours == null & minutes == null) {
            minutes = string;
        }

        if (stringtoDouble(minutes) < 0) {
            // Something is wrong, return -1
            return -1;
        }

        res += stringtoDouble(minutes);
        res += stringtoDouble(hours) * 60;
        res += stringtoDouble(days) * 60 * 24;

        // Res time is in minutes

        if (time.equals(Time.SECONDS)) {
            return res * 60;
        } else if (time.equals(Time.MINUTES)) {
            return res;
        } else if (time.equals(Time.HOURS)) {
            return res / 60;
        } else if (time.equals(Time.DAYS)) {
            return res / 1440;
        } else {
            return 0;
        }
    }

    /**
     * Convert an integer to a string. <br>
     * Format of the returned string: <b>x days, y hours, z minutes and r
     * seconds</b>
     * 
     * @param count
     *            the value to convert
     * @param time
     *            the type of time of the value given (DAYS, HOURS, MINUTES,
     *            SECONDS)
     * @return string in given format
     */
    public static String timeToString(int count, final Time time) {
        final StringBuilder b = new StringBuilder();

        int days = 0, hours = 0, minutes = 0, seconds = 0;

        if (time.equals(Time.DAYS)) {
            days = count;
        } else if (time.equals(Time.HOURS)) {
            days = count / 24;

            hours = count - (days * 24);
        } else if (time.equals(Time.MINUTES)) {
            days = count / 1440;

            count = count - (days * 1440);

            hours = count / 60;

            minutes = count - (hours * 60);
        } else if (time.equals(Time.SECONDS)) {
            days = count / 86400;

            count = count - (days * 86400);

            hours = count / 3600;

            count = count - (hours * 3600);

            minutes = count / 60;

            seconds = count - (minutes * 60);
        }

        if (days != 0) {
            b.append(days);
            b.append(" ");
            if (days != 1)
                b.append(Lang.DAY_PLURAL.getConfigValue());
            else
                b.append(Lang.DAY_SINGULAR.getConfigValue());

            if (hours != 0 || minutes != 0)
                b.append(", ");
        }

        if (hours != 0) {
            b.append(hours);
            b.append(" ");
            if (hours != 1)
                b.append(Lang.HOUR_PLURAL.getConfigValue());
            else
                b.append(Lang.HOUR_SINGULAR.getConfigValue());

            if (minutes != 0)
                b.append(", ");
        }

        if (minutes != 0 || (hours == 0 && days == 0)) {
            b.append(minutes);
            b.append(" ");
            if (minutes != 1)
                b.append(Lang.MINUTE_PLURAL.getConfigValue());
            else
                b.append(Lang.MINUTE_SINGULAR.getConfigValue());

            if (seconds != 0)
                b.append(", ");
        }

        if (seconds != 0) {
            b.append(seconds);
            b.append(" ");
            if (seconds != 1)
                b.append(Lang.SECOND_PLURAL.getConfigValue());
            else
                b.append(Lang.SECOND_SINGULAR.getConfigValue());
        }

        // Replace last comma with an and if needed.
        final int index = b.lastIndexOf(",");

        if (index != -1) {
            b.replace(index, index + 1, " " + Lang.AND.getConfigValue());
        }

        return b.toString();
    }
}
