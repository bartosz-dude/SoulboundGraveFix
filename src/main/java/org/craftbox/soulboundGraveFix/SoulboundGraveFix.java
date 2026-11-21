package org.craftbox.soulboundGraveFix;

import org.bukkit.plugin.java.JavaPlugin;

public final class SoulboundGraveFix extends JavaPlugin {

    @Override
    public void onEnable() {
//        getSLF4JLogger().atInfo("Loading server plugin SoulboundGraveFix");
        getServer().getPluginManager().registerEvents(new EventHandlers(this), this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
