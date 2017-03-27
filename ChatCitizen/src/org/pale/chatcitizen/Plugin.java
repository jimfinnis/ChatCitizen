package org.pale.chatcitizen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.pale.chatcitizen.plugininterfaces.NPCDestinations;
import org.pale.chatcitizen.plugininterfaces.Sentinel;
import org.pale.chatcitizen.Command.CallInfo;
import org.pale.chatcitizen.Command.Cmd;
import org.pale.chatcitizen.Command.Registry;



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
	public Sentinel sentinelPlugin;
	
	private Registry commandRegistry=new Registry();

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
		
		sentinelPlugin = new Sentinel();

		// initialise AIML extensions
		AIMLProcessor.extension = new ChatBotAIMLExtension();

		// this is the listener for pretty much ALL events EXCEPT NPC events, not just chat.
		new ChatEventListener(this);


		//Register.        
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(ChatTrait.class));	

		saveDefaultConfig();
		loadBots();
		commandRegistry.register(this); // register commands

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
			commandRegistry.handleCommand(sender, args);
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

	/**
	 * Commands
	 */

	@Cmd(desc="get info on a bot",argc=0,usage="<npcname>",cz=true)
	public void info(CallInfo c){
		String [] a;
		ChatTrait ct = c.getCitizen();
		a = new String[] {
				"Bot name = "+ct.getBotName(),
				"Random speech distance [saydist] = "+ct.sayDist,
				"Random speech interval [sayint] = "+ct.sayInterval,
				"Random speech chance [sayprob] = "+(int)(ct.sayProbability*100),
				"Greet distance [greetdist] = "+ct.greetDist,
				"Greet interval [greetint] = "+ct.greetInterval,
				"Greet chance = [greetprob] "+(int)(ct.greetProbability*100),
				"Audible distance [auddist] = "+ct.audibleDistance
		};

		StringBuilder b = new StringBuilder();
		for(String s: a){
			b.append(s);b.append("\n");
		}
		c.msg(b.toString());
	}

	@Cmd(desc="reload all bots",argc=0,permission="chatcitizen.reloadall")
	public void reloadall(CallInfo c){
		for(ChatterWrapper b : bots.values()){
			b.reload();
		}		
	}
	
	@Cmd(desc="reload a given bot",argc=1,permission="chatcitizen.reload",usage="[botname]")
	public void reload(CallInfo c){
		String n = c.getArgs()[0];
		if(!bots.containsKey(n)){
			c.msg("Bot not known. List bots with \"ccz bots\".");
		} else {
			ChatterWrapper b = bots.get(n);
			b.reload();
		}
	}
	
	@Cmd(name="bots",desc="list all bots and which NPCs use them",argc=0)
	public void listBots(CallInfo c){
		for(String s : bots.keySet()){
			ChatterWrapper b = bots.get(s);
			StringBuilder sb = new StringBuilder();
			sb.append(ChatColor.AQUA+s+": "+ChatColor.GREEN);
			for(Chat chat: b.getChats()){
				sb.append(chat.npc.getFullName()+" ");
			}
			c.msg(sb.toString());
		}
	}
	
	@Cmd(name="t",desc="chat test",permission="chatcitizen.test",usage="[string]",cz=true)
	public void testBot(CallInfo c){
		ChatTrait ct = c.getCitizen();
		String msg = "";
		for(String s:c.getArgs())
			msg += s + " ";
		String m = ct.getResponseTest(msg);
		getLogger().info("RESPONSE :"+m);
	}



	private static String[] paramNames={"saydist","sayint","sayprob","greetdist","greetint","greetprob","auddist"};

	// same ordering as paramNames - these are the actual field names!
	private static String[] paramFields={"sayDist","sayInterval","sayProbability","greetDist","greetInterval",
		"greetProbability","audibleDistance"
	};

	@Cmd(desc="set a property in a bot",argc=-1,usage="<property> <value>", cz=true, permission="chatcitizen.set")
	public void set(CallInfo c) {
		if(c.getArgs().length < 2){
			StringBuilder b = new StringBuilder();
			b.append("Parameters are: ");
			for(String s: paramNames){
				b.append(s);b.append(" ");
			}
			c.msg(b.toString());
		} else {
			ChatTrait ct = c.getCitizen();
			String[] args=c.getArgs();
			for(int i=0;i<paramNames.length;i++){
				if(args[0].equals(paramNames[i])){
					try {
						Field f = ChatTrait.class.getDeclaredField(paramFields[i]);
						double val = Double.parseDouble(args[1]);
						if(paramNames[i].contains("prob")){
							val *= 0.01; // convert "prob"abilities from percentages.
						}
						f.setDouble(ct,val);
					} catch (NumberFormatException e) {
						c.msg("that is not a number");							
					} catch (NoSuchFieldException | SecurityException e) {
						c.msg("no such field - this shouldn't happen:"+paramFields[i]);
					} catch (IllegalArgumentException e) {
						c.msg("probably a type mismatch - this shouldn't happen:"+paramFields[i]);
					} catch (IllegalAccessException e) {
						c.msg("illegal access to field - this shouldn't happen:"+paramFields[i]);
					}
					return; // found and handled, so exit.
				}
			}
			c.msg("No parameter of that name found");
			return;
		}
	}
	
	@Cmd(desc="set a chatbot for an NPC",argc=1,usage="<botname>",cz=true,permission="chatcitizen.set")
	public void setbot(CallInfo c){
		String name = c.getArgs()[0];
		ChatTrait ct = c.getCitizen();
		ChatterWrapper b = Plugin.getInstance().getBot(name);
		if(b==null){
			 c.msg("\""+name+"\" is not installed on this server.");
		} else {
			ct.setBot(b);
			c.msg(ct.getNPC().getFullName()+" is now using bot \""+name+"\".");
		}
	}
	
	@Cmd(desc="show help for a command or list commands",argc=-1,usage="[<command name>]")
	public void help(CallInfo c){
		if(c.getArgs().length==0){
			commandRegistry.listCommands(c);
		} else {
			commandRegistry.showHelp(c,c.getArgs()[0]);
		}
	}
}
