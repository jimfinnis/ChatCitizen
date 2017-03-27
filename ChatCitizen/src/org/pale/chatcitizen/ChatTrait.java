package org.pale.chatcitizen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.Messaging;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.pale.chatcitizen.plugininterfaces.NPCDestinations;


//This is your trait that will be applied to a npc using the /trait mytraitname command. 
//Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
@TraitName("chatcitizen") // convenience annotation in recent CitizensAPI versions for specifying trait name
public class ChatTrait extends Trait {

	private static final long sayCheckInterval = 5000; //!< how often (in ms) we check random say.

	/**
	 * This will point to some data if we have NPCDestinations.
	 */
	public NPCDestinations.NPCDestData nddat;

	public ChatTrait() {
		super("chatcitizen");
		plugin = JavaPlugin.getPlugin(Plugin.class);
	}

	static Random rand = new Random();

	Plugin plugin = null;
	@Persist String botName = null; //!< name of the bot in config.yml

	/**
	 * In the discussion below, "dist" means horizontal XZ distance, Y distance must always be < 2.
	 * The bot will say RANDSAY to the player 
	 *  - if sayInterval has passed since the last time something was said
	 *  - if random [0:1] < sayProbability (checked when the last check passes)
	 * 	- if dist < sayDist for some player (picked at random)
	 * Will do nothing if the pattern RANDSAY has no category. 
	 */
	@Persist public double sayInterval = 20; //!< min time between the bot saying stuff randomly
	@Persist public double sayProbability = 0.3; //!< chance the NPC will try to speak each sayInterval
	@Persist public double sayDist = 10; //!< how far the bot will look for someone to randomly talk at.
	/**
	 * Say GREETSAY when the dist (see above) drops below greetDist having been above greetDist for
	 * greetTime seconds.
	 * Will do nothing if the pattern GREETSAY has no category. 
	 */
	@Persist public double greetDist = 3; //!< how close a player should be before greet
	@Persist public double greetInterval = 20; //!< how long between greeting each player
	@Persist public double greetProbability = 0.9; //!< how likely is it we will greet a player? If this fails, we just ignore them.

	@Persist public double audibleDistance=10; //!< how far this robot is audible

	private boolean hasGreetSay,hasRandSay,hasEntityHitMe,hasPlayerHitMe,hasHitSomething,hasRightClick;



	// example setting.
	boolean SomeSetting = false;

	// the actual chatbot
	private ChatterWrapper bot;
	private long lastRandSay;


	public String getBotName(){
		return botName;
	}

	List<Player> getNearPlayers(double d){
		List<Player> r = new ArrayList<Player>();
		for(Entity e: npc.getEntity().getNearbyEntities(d,d,d)){
			if(e instanceof Player){
				r.add((Player)e);
			}
		}
		return r;
	}

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

