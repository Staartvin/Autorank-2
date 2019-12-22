package me.armar.plugins.autorank.hooks.download;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DependencyDownloader {

    private Autorank plugin;
    private boolean hasLoaded;

    public DependencyDownloader(Autorank plugin) {
        this.plugin = plugin;
        this.hasLoaded = false;
    }

    public void downloadDependency(String name, String id) {
        File file = new File(plugin.getDataFolder().getParent(), name + ".jar");

        if (file.exists() || Bukkit.getPluginManager().getPlugin(name) != null) {
            // Not downloading anything, because it's already present.
            return;
        }

        try {
            URL url = new URL("https://api.spiget.org/v2/resources/" + id + "/download");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) " +
                    "Gecko/20100101 Firefox/56.0");

            InputStream in = connection.getInputStream();
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            byte[] buffer = new byte[1024];

            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
//            downloadIfFail(name, id);
            Bukkit.getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED + "The dependency " + ChatColor.AQUA + name +
                    ChatColor.RED + " could not be downloaded!");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        loadPlugin(file);
        Bukkit.getConsoleSender().sendMessage("[Autorank] " + ChatColor.GREEN + "The dependency " + ChatColor.AQUA + name +
                ChatColor.GREEN + " was successfully downloaded!");
        this.hasLoaded = true;
    }


//    public void downloadIfFail(String name, String id){
//        File temp = new File(plugin.getDataFolder(), name + ".json");
//        File file = new File(plugin.getDataFolder().getParent(), name + ".jar");
//        try {
//            URL url = new URL("https://api.spiget.org/v2/resources/" + id);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0)
//            Gecko/20100101 Firefox/56.0");
//
//            InputStream in = connection.getInputStream();
//            OutputStream out = new BufferedOutputStream(new FileOutputStream(temp));
//
//            byte[] buffer = new byte[1024];
//
//            int numRead;
//            while ((numRead = in.read(buffer)) != -1) {
//                out.write(buffer, 0, numRead);
//            }
//
//            in.close();
//            out.close();
//        }catch (IOException e){
//            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The dependency " + ChatColor.AQUA + name +
//            ChatColor.RED + " was not downloaded!");
//            return;
//        }
//
//        Gson gson = new Gson();
//        Resource resource;
//        try (Reader reader = new FileReader(temp)) {
//            resource = gson.fromJson(reader, Resource.class);
//        } catch (IOException e) {
//            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The dependency " + ChatColor.AQUA + name +
//            ChatColor.RED + " was not downloaded!");
//            return;
//        }
//
//        try {
//            InputStream in = getInputStream("https://www.spigotmc.org/" + resource.getFile().getUrl());
//            if(in == null){
//                System.out.println("NULL");
//                return;
//            }
//
//            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
//
//            byte[] buffer = new byte[1024];
//            int numRead;
//            while ((numRead = in.read(buffer)) != -1) {
//                out.write(buffer, 0, numRead);
//            }
//
//            in.close();
//            out.close();
//        }catch (IOException e){
//            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The dependency " + ChatColor.AQUA + name +
//            ChatColor.RED + " was not downloaded as the downloaded!");
//            return;
//        }
//
//        loadPlugin(file);
//        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "The dependency " + ChatColor.AQUA + name +
//        ChatColor.GREEN + " was successfully downloaded!");
//        this.hasLoaded = true;
//    }

    public Autorank getPlugin() {
        return plugin;
    }

    public boolean hasLoaded() {
        return hasLoaded;
    }

    private void loadPlugin(File file) {
        try {
            Bukkit.getPluginManager().loadPlugin(file);
        } catch (InvalidDescriptionException | InvalidPluginException e) {
            e.printStackTrace();
        }
    }

}
