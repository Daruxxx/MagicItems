package me.darux.magicitem.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import me.darux.magicitem.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class MagicItems implements Listener {
    private Main plugin;
    private HashMap<Player, ItemStack> cooldown = new HashMap();
    private ArrayList<Player> coolplayer = new ArrayList();

    public MagicItems(Main plugin) {
        this.plugin = plugin;
        this.loadTask();
    }

    public static String getName(ItemStack stack) {
        try {
            return CraftItemStack.asNMSCopy(stack).getName();
        } catch (NullPointerException var2) {
            return stack.getType().name();
        }
    }

    public static short getRealMaxDurability(Material m) {
        String firstName = getName(new ItemStack(m));

        short dur;
        for(dur = 1; !getName(new ItemStack(m, 1, dur)).equals(firstName); ++dur) {
        }

        return (short)(dur - 1);
    }

    @EventHandler
    public void MagicSword1(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE) || e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.WORKBENCH)) {
                return;
            }

            if (e.getItem() != null && e.getItem().getType() == Material.DIAMOND_SWORD) {
                ItemStack item = e.getItem();
                int durability = item.getDurability();
                Vector v;
                if (item.getEnchantmentLevel(Enchantment.KNOCKBACK) >= 1 && durability < 1000) {
                    v = p.getLocation().getDirection();
                    Snowball snow = (Snowball)p.launchProjectile(Snowball.class);
                    snow.setFallDistance(4.0F);
                    snow.setVelocity(v.multiply(1));
                    snow.setShooter(p);
                    item.setDurability((short)(item.getDurability() + 100));
                }

                if (item.getEnchantmentLevel(Enchantment.DAMAGE_ALL) >= 1  && durability < 700) {
                    if (durability > 700) {
                        return;
                    }

                    item.setDurability((short)(item.getDurability() + 300));
                    v = p.getLocation().getDirection();
                    TNTPrimed tnt = (TNTPrimed)p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
                    tnt.setVelocity(v.multiply(1.3D));
                    tnt.setFuseTicks(20);
                }
            }
        }

    }

    @EventHandler
    public void LightningArrowEffect(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            Player Shooter = (Player)e.getEntity().getShooter();
            Projectile Arrow = e.getEntity();
            if (Arrow.getType() != EntityType.ARROW) {
                return;
            }
            if(Shooter.getItemInHand().getItemMeta().hasEnchant(Enchantment.ARROW_DAMAGE)){
                this.lightning(Shooter, Arrow, 4, 8, 3.0F, 0.2F);
            }



        }

    }

    @EventHandler
    public void LightningBowEffect(EntityShootBowEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            if (e.getProjectile().getType() == EntityType.ARROW) {
                ItemStack item = e.getBow();
                int durability = item.getDurability();
                if (item.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) >= 1 && durability < 300) {
                    if (durability > 300) {
                        return;
                    }

                    Projectile proj = (Projectile)e.getProjectile();
                    proj.setMetadata("Lighting", new FixedMetadataValue(Bukkit.getServer().getPluginManager().getPlugin("ProtectTheCore"), true));
                    item.setDurability((short)(item.getDurability() + 60));
                }

            }
        }
    }

    private void lightning(Player shooter, Entity arrow, int Radius, int Damage, float multiply, float y) {
        LightningStrike lightningStrike = arrow.getLocation().getWorld().strikeLightning(arrow.getLocation());
        lightningStrike.setFireTicks(100);
    }

    public void loadTask() {
        final boolean[] enabled = new boolean[]{true};
        final int[] swords = new int[]{30, 40, 50, 60};
        final int[] bow = new int[]{7, 8, 6};
        (new Thread(new Runnable() {
            public void run() {
                while(enabled[0]) {
                    Iterator var2 = Bukkit.getOnlinePlayers().iterator();

                    while(var2.hasNext()) {
                        Player p = (Player)var2.next();
                        ItemStack item = p.getInventory().getItemInHand();
                        if (item.getType() == Material.DIAMOND_SWORD) {
                            if (item.getEnchantmentLevel(Enchantment.KNOCKBACK) >= 1) {
                                item.setDurability((short)(item.getDurability() - swords[(new Random()).nextInt(swords.length)]));
                            } else if (item.getEnchantmentLevel(Enchantment.DAMAGE_ALL) >= 4) {
                                item.setDurability((short)(item.getDurability() - swords[(new Random()).nextInt(swords.length)]));
                            }
                        } else if (item.getType() == Material.BOW && item.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) >= 1) {
                            item.setDurability((short)(item.getDurability() - bow[(new Random()).nextInt(bow.length)]));
                        }
                    }

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var4) {
                        enabled[0] = false;
                        var4.printStackTrace();
                    }
                }

            }
        })).start();
    }

    public void recargarespadatnt(){
        int taskID=Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for(Player p: Bukkit.getOnlinePlayers()){
                    if(p.getItemInHand().getType().equals(Material.DIAMOND_SWORD) && p.getItemInHand().getItemMeta().hasEnchant(Enchantment.DAMAGE_ALL)){
                        if(p.getItemInHand().getDurability()==0)return;
                            if(p.getItemInHand().getDurability()>200){
                                p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability()-200));
                            }else{
                                p.getItemInHand().setDurability((short) 0);

                        }
                    }
                }
            }
        },0,150);
    }
}