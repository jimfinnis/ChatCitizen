package org.pale.chatcitizen.plugininterfaces;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mcmonkey.sentinel.SentinelTrait;
import org.pale.chatcitizen.ExternalPluginInterface;

public class Sentinel extends ExternalPluginInterface {

	public class SentinelData {

		public long timeSinceAttack;
		public String guarding; // "nothing", player name, or "something" if not guarding a player
		public long timeSinceSpawn;
		
	}
	public Sentinel() {
		super("Sentinel", "1.0");
		
	}
	
	public SentinelData makeData(NPC n){
		if(isValid()){
			SentinelData d = new SentinelData();
			SentinelTrait t = n.getTrait(SentinelTrait.class);
			d.timeSinceAttack = 0;
			d.timeSinceSpawn = t.stats_ticksSpawned;
			
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
