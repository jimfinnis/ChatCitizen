package org.pale.chatcitizen;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.citizensnpcs.api.npc.NPC;

import org.alicebot.ab.AIMLMap;
import org.alicebot.ab.AIMLSet;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.utils.IOUtils;
import org.bukkit.entity.Player;

/**
 * This class wraps a single chatbot so that it can be reused in
 * multiple conversations by switching its context. With Program AB we
 * do this by using a Chat for each NPC.
 * 
 * @author white
 *
 */
public class ChatterWrapper {
	private Bot bot;
	private String path;
	private String name;
	private Map<Integer,Chat> chats;
	private Map<String,SubBotData> subbots = new HashMap<String,SubBotData>();

	public ChatterWrapper(String name,String path){
		this.path = path;
		this.name = name;
		reload();
	}
	public boolean hasSpecialCategory(String s){
		return bot.hasSpecialCategory(s);
	}

	public Collection<Chat> getChats(){
		return chats.values();
	}

	/**
	 * Discard and reload this bot and clear all chats (and all properties therein).
	 */
	public void reload(){
		bot = new Bot(name,path);
		bot.getSpecialCategoriesPresent("randsay","greetsay","entityhitme","playerhitme","hitsomething","rightclick");
		chats= new HashMap<Integer,Chat>();
		
		// now we load the sets and maps for different "versions" of this bot that know different things.
		// We look for all "subbot" files.
		File dir = new File(path+"/subbots");
		if(dir.exists()){
			File[] list = IOUtils.listFiles(dir);
			for(File f: list){
				if(f.isFile()){
					String fn = f.getName();
					if(fn.endsWith(".yml")){
						SubBotData d = new SubBotData(f.getAbsolutePath());
						String bn = fn.replace(".yml", "");
						subbots.put(bn,d);
						Plugin.log("Loaded subbot "+bn+" from file "+fn);
					}
				}
			}
		}
	}
	
	public SubBotData getSubBot(String s){
		return subbots.get(s);
	}

	public synchronized Chat getChat(NPC npc){ // synch - more than one chatbot might be using this!
		Chat c;
		if(chats.containsKey(npc.getId())){
			c = chats.get(npc.getId());
		} else {
			Plugin.log("Creating new chat, bot="+path+", npc="+npc.getFullName());
			c = new Chat(bot,npc,npc.getTrait(ChatTrait.class));
			chats.put(npc.getId(), c);
		}
		return c;
	}

	/**
	 * Set a property in the context of a given chat bot (as indicated by the NPC)
	 * @param npc the Citizens NPC
	 * @param s the name of the property
	 * @param o the property 
	 */
	public synchronized void setProperty(NPC npc,String s,String o){// synch - more than one chatbot might be using this!
		Chat c = getChat(npc);
		Plugin.log("Property set in "+c.toString()+", "+s+"="+o);
		c.predicates.put(s,o);
		//		c.dumpProperties(Plugin.getInstance().getLogger());
	}

	/**
	 * respond to an utterance from a player (which may be null if the NPC is just saying something)
	 * @param p
	 * @param npc
	 * @param msg
	 * @return
	 */
	public synchronized String respond(Player p,NPC npc, String msg) {// synch - more than one chatbot might be using this!
		return getChat(npc).multisentenceRespond(p,msg);
	}

	public String getName() {
		return name;
	}
}
