package net.thecomplex.complexlife.config;

import cpw.mods.gross.Java9ClassLoaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.mclanguageprovider.MinecraftModLanguageProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;

public class ConfigurationManager {
    private final Logger logger = LogManager.getLogger();

    public static String getConfigDir() {
        return "";
    }

    public static Configuration getConfiguration(String modid) {
        File configFile = new File(getConfigDir(), modid + ".cfg");

        return null;
    }
}
