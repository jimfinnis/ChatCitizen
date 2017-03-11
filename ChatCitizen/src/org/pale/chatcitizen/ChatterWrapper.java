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

	public void switchNPC(NPC npc){
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
			cachedNPC = npc;
		}
	}

	public void setProperty(NPC npc,String s,Object o){
		if(!contexts.containsKey(npc.getId()))
			throw new RuntimeException("cannot set property \""+s+"\" in bot \""+path+"\" for npc \""+npc.getFullName()+"\" - no context.");
		contexts.get(npc.getId()).property(s,o);
	}

	public String respond(NPC npc, String msg) {
		switchNPC(npc);
		return bot.respond(msg);
	}

	public String getName() {
		return name;
	}
}
