package org.pale.chatcitizen;

import java.util.Set;

import net.citizensnpcs.api.npc.NPC;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.AIMLProcessorExtension;
import org.alicebot.ab.ParseState;
import org.alicebot.ab.Utilities;
import org.pale.chatcitizen.plugininterfaces.NPCDestinations;
import org.pale.chatcitizen.plugininterfaces.Sentinel;
import org.pale.chatcitizen.plugininterfaces.Sentinel.SentinelData;
import org.w3c.dom.Node;

public class ChatBotAIMLExtension implements AIMLProcessorExtension {
	public Set<String> extensionTagNames = Utilities.stringSet("mctime","npcdest","sentinel");
	public Set <String> extensionTagSet() {
		return extensionTagNames;
	}

	private ChatTrait getTrait(NPC npc){
		return npc.getTrait(ChatTrait.class);
	}


	@Override
	public String recursEval(Node node, ParseState ps) {
		try {
			String nodeName = node.getNodeName();
			if (nodeName.equals("mctime"))
				return mctime(node, ps);
			else if(nodeName.equals("npcdest"))
				return npcdest(node,ps);
			else if(nodeName.equals("sentinel"))
				return sentinel(node,ps);
			else return (AIMLProcessor.genericXML(node, ps));
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	private String npcdest(Node node, ParseState ps) {
		String cmd = AIMLProcessor.getAttributeOrTagValue(node, ps, "cmd");
		ChatTrait t = getTrait(ps.chatSession.npc);
		NPCDestinations.NPCDestData d = t.nddat;
		if(d==null){
			return "NO";
		}
		if(cmd.equalsIgnoreCase("go")){
			String name = AIMLProcessor.getAttributeOrTagValue(node, ps, "loc");
			if(name == null){
				Plugin.warn("No loc tag in <npcdest cmd=\"go\"> for NPC: "+d.npc.getFullName());
				return "NO";
			}
			String stime = AIMLProcessor.getAttributeOrTagValue(node, ps, "time");
			long time;
			if(stime == null)
				time = 86400*1000; //1 day
			else
				time = Long.parseLong(stime);

			return d.go(name, time);
		} else
			return "NO";
	}
	
	private String sentinel(Node node, ParseState ps){
		String cmd = AIMLProcessor.getAttributeOrTagValue(node, ps, "cmd");
		Sentinel.SentinelData d = Plugin.getInstance().sentinelPlugin.makeData(ps.chatSession.npc);
		if(d==null){
			return "NO";
		} else {
			if(cmd.equalsIgnoreCase("timeSinceAttack")){
				return Long.toString(d.timeSinceAttack);
			} else if(cmd.equalsIgnoreCase("timeSinceSpawn")){
				return Long.toString(d.timeSinceSpawn);
			} else if(cmd.equalsIgnoreCase("guarding")){
				return d.guarding;
			} else 
				return "NO";
		}
	}


	// <mctime type=".."/> get Minecraft time in several formats:
	private String mctime(Node node, ParseState ps) {
		String type = AIMLProcessor.getAttributeOrTagValue(node, ps, "type");
		long t = ps.chatSession.npc.getEntity().getWorld().getTime();
		int hours = (int) ((t / 1000 + 8) % 24);
		int minutes = (int) (60 * (t % 1000) / 1000);
		if(type==null)type="digital";
		if(type.equals("digital")){
			return String.format("%02d:%02d", hours,minutes);
		} else if(type.equals("todstring")) {
			if(t>22000 || t<6000)
				return "morning";
			if(t>=6000 && t<11500)
				return "afternoon";
			if(t>=11500 && t<15000)
				return "evening";
			else return "night";
		} else if(type.equals("approx")){
			if (t > 22700 || t <= 450) {
				return "dawn";	
			} else if (t > 4000 && t <= 8000) {
				return "noon";
			} else if (t > 11500 && t <= 13500) {
				return "dusk";
			} else if (t > 16000 && t <= 20000) {
				return "midnight";
			} else if (t > 12000) {
				return "night";
			} else 
				return "day";
		} else return Long.toString(t);
	}


}