	@EventHandler
	public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
		//Handle a click on a NPC. The event has a getNPC() method. 
		//Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
		if(event.getNPC() == this.getNPC()){
			// this is where we trap a "give" action or suchlike
			Player p = event.getClicker();
			ItemStack held = p.getInventory().getItemInMainHand();
			// shouldn't be necessary, but it does seem odd that an empty hand is full of air...
			String hstr = (held==null)?"air":held.getType().toString();
			bot.setProperty(npc, "itemheld", hstr);
			respondTo(p,"RIGHTCLICK");
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void monitorDamageFromEntity(final net.citizensnpcs.api.event.NPCDamageByEntityEvent e){
		if(e.getNPC() == this.getNPC()){
			Entity bastard = e.getDamager();
			if(bastard instanceof Player){
				setPropertiesForSender((Player)bastard);
				if(hasPlayerHitMe)respondTo((Player)bastard,"PLAYERHITME");
			} else {
				if(hasEntityHitMe)sayToAll("ENTITYHITME");
			}
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void monitorDamageEntity(final net.citizensnpcs.api.event.NPCDamageEntityEvent e){
		if(e.getNPC() == this.getNPC()){
			if(hasHitSomething)sayToAll("HITSOMETHING");
		}
	}



	private int tickint=0;
	public long timeSpawned=0;

	// Called every tick
	@Override
	public void run() {
		if(tickint++==20){ // to reduce CPU usage
			processRandSay();
			processGreetSay();
			tickint=0;
		}
		timeSpawned++;
	}

	//Run code when your trait is attached to a NPC. 
	//This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
	//This would be a good place to load configurable defaults for new NPCs.
	@Override
	public void onAttach() {
		botName = "default";
		plugin.getServer().getLogger().info(npc.getName() + " has been assigned ChatCitizen!");
		// set up the NPCDestinations data (if present)
		nddat = plugin.ndPlugin.makeData(npc);
	}

	public void setBot(ChatterWrapper b){
		b.setProperty(npc,"botname",npc.getFullName());		
		bot = b;
		botName = b.getName();

		hasGreetSay = bot.hasSpecialCategory("greetsay");
		hasRandSay = bot.hasSpecialCategory("randsay");
		hasEntityHitMe = bot.hasSpecialCategory("entityhitme");
		hasPlayerHitMe = bot.hasSpecialCategory("playerhitme");
		hasHitSomething = bot.hasSpecialCategory("hitsomething");
		hasRightClick = bot.hasSpecialCategory("hasrightclick");
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

	/**
	 * Generate and send a response to a list of players. 
	 */
	private void say(String toName,String pattern){
		List<Player> q = getNearPlayers(audibleDistance);
		if(q.size()>0){
			String msg = bot.respond(npc, pattern);
			if(msg.trim().length()!=0){
				String s = ChatColor.AQUA+"["+npc.getFullName()+" -> "+toName+"] "+ChatColor.WHITE+msg;
				for(Player p: getNearPlayers(audibleDistance)){
					p.sendMessage(s);
				}
			}
		}
	}

	/**
	 * Respond to a player saying something nearby. Alternatively used to just say something randomly,
	 * in which case the player argument is to whom it should be said and the string is a special pattern (like RANDSAY).
	 * @param player the player who spoke
	 * @param msg what they said
	 */
	public void respondTo(Player player,String input) {
		say(player.getDisplayName(),input);
	}

	/**
	 * Say something (typically a spontaneous speech) to everyone nearby. The msg is passed to be bot,
	 * and should be a special (RANDSAY etc.). 
	 */
	public void sayToAll(String pattern){
		say("(nearby)",pattern);
	}

	/**
	 * Set properties within the chat bot based on the player who has just spoken to it.
	 * @param player
	 */
	public void setPropertiesForSender(Player player) {
		bot.setProperty(npc, "name", player.getDisplayName());
	}

	private long lastSayCheckIntervalTime=0;
	private void processRandSay(){
		long t = System.currentTimeMillis();
		if(hasRandSay && (t-lastSayCheckIntervalTime > sayCheckInterval)){
			if((t-lastRandSay > sayInterval*1000) && (rand.nextDouble()<sayProbability)){
				// try to find someone to talk to
				List<Player> ps  = getNearPlayers(sayDist);
				if(ps.size() > 0){
					Player p = ps.get(rand.nextInt(ps.size()));
					respondTo(p,"RANDSAY");
					lastRandSay = t;
				}
			}
			lastSayCheckIntervalTime = t;
		}
	}

	Map<String,Long> lastGreeted = new HashMap<String,Long>();

	List<Player> nearPlayersForGreet  = new ArrayList<Player>();
	private void processGreetSay(){
		if(hasGreetSay){
			List<Player> nearPlayersNew = getNearPlayers(greetDist);
			for(Player p : nearPlayersNew){
				// is this someone who has just appeared?
				if(!nearPlayersForGreet.contains(p)){
					long lasttime;
					long t = System.currentTimeMillis();
					if(lastGreeted.containsKey(p.getName()))
						lasttime = lastGreeted.get(p.getName());
					else
						lasttime = 0;
					// we didn't greet them recently; let's do that.
					if(t-lasttime > greetInterval*1000){
						if(rand.nextDouble()<greetProbability){
							setPropertiesForSender(p);
							respondTo(p,"GREETSAY");
						}
						lastGreeted.put(p.getName(), t);
					}
				}
			}
			nearPlayersForGreet = nearPlayersNew;
		}
	}

	/**
	 * Used in the "t" test command
	 * @param msg
	 * @return
	 */
	public String getResponseTest(String msg) {
		return bot.respond(npc, msg);
	}
}
