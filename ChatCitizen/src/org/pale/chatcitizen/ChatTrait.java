package org.pale.chatcitizen;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.Messaging;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;


//This is your trait that will be applied to a npc using the /trait mytraitname command. 
//Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
@TraitName("chatcitizen") // convenience annotation in recent CitizensAPI versions for specifying trait name
public class ChatTrait extends Trait {
	public ChatTrait() {
		super("chatcitizen");
		plugin = JavaPlugin.getPlugin(Plugin.class);
	}

	Plugin plugin = null;
	@Persist String botName = null;
	
	boolean SomeSetting = false;

	// the actual chatbot
	private ChatterWrapper bot;

	// Here you should load up any values you have previously saved (optional). 
	// This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
	// This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
	// This is called BEFORE onSpawn, npc.getBukkitEntity() will return null.
	public void load(DataKey key) {
		SomeSetting = key.getBoolean("SomeSetting", false);
	}

	// Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
	public void save(DataKey key) {
		key.setBoolean("SomeSetting",SomeSetting);
	}

	// An example event handler. All traits will be registered automatically as Bukkit Listeners.
	@EventHandler
	public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
		//Handle a click on a NPC. The event has a getNPC() method. 
		//Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
		if(event.getNPC() == this.getNPC()){
			Messaging.send(event.getClicker(), "oi!");
			Plugin.log("Click.");
		}
	}
	@EventHandler
	public void punch(net.citizensnpcs.api.event.NPCLeftClickEvent event){
		//Handle a click on a NPC. The event has a getNPC() method. 
		//Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
		if(event.getNPC() == this.getNPC()){
			Messaging.send(event.getClicker(), "OW!");
			Plugin.log("Punch.");
		}
	}
	

	// Called every tick
	@Override
	public void run() {
	}

	//Run code when your trait is attached to a NPC. 
	//This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
	//This would be a good place to load configurable defaults for new NPCs.
	@Override
	public void onAttach() {
		botName = "default";
		plugin.getServer().getLogger().info(npc.getName() + " has been assigned ChatCitizen!");
	}
	
	public void setBot(ChatterWrapper b){
		b.setProperty(npc,"botname",npc.getFullName());		
		bot = b;
		botName = b.getName();
	}

	// Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getBukkitEntity() is still valid.
	@Override
	public void onDespawn() {
		Plugin.log(" Despawn run on "+npc.getFullName());
		bot=null;
		plugin.removeChatter(npc);
	}

	//Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
	//This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {
		if(botName==null)botName="default"; // this really shouldn't be required.
		ChatterWrapper b = plugin.getBot(botName);
		if(b==null)
			throw new RuntimeException("bot \""+botName+"\" not found - is it in the config?");
		
		setBot(b);

		plugin.addChatter(npc);
		Plugin.log(" Spawn run on "+npc.getFullName());
	}

	//run code when the NPC is removed. Use this to tear down any repeating tasks.
	@Override
	public void onRemove() {
	}

	public void respondTo(String msg) {
		String botresp = bot.respond(npc,msg);
		Plugin.log("Bot response: "+botresp);
		String response = ChatColor.AQUA+"["+npc.getFullName()+"] "+ChatColor.WHITE+botresp;
		for(Player p: plugin.getServer().getOnlinePlayers()){
			if(Plugin.isNear(p.getLocation(), npc.getEntity().getLocation())){
				p.sendMessage(response);
			}
		}
		
	}

	/**
	 * Set properties within the chat bot based on the player who has just spoken to it.
	 * @param player
	 */
	public void setPropertiesForSender(Player player) {
		bot.setProperty(npc, "name", player.getDisplayName());
	}

}
