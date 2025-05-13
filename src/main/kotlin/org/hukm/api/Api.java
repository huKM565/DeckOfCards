package org.hukm.api;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import ru.hukm.pokercards.utils.ItemsManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class Api extends JavaPlugin {

    @Override
    public void onEnable() {
    }

    public static boolean isFullInventory(Inventory inventory) {
        int countDeleteInventoryContains = 0;
        if(inventory.getSize() == 41 ) countDeleteInventoryContains = 5;
        for(int i = 0; i < inventory.getSize() - countDeleteInventoryContains; i++) if(inventory.getContents()[i] == null) return false;
        return true;
    }

    public static boolean tryGiveItem(ItemStack item, Player player) {
        Inventory inventory = player.getInventory();

        if(!isFullInventory(inventory)) {
            inventory.addItem(item);
            return true;
        }

        return false;
    }

    public static boolean giveItem(ItemStack item, Player player) {

        if (!tryGiveItem(item, player)) {
            player.getWorld().dropItem(player.getLocation(), item);
            return false;
        }

        return true;
    }

    public static boolean equalDisplayName(ItemStack item1, String itemDisplayName2) {
        if(item1.getItemMeta().getDisplayName().equals(itemDisplayName2)) return true;
        else return false;
    }

    public static boolean hasItemsEqualCustomModelData(Inventory inventory, ItemStack item, int count) {

        if(!hasItemsEqualMaterial(inventory, item, count)) return false;

        int sum = 0;

        for(ItemStack stack: inventory.getContents()) {
            try {
                int stackCustomModelData = stack.getItemMeta().getCustomModelData();
                int itemCustomModelData = item.getItemMeta().getCustomModelData();

                if(stackCustomModelData == itemCustomModelData) sum += stack.getAmount();
            }catch (NullPointerException | IllegalStateException e) {}
        }

        if(sum >= count) return true;

        return false;
    }

    public static boolean hasItemsEqualMaterial(Inventory inventory, ItemStack item, int count) {

        int sum = 0;

        for(ItemStack stack: inventory.getContents()) {
            try {
                Material stackMaterial = stack.getType();
                Material itemMaterial = item.getType();

                if(stackMaterial == itemMaterial) sum += stack.getAmount();
            }catch (NullPointerException e) {}
        }

        if(sum >= count) return true;

        return false;
    }

    public static void removeItemsEqualCustomModelData(Inventory inventory, ItemStack item, int count) {
        for(ItemStack stack: inventory.getContents()) {
            try {
                int stackCustomModelData = stack.getItemMeta().getCustomModelData();
                int itemCustomModelData = item.getItemMeta().getCustomModelData();

                int countStack = stack.getAmount();

                if(stackCustomModelData == itemCustomModelData) {
                    count -= countStack;
                    if(count >= 0) {
                        stack.setAmount(0);
                    }else{
                        stack.setAmount(-count);
                        break;
                    };
                };
            }catch (NullPointerException | IllegalStateException e) {}
        }
    }

    public static void removeItemsEqualMaterial(Inventory inventory, ItemStack item, int count) {
        for(ItemStack stack: inventory.getContents()) {
            try {
                Material stackMaterial = stack.getType();
                Material itemMaterial = item.getType();

                int countStack = stack.getAmount();

                if(stackMaterial == itemMaterial) {
                    count -= countStack;
                    if(count >= 0) {
                        stack.setAmount(0);
                    }else{
                        stack.setAmount(-count);
                        break;
                    };
                };
            }catch (NullPointerException e) {}
        }
    }

    public static int[] secondsToTimeValue(int allSeconds) {
        int hours = allSeconds / 3600;
        int minutes = (allSeconds % 3600) / 60;
        int seconds = allSeconds - hours * 3600 - minutes * 60;

        return new int[]{hours, minutes, seconds};
    }

    public static String secondsToTime(int allSeconds) {
        int[] time = secondsToTimeValue(allSeconds);

        return time[0] + ":" + time[1] + ":" + time[2];
    }

    public static String secondsToDate(int allSeconds) {
        int[] time = secondsToTimeValue(allSeconds);

        int day = time[0] / 24;
        time[0] -= day * 24;

        Date date = new Date();
        date.setHours(date.getHours() + time[0]);
        date.setMinutes(date.getMinutes() + time[1]);
        date.setSeconds(date.getSeconds() + time[2]);
        date.setDate(date.getDate() + day);

        return date.toString();
    }


    public static <Z, T> Z getContainerValue(ItemStack item, NamespacedKey key, PersistentDataType<T, Z> type) {
        try{
            return item.getItemMeta().getPersistentDataContainer().get(key, type);
        }catch (NullPointerException ignored) {}
        return null;
    };

    public static <Z, T> Z getContainerValue(PersistentDataHolder holder, NamespacedKey key, PersistentDataType<T, Z> type) {
        try{
            return holder.getPersistentDataContainer().get(key, type);
        }catch (NullPointerException ignored) {}
        return null;
    };


    public static <Z, T> ItemStack setContainerValue(ItemStack item, NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(key, type, value);
        item.setItemMeta(meta);

        return item;
    };

    public static <Z, T> void setContainerValue(PersistentDataHolder holder, NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        container.set(key, type, value);
    };


    public static <Z, T> Boolean hasContainerValue(ItemStack item, NamespacedKey key, PersistentDataType<Z, T> type) {
        return item.getItemMeta().getPersistentDataContainer().has(key, type);
    }

    public static <Z, T> Boolean hasContainerValue(PersistentDataHolder holder, NamespacedKey key, PersistentDataType<Z, T> type) {
        return holder.getPersistentDataContainer().has(key, type);
    }

    public static <Z> Z compactBase64GetContainerValue(ItemStack item, NamespacedKey key, Class<Z> clazz) {
        return compactBase64Deserialize(getContainerValue(item, key, PersistentDataType.STRING), clazz);
    }

    public static <Z> Z base64GetContainerValue(ItemStack item, NamespacedKey key, Class<Z> clazz) {
        return base64Deserialize(getContainerValue(item, key, PersistentDataType.STRING), clazz);
    }

    public static <Z> Z compactBase64GetContainerValue(PersistentDataHolder holder, NamespacedKey key, Class<Z> clazz) {
        return compactBase64Deserialize(getContainerValue(holder, key, PersistentDataType.STRING), clazz);
    }

    public static <Z> Z base64GetContainerValue(PersistentDataHolder holder, NamespacedKey key, Class<Z> clazz) {
        return base64Deserialize(getContainerValue(holder, key, PersistentDataType.STRING), clazz);
    }

    public static <Z> ItemStack compactBase64SetContainerValue(ItemStack item, NamespacedKey key, Z value) {
        return setContainerValue(item, key, PersistentDataType.STRING, compactBase64Serialize(value));
    }

    public static <Z> void compactBase64SetContainerValue(PersistentDataHolder holder, NamespacedKey key, Z value) {
        setContainerValue(holder, key, PersistentDataType.STRING, compactBase64Serialize(value));
    }

    public static <Z> ItemStack base64SetContainerValue(ItemStack item, NamespacedKey key, Z value) {
        return setContainerValue(item, key, PersistentDataType.STRING, base64Serialize(value));
    }

    public static <Z> void base64SetContainerValue(PersistentDataHolder holder, NamespacedKey key, Z value) {
        setContainerValue(holder, key, PersistentDataType.STRING, base64Serialize(value));
    }
    public static String compactBase64Serialize(Object object) {
        if (object == null) {
            return null;
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
             BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(gzipOutputStream)) {

            objectOutputStream.writeObject(object);

            objectOutputStream.flush();
            gzipOutputStream.finish();

            return Base64.getUrlEncoder().encodeToString(byteArrayOutputStream.toByteArray());

        } catch (IOException ex) {
            return null;
        }
    }

    public static String base64Serialize(Object object) {
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytesOut);

            out.writeObject(object);
            out.flush();
            out.close();

            return Base64.getUrlEncoder().encodeToString(bytesOut.toByteArray());
        } catch (Exception ex) {
            return null;
        }
    }

    public static <Z> Z base64Deserialize(String base64, Class<Z> clazz) {
        try {
            byte[] data = Base64.getUrlDecoder().decode(base64);

            ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
            BukkitObjectInputStream in = new BukkitObjectInputStream(bytesIn);
            in.close();

            return clazz.cast(in.readObject());
        } catch (Exception ex) {
            return null;
        }
    }

    public static <Z> Z compactBase64Deserialize(String base64, Class<Z> clazz) {
        if (base64 == null || base64.isEmpty()) {
            return null;
        }

        try {
            byte[] compressedData = Base64.getUrlDecoder().decode(base64);

            ByteArrayInputStream bytesIn = new ByteArrayInputStream(compressedData);
            GZIPInputStream gzipIn = new GZIPInputStream(bytesIn);

            try (bytesIn; gzipIn; BukkitObjectInputStream objectIn = new BukkitObjectInputStream(gzipIn)) {
                Object obj = objectIn.readObject();
                return clazz.cast(obj);
            }
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }

    public static void setGrayGlassPane(Inventory inventory) {
        setGrayGlassPane(inventory, 0);
    }

    public static void setGrayGlassPane(Inventory inventory, int customModelData) {
        setGrayGlassPane(inventory, customModelData, new ArrayList<>());
    }

    public static void setGrayGlassPane(Inventory inventory, int customModelData, List missSlots) {
        for(int index = 0; index < inventory.getSize(); index++) {
            if(inventory.getItem(index) == null && !missSlots.contains(index)) {
                ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta meta = item.getItemMeta();

                meta.setDisplayName(" ");

                if (customModelData != 0) meta.setCustomModelData(customModelData);

                item.setItemMeta(meta);

                inventory.setItem(index, ItemsManager.Companion.setType(item, "glass"));
            }
        }
    }

}
