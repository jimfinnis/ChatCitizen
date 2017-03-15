package org.pale.chatcitizen;

import java.util.HashMap;
import java.util.Map;

import net.citizensnpcs.api.npc.NPC;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

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
	private Map<Integer,Chat> chats= new HashMap<Integer,Chat>();

	public ChatterWrapper(String name,String path){
		bot = new Bot(name,path);
		this.path = path;
		this.name = name;
		
		// look for special categories the bot might need to have for spontaneous speech, etc.
		bot.getSpecialCategoriesPresent("randsay","greetsay","entityhitme","playerhitme");
	}
	public boolean hasSpecialCategory(String s){
		return bot.hasSpecialCategory(s);
	}

	public synchronized Chat getChat(NPC npc){ // synch - more than one chatbot might be using this!
		Chat c;
		if(chats.containsKey(npc.getId())){
			c = chats.get(npc.getId());
		} else {
			Plugin.log("Creating new chat, bot="+path+", npc="+npc.getFullName());
			c = new Chat(bot);
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

	public synchronized String respond(NPC npc, String msg) {// synch - more than one chatbot might be using this!
		return getChat(npc).multisentenceRespond(msg);
	}

	public String getName() {
		return name;
	}
}
