package org.pale.chatcitizen;

import java.util.HashMap;
import java.util.Map;

import net.citizensnpcs.api.npc.NPC;
import bitoflife.chatterbean.ChatterBean;
import bitoflife.chatterbean.Context;

/**
 * This class wraps a single chatbot (ChatterBean) so that it can be reused in
 * multiple conversations by switching its context.
 * @author white
 *
 */
public class ChatterWrapper {
	private ChatterBean bot;
	private Context baseContext;
	private String path;
	private String name;
	private NPC cachedNPC = null;
	private Map<Integer,Context> contexts = new HashMap<Integer,Context>();

	public ChatterWrapper(String name,String path){
		bot = new ChatterBean(path);
		this.path = path;
		this.name = name;
		baseContext = bot.getAliceBot().getContext();
	}

	// switch to the context for the NPC, creating a new one from the base context
	// if required.

	public synchronized void switchNPC(NPC npc){ // synch - more than one chatbot might be using this!
		if(npc!=cachedNPC){
			Context c;
			if(contexts.containsKey(npc.getId())){
				c = contexts.get(npc.getId());
			} else {
				Plugin.log("Creating new context from base, bot="+path+", npc="+npc.getFullName());
				c = new Context(baseContext);
				contexts.put(npc.getId(), c);
			}
			bot.getAliceBot().setContext(c);
			Plugin.log("Context switch to "+c.toString());
			cachedNPC = npc;
			c.dumpProperties(Plugin.getInstance().getLogger());
		}
	}

	public synchronized void setProperty(NPC npc,String s,Object o){// synch - more than one chatbot might be using this!
		if(!contexts.containsKey(npc.getId()))
			throw new RuntimeException("cannot set property \""+s+"\" in bot \""+path+"\" for npc \""+npc.getFullName()+"\" - no context.");
		Context c = contexts.get(npc.getId());
		Plugin.log("Property set in "+c.toString()+", "+s+"="+o.toString());
		c.property("predicate."+s,o); // changeable stuff is prefixed with "predicate.", apparently. See bitoflife.chatterbean.aiml.Get.
		c.dumpProperties(Plugin.getInstance().getLogger());
	}

	public synchronized String respond(NPC npc, String msg) {// synch - more than one chatbot might be using this!
		switchNPC(npc);
		return bot.respond(msg);
	}

	public String getName() {
		return name;
	}
}
