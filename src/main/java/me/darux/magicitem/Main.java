package me.darux.magicitem;

import me.darux.magicitem.Listeners.MagicItems;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new MagicItems(this),this);
        // Plugin startup logic
        MagicItems magicItems=new MagicItems(this);
        magicItems.recargarespadatnt();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
