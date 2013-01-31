package hinadamari.wolfranger;

import java.util.logging.Logger;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class WolfRangerEventListener implements Listener
{
	public WolfRanger plugin;
	public final static Logger log = Logger.getLogger("Minecraft");
	public boolean eventflg = false;

	public WolfRangerEventListener(WolfRanger instance)
	{
		plugin = instance;
	}

	@EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityAttack(EntityDamageByEntityEvent event) {

		if (eventflg) return;

		if (event.getDamager().getType() == EntityType.WOLF  && event.getCause() == DamageCause.ENTITY_ATTACK) {

			double rate = 0;

			Wolf attacker = (Wolf) event.getDamager();
			Damageable enemy = (Damageable) event.getEntity();
			Player kainusi = (Player)attacker.getOwner();

			if (!attacker.isTamed()) return;

			switch (attacker.getCollarColor()) {
		        case BLUE:
		        case CYAN:
		        case LIGHT_BLUE:
		        case MAGENTA:
		        case PURPLE:
		        	if (attacker.getHealth() == attacker.getMaxHealth()) break;
		        	rate = plugin.config.get(DyeColor.BLUE);
		        	if (rate == 0) break;
		        	attacker.setHealth(Math.min(attacker.getMaxHealth(), attacker.getHealth() + (int)Math.floor(event.getDamage() * rate)));
		        	kainusi.sendMessage("Wolf's Health is" + attacker.getHealth());
		    }

			switch (attacker.getCollarColor()) {
		        case BLACK:
		        case GRAY:
		        case SILVER:
		        	if (enemy.getHealth() - event.getDamage() <= 0) break;
		        	rate = plugin.config.get(DyeColor.BLACK);
		        	if (rate == 0 || Math.random() > rate) break;
		        	eventflg = true;
		        	enemy.damage(1000, attacker);
			    	eventflg = false;
			    	kainusi.sendMessage("overkill!!!");
			}

			switch (attacker.getCollarColor()) {
    			case ORANGE:
    			case YELLOW:
    				if (enemy.getHealth() - event.getDamage() <= 0) break;
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
    				if (enemy.getHealth() - event.getDamage() <= 0) break;
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
					if (enemy.getHealth() - event.getDamage() > 0) break;
					rate = plugin.config.get(DyeColor.GREEN);
		        	if (rate == 0 || Math.random() > rate) break;
					ItemStack drop = new ItemStack(Material.MELON_BLOCK, 1);
					World world = enemy.getWorld();
					world.dropItem(enemy.getLocation(), drop);
					kainusi.sendMessage("melon!");
			}

		} else if (event.getEntity().getType() == EntityType.WOLF  && event.getCause() == DamageCause.ENTITY_EXPLOSION) {

			Wolf wan = (Wolf) event.getEntity();

			switch (wan.getCollarColor()) {
				case CYAN:
				case GREEN:
    			case LIME:
    				eventflg = true;
    				event.setCancelled(true);
    				eventflg = false;
			}

		}

	}

	@EventHandler(priority= EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {

		if (event.getEntity().getType() != EntityType.WOLF) return;

		Wolf wan = (Wolf) event.getEntity();

		if (!wan.isTamed()) return;

		if (event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.FIRE) {

			switch (wan.getCollarColor()) {
				case BROWN:
					event.setCancelled(true);
					wan.setFireTicks(0);
			}

		} else if (event.getCause() == DamageCause.LAVA) {

			switch (wan.getCollarColor()) {
        		case BROWN:
    				event.setCancelled(true);
			}

		} else if (event.getCause() == DamageCause.FALL) {

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