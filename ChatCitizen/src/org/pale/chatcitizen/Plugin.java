package org.pale.chatcitizen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;



public class Plugin extends JavaPlugin {
	public static void log(String msg) {
		getInstance().getLogger().info(msg);
	}
	/**
	 * Make the plugin a weird singleton.
	 */
	static Plugin instance = null;
	
	/**
	 * All the bot wrappers - the Traits share the bots.
	 */
	private Map<String,ChatterWrapper> bots = new HashMap<String,ChatterWrapper>();
	
	/**
	 * Use this to get plugin instances - don't play silly buggers creating new
	 * ones all over the place!
	 */
	public static Plugin getInstance() {
		if (instance == null)
			throw new RuntimeException(
					"Attempt to get plugin when it's not enabled");
		return instance;
	}

	@Override
	public void onDisable() {
		instance = null;
		getLogger().info("ChatCitizen has been disabled");
	}

	public Plugin(){
		super();
		if(instance!=null)
			throw new RuntimeException("oi! only one instance!");
	}

	@Override
	public void onEnable() {
		instance = this;
		//check if Citizens is present and enabled.

		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().severe("Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}	
		
		new ChatEventListener(this);
		

		//Register your trait with Citizens.        
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(ChatTrait.class));	

		saveDefaultConfig();
		loadBots();
		getLogger().info("ChatCitizen has been enabled");
	}
	
	public void loadBots(){
		FileConfiguration c = this.getConfig();
		ConfigurationSection bots = c.getConfigurationSection("bots");
		if(bots==null){
			throw new RuntimeException("No bots section in config");
		}
		for(String name : bots.getKeys(false)){
			String confpath = bots.getString(name);
			log("Loading bot "+name+" from path "+confpath);
			this.bots.put(name,new ChatterWrapper(confpath));
		}
		log("Bots all loaded.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String cn = command.getName();
		if(cn.equals("chattest")){
			log("All going swimmingly so far");
			return true;
		}
		return false;
	}
	
	public ChatterWrapper getBot(String s){
		if(bots.containsKey(s)){
			return bots.get(s);
		} else
			throw new RuntimeException("Bot "+s+" not found - is it in the config?");
	}

	List<NPC> chatters = new ArrayList<NPC>();
	public void addChatter(NPC npc) {
		chatters.add(npc);
	}
	public void removeChatter(NPC npc){
		chatters.remove(npc);
	}
	
	public static boolean isNear(Location a,Location b){
		return (a.distance(b)<10 && Math.abs(a.getY()-b.getY())<2);
	}
	
	public void handleMessage(Location l, String msg){
		for(NPC npc: chatters){
			Location npcl = npc.getEntity().getLocation();
			if(isNear(l,npcl)){
				ChatTrait ct = npc.getTrait(ChatTrait.class);
				ct.respondTo(msg);
			}
		}
	}

}
