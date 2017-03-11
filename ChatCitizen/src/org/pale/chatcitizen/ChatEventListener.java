package org.pale.chatcitizen;

import net.citizensnpcs.api.util.Messaging;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
					Location l = e.getPlayer().getLocation();
					plugin.handleMessage(l,e.getMessage());
				}
			});
		}
	}
}
