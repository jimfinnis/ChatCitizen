package org.pale.chatcitizen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.alicebot.ab.AIMLProcessor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.pale.chatcitizen.plugininterfaces.NPCDestinations;



public class Plugin extends JavaPlugin {
	public static void log(String msg) {
		getInstance().getLogger().info(msg);
	}
	public static void warn(String msg) {
		getInstance().getLogger().warning(msg);
	}
	/**
	 * Make the plugin a weird singleton.
	 */
	static Plugin instance = null;

	/**
	 * All the bot wrappers - the Traits share the bots.
	 */
	private Map<String,ChatterWrapper> bots = new HashMap<String,ChatterWrapper>();

	public NPCDestinations ndPlugin;

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
		
		// check other optional plugins
		ndPlugin = new NPCDestinations();
		
		// initialise AIML extensions
		AIMLProcessor.extension = new ChatBotAIMLExtension();

		// this is the listener for pretty much ALL events EXCEPT NPC events, not just chat.
		new ChatEventListener(this);


		//Register.        
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(ChatTrait.class));	

		saveDefaultConfig();
		loadBots();
		autoRegisterCommands();
		
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
			this.bots.put(name,new ChatterWrapper(name,confpath));
		}
		log("Bots all loaded.");
	}

	public static void sendCmdMessage(CommandSender s,String msg){
		s.sendMessage(ChatColor.AQUA+"[ChatCitizen] "+ChatColor.YELLOW+msg);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String cn = command.getName();
		if(cn.equals("chatcitizen")){
			if(args.length == 0){
				String cmds="";
				for(String cc: cmdMap.keySet())cmds+=cc+" ";
				sendCmdMessage(sender,cmds);
				return true;
			}
			String cmdName = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);
			if(!cmdMap.containsKey(cmdName)){
				sendCmdMessage(sender,"unknown chatcitizen command: "+cmdName);
				return true;
			}
			CommandAction ca = cmdMap.get(cmdName);
			ca.execute(sender,args);
			return true;
		}
		return false;
	}

	public ChatterWrapper getBot(String s){
		if(bots.containsKey(s)){
			return bots.get(s);
		} else return null;
	}

	List<NPC> chatters = new ArrayList<NPC>();

	private TreeMap<String, CommandAction> cmdMap = new TreeMap<String,CommandAction>();
	public void addChatter(NPC npc) {
		chatters.add(npc);
	}
	public void removeChatter(NPC npc){
		chatters.remove(npc);
	}

	public static boolean isNear(Location a,Location b,double dist){
		return (a.distance(b)<5 && Math.abs(a.getY()-b.getY())<2);
	}

	public void handleMessage(Player player, String msg){
		Location playerloc = player.getLocation();
		for(NPC npc: chatters){
			Location npcl = npc.getEntity().getLocation();
			if(isNear(playerloc,npcl,2)){ // chatters assume <2m and you're talking to them.
				if(npc.hasTrait(ChatTrait.class)){
					ChatTrait ct = npc.getTrait(ChatTrait.class);
					ct.setPropertiesForSender(player);
					ct.respondTo(player,msg);
				}
			}
		}
	}
	private void autoRegisterCommands(){
		FileConfiguration conf = new ConfigAccessor("commands.yml").getConfig();

		ConfigurationSection cmds = conf.getRoot();

		for(String s: cmds.getKeys(false)){
			ConfigurationSection sec = cmds.getConfigurationSection(s);
			for(String ss: sec.getKeys(false)){
				Object o = sec.get(ss);
				log("Command "+s+" Thing " + ss + " type "+o.getClass().getCanonicalName());
			}

			// get the usage details from plugin.yml
			String usage;
			int argc;
			if(sec.contains("usage") && sec.get("usage") instanceof String)
				usage = sec.getString("usage");
			else
				usage = "??";
			if(sec.contains("argc") && sec.get("argc") instanceof Integer)
				argc = sec.getInt("argc");
			else
				argc = -1; // default is varargs.

			// try to find the class
			String className = "org.pale.chatcitizen.Command."+s.substring(0, 1).toUpperCase() + s.substring(1);
			try {
				Class<?> cl = Class.forName(className);
				// instantiate and register
				CommandAction ca = (CommandAction)(cl.newInstance());
				ca.init(argc,usage);
				cmdMap.put(s, ca);
			} catch (ClassNotFoundException e) {
				log("class not found for command: "+s+" ["+className+"]");
			} catch (InstantiationException e) {
				log("cannot instantiate: "+className);
			} catch (IllegalAccessException e) {
				log("illegal access: "+className);
				e.printStackTrace();
			}
		}
	}
	
	public void showCommandHelp(CommandSender sender){
		for(String ss: cmdMap.keySet()){
			CommandAction ca = cmdMap.get(ss);
			sendCmdMessage(sender, ca.getUsage());
		}
	}

	public static ChatTrait getChatCitizenFor(CommandSender sender) {
		NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
		if (npc == null) {
			return null;
		}
		if (npc.hasTrait(ChatTrait.class)) {
			return npc.getTrait(ChatTrait.class);
		}
		return null;
	}
	public void reloadAllBots() {
		for(ChatterWrapper b : bots.values()){
			b.reload();
		}
	}	
}
