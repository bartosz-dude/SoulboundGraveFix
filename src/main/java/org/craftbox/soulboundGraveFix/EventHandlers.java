package org.craftbox.soulboundGraveFix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.ranull.graves.type.Grave;
import dev.cwhead.GravesX.event.GraveBlockPlaceEvent;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class EventHandlers implements Listener {
    private static final Enchantment SOULBOUND_ENCHANTMENT = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(
            Key.key("soulbound_enchantment", "soulbound"));

//    private final HashMap<UUID, ArrayList<ItemStack>> itemStacksToReturn = new HashMap<>();

    private final NamespacedKey itemsKey;

    public EventHandlers(JavaPlugin plugin) {
        this.itemsKey = new NamespacedKey(plugin, "soulbound_items_to_return");
    }


    @EventHandler
    public void onGraveBlockPlace(GraveBlockPlaceEvent event) {
        Grave grave = event.getGrave();
        Player player = event.getPlayer();

        ArrayList<ItemStack> soulbound = new ArrayList<>();

        for (var itemStack : grave.getInventoryItemStack()) {
            if (itemStack != null && itemStack.containsEnchantment(EventHandlers.SOULBOUND_ENCHANTMENT)) {
                soulbound.add(itemStack);
            }
        }

        if (soulbound.isEmpty()) return;

        player.getPersistentDataContainer().set(itemsKey, PersistentDataType.BYTE_ARRAY, ItemStack.serializeItemsAsBytes(soulbound));

        for (var itemStack : soulbound) {
            grave.getInventory().remove(itemStack);
        }
    }

    @EventHandler
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        byte[] data = pdc.get(itemsKey, PersistentDataType.BYTE_ARRAY);

        if (data == null) return;

        for (ItemStack item : ItemStack.deserializeItemsFromBytes(data)) {
            if (item.isEmpty()) continue;

            player.getInventory().addItem(item).values().forEach(
                    leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
        }

        pdc.remove(itemsKey);
    }
}
