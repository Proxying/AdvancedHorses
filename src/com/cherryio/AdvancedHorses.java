package com.cherryio;

import com.cherryio.entities.AdvancedHorse;
import com.cherryio.listeners.AdvancedHorsesListener;
import com.cherryio.utils.NMSUtils;
import net.minecraft.server.v1_9_R1.EntityHorse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by Kieran on 24-Mar-16.
 */
public class AdvancedHorses extends JavaPlugin {

    private static AdvancedHorses instance;
    public static Logger logger;
    public static String version = "16w12a";
    public static Random random = new Random();

    public static AdvancedHorses getInstance() {
        return instance;
    }

    public void onLoad() {
        instance = this;
        logger = this.getLogger();
        NMSUtils nmsUtils = new NMSUtils();
        nmsUtils.registerEntity("AdvancedHorse", 100, EntityHorse.class, AdvancedHorse.class);
    }

    public void onEnable() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AdvancedHorsesListener(), this);
        saveDefaultConfig();
    }
}
