package org.pale.chatcitizen;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * This listens for the various events we're interested in, not just the chat - despite
 * the name (this class was written when the only event was Chat, but I don't want to rename
 * it because reasons).
 * 
 * IT DOES NOT listen for NPC specific events - they are in the trait.
 * @author white
 *
 */
public final class ChatEventListener implements Listener {
	Plugin plugin;
	public ChatEventListener(Plugin p){
		plugin=p;
		p.getServer().getPluginManager().registerEvents(this, p);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void monitorChat(final AsyncPlayerChatEvent e){
		if(e.isAsynchronous()){
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
				// might be better do do this in thread...
				@Override
				public void run() {
					plugin.handleMessage(e.getPlayer(),e.getMessage());
				}
			});
		} else {
			plugin.handleMessage(e.getPlayer(),e.getMessage());			
		}
	}
}
