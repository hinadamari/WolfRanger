package hinadamari.wolfranger;

import java.util.logging.Logger;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

/**
 * WolfRanger イベントリスナ
 * @author hinadamari
 */
public class WolfRangerEventListener implements Listener
{
    public WolfRanger plugin;
    public final static Logger log = Logger.getLogger("Minecraft");
    public boolean eventflg = false;

    public WolfRangerEventListener(WolfRanger instance)
    {
        plugin = instance;
    }

    /**
     * 攻撃時の追加効果など
     * @param event
     */
    @EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityAttack(EntityDamageByEntityEvent event) {

        if (eventflg) return;

        if (event.getDamager().getType() == EntityType.WOLF  && event.getCause() == DamageCause.ENTITY_ATTACK) {

            // ■オオカミが敵に攻撃した時の追加効果

            double rate = 0;

            Wolf attacker = (Wolf) event.getDamager();
            Damageable enemy = (Damageable) event.getEntity();

            if (!attacker.isTamed()) return;

            switch (attacker.getCollarColor()) {
                case BLUE:
                case CYAN:
                case LIGHT_BLUE:
                case MAGENTA:
                case PURPLE:
                    // 与えたダメージに比例して体力を回復
                    if (attacker.getHealth() == attacker.getMaxHealth()) break; // 体力がMAXなら中止
                    rate = plugin.config.get(DyeColor.BLUE);
                    if (rate == 0) break;
                    attacker.setHealth(Math.min(attacker.getMaxHealth(), attacker.getHealth() + (int)Math.floor(event.getDamage() * rate)));
            }

            switch (attacker.getCollarColor()) {
                case BLACK:
                case GRAY:
                case SILVER:
                    // 敵に致命的な追加ダメージ
                    if (enemy.getHealth() - event.getDamage() <= 0) break; // 敵死亡時は中止
                    rate = plugin.config.get(DyeColor.BLACK);
                    if (rate == 0 || Math.random() > rate) break; // 当たり抽選
                    eventflg = true;
                    enemy.damage(1000, attacker);
                    eventflg = false;
            }

            switch (attacker.getCollarColor()) {
                case ORANGE:
                case YELLOW:
                    // 敵に着火
                    if (enemy.getHealth() - event.getDamage() <= 0) break; // 敵死亡時は中止
                    eventflg = true;
                    enemy.setFireTicks(100);
                    eventflg = false;
            }

            switch (attacker.getCollarColor()) {
                case MAGENTA:
                case ORANGE:
                case PURPLE:
                case PINK:
                case RED:
                    // 敵に追加ダメージ
                    if (enemy.getHealth() - event.getDamage() <= 0) break; // 敵死亡時は中止
                    rate = plugin.config.get(DyeColor.RED);
                    if (rate == 1) break;
                    eventflg = true;
                    enemy.damage((int)Math.floor(event.getDamage() * (rate - 1)), attacker);
                    eventflg = false;
            }

            switch (attacker.getCollarColor()) {
                case CYAN:
                case GREEN:
                case LIME:
                    // メロンを落とさせる
                    if (enemy.getHealth() - event.getDamage() > 0) break; // 敵死亡時のみ処理を続ける
                    rate = plugin.config.get(DyeColor.GREEN);
                    if (rate == 0 || Math.random() > rate) break; // 当たり抽選
                    ItemStack drop = new ItemStack(Material.MELON_BLOCK, 1);
                    World world = enemy.getWorld();
                    world.dropItem(enemy.getLocation(), drop);
            }

        } else if (event.getEntity().getType() == EntityType.WOLF  && event.getCause() == DamageCause.ENTITY_EXPLOSION) {

            // ■クリーパーがオオカミに攻撃した時の追加効果

            Wolf wan = (Wolf) event.getEntity();

            switch (wan.getCollarColor()) {
                case CYAN:
                case GREEN:
                case LIME:
                    // クリーパーの爆破ダメージを無効化
                    event.setCancelled(true);
            }

        }

    }

    /**
     * ダメージ軽減・無効化処理
     * @param event
     */
    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntity().getType() != EntityType.WOLF) return;

        Wolf wan = (Wolf) event.getEntity();

        if (!wan.isTamed()) return;

        if (event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.FIRE) {

            // 炎によるダメージを無効化
            switch (wan.getCollarColor()) {
                case BROWN:
                    event.setCancelled(true);
                    wan.setFireTicks(0);
            }

        } else if (event.getCause() == DamageCause.LAVA) {

            // 熔岩によるダメージを無効化
            switch (wan.getCollarColor()) {
                case BROWN:
                    event.setCancelled(true);
            }

        } else if (event.getCause() == DamageCause.FALL) {

            // 落下によるダメージを無効化
            switch (wan.getCollarColor()) {
                case GRAY:
                case SILVER:
                case LIGHT_BLUE:
                case LIME:
                case PINK:
                case WHITE:
                    event.setCancelled(true);
            }

        }

    }

}