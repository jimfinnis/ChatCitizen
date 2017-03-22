package org.pale.chatcitizen.plugininterfaces;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mcmonkey.sentinel.SentinelTrait;
import org.pale.chatcitizen.ChatTrait;
import org.pale.chatcitizen.ExternalPluginInterface;

public class Sentinel extends ExternalPluginInterface {

	public class SentinelData {

		public long timeSinceAttack;
		public String guarding; // "nothing", player name, or "something" if not guarding a player
		public long timeSinceSpawn;
		public double health;
		public String debug;
		
	}
	public Sentinel() {
		super("Sentinel", "1.0");
		
	}
	
	public SentinelData makeData(NPC n){
		if(isValid()){
			SentinelData d = new SentinelData();
			SentinelTrait t = n.getTrait(SentinelTrait.class);
			ChatTrait ct = n.getTrait(ChatTrait.class);
			
			// Sentinel resets TSA at attach, so it will be artificially low.
			if(Math.abs(t.timeSinceAttack - ct.timeSpawned)<100)
				d.timeSinceAttack = 1000000;
			else
				d.timeSinceAttack = t.timeSinceAttack;
			
			d.timeSinceSpawn = t.stats_ticksSpawned;
			
// hack for debugging
			d.debug = "TSA: "+t.timeSinceAttack+" TS: "+ct.timeSpawned;
			
			
			double maxh = t.getLivingEntity().getMaxHealth();
			double h = t.getLivingEntity().getHealth();
			
			d.health = (h/maxh)*100.0;
			
			if(t.getGuarding()!=null){
				Player p = Bukkit.getPlayer(t.getGuarding());
				if(p == null)
					d.guarding = "something";
				else
					d.guarding = p.getName();
					
			} else 
				d.guarding = "nothing";
			
			return d;
		} else
			return null;
	}

}
