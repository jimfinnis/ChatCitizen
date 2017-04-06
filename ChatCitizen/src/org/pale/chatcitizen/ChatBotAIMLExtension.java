package org.pale.chatcitizen;

import java.util.HashSet;
import java.util.Set;

import net.citizensnpcs.api.npc.NPC;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.AIMLProcessorExtension;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.ParseState;
import org.alicebot.ab.Utilities;
import org.pale.chatcitizen.plugininterfaces.NPCDestinations;
import org.pale.chatcitizen.plugininterfaces.Sentinel;
import org.pale.chatcitizen.plugininterfaces.Sentinel.SentinelData;
import org.w3c.dom.Node;

public class ChatBotAIMLExtension implements AIMLProcessorExtension {
	public Set<String> extensionTagNames = Utilities.stringSet("mctime","npcdest","sentinel","clean",
			"insbset","randsbset","getsb",
			"setpl","getpl","debug");
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
			else if(nodeName.equals("getpl"))
				return getpl(node,ps);
			else if(nodeName.equals("setpl"))
				return setpl(node,ps);
			else if(nodeName.equals("clean"))
				return clean(node,ps);
			else if(nodeName.equals("debug"))
				return debug(node,ps);
			else if(nodeName.equals("insbset"))
				return insbset(node,ps);
			else if(nodeName.equals("randsbset"))
				return randsbset(node,ps);
			else if(nodeName.equals("getsb"))
				return getsb(node,ps);
			else return (AIMLProcessor.genericXML(node, ps));
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	private String debug(Node node, ParseState ps) {
		String r = AIMLProcessor.evalTagContent(node, ps,null);
		Plugin.log("DEBUG: **"+r+"**");
		return r;
	}


	private String clean(Node node, ParseState ps) {
		String r = AIMLProcessor.evalTagContent(node, ps, Utilities.stringSet("opts")).trim();
		String opts = AIMLProcessor.getAttributeOrTagValue(node, ps, "opts");
		r = r.replaceAll("\\s+", " "); // replace mult. whitespace with space
		r = r.replaceAll("\\.(\\w)", ". $1"); // replace "." followed by word char with ". "
		r = r.replaceAll("\\s+\\.", "."); // replace "  ." with "."
		if(opts!=null){
			if(opts.contains("s")) // capitalize initial if true, like <sentence><clean>...
				r= (r.length() > 1) ? r.substring(0, 1).toUpperCase()+r.substring(1, r.length()) : "";
		}
		return r;
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
			} else if(cmd.equalsIgnoreCase("guarding")){ // playername, "something" or "nothing"
				return d.guarding;
			} else if(cmd.equalsIgnoreCase("health")){
				return Double.toString(d.health); // percentage of max
			} else if(cmd.equalsIgnoreCase("debug")){
				return d.debug;
			} else 
				return "NO";
		}
	}


	// <mctime type=".."/> get Minecraft time in several formats:
	private String mctime(Node node, ParseState ps) {
		String type = AIMLProcessor.getAttributeOrTagValue(node, ps, "type");
		long t = ps.chatSession.npc.getEntity().getWorld().getTime();
		int hours = (int) ((t / 1000 + 6) % 24);
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
	
	// is an item in a subbot set for the current bot?
	/// <insbset set="birds" yes=".." no="..">ITEM</insbset>
	// yes or no default to YES or NO.
	
	private String insbset(Node node,ParseState ps){
        HashSet<String> attributeNames = Utilities.stringSet("set","yes","no");
        String setName = AIMLProcessor.getAttributeOrTagValue(node, ps, "set");
        String no = AIMLProcessor.getAttributeOrTagValue(node, ps, "no");
        if(no==null)no="NO";
        if(setName!=null){
            String yes = AIMLProcessor.getAttributeOrTagValue(node, ps, "yes"); 
            if(yes==null)yes="YES";
        	String result = AIMLProcessor.evalTagContent(node, ps, attributeNames).trim();
        	result = result.replaceAll("(\r\n|\n\r|\r|\n)", " ");
        	String item=result.trim();
        	ChatTrait t = getTrait(ps.chatSession.npc);
        	SubBotData sb = t.getSubBot();
        	if(sb!=null)
        		return sb.isInSet(setName, item) ? yes : no;
        }
        
        return no;
	}
	

	// get random item from a subbot set <randsbset set="birds"/>
	private String randsbset(Node node,ParseState ps){
        HashSet<String> attributeNames = Utilities.stringSet("set");
        String setName = AIMLProcessor.getAttributeOrTagValue(node, ps, "set");
        if(setName!=null){
        	Plugin.log("GOT SET NAME "+setName);
        	ChatTrait t = getTrait(ps.chatSession.npc);
        	SubBotData sb = t.getSubBot();
        	if(sb!=null)
        		return sb.randFromSet(setName);
        }
        
        return "";
	}

	// get item from a subbot map <sbget name="..."/>
	private String getsb(Node node,ParseState ps){
        HashSet<String> attributeNames = Utilities.stringSet("name");
        String name = AIMLProcessor.getAttributeOrTagValue(node, ps, "name");
        Plugin.log("GOT MAP KEY: "+name);
        if(name!=null){
        	ChatTrait t = getTrait(ps.chatSession.npc);
        	SubBotData sb = t.getSubBot();
        	if(sb!=null)
        		return sb.getFromMap(name);
        }
        
        return "";
	}

	private String setpl(Node node, ParseState ps) {
        HashSet<String> attributeNames = Utilities.stringSet("name");
        String predicateName = AIMLProcessor.getAttributeOrTagValue(node, ps, "name");
        String result = AIMLProcessor.evalTagContent(node, ps, attributeNames).trim();
        result = result.replaceAll("(\r\n|\n\r|\r|\n)", " ");
        String value=result.trim();
        if (predicateName != null) {
        	ChatTrait t = getTrait(ps.chatSession.npc);
        	t.setPlayerPredicate(predicateName,value);
			ps.chatSession.predicates.put(predicateName, result);
		}
		return result;
    }

    /** get the value of an AIML predicate.
     * implements <get name="predicate"></get>  and <get var="varname"></get>
     *
     * @param node     current XML parse node
     * @param ps       AIML parse state
     * @return         the result of the <get> operation
     */
    private String getpl(Node node, ParseState ps) {
        String result = MagicStrings.default_get;
        String predicateName = AIMLProcessor.getAttributeOrTagValue(node, ps, "name");
        if (predicateName != null){
        	ChatTrait t = getTrait(ps.chatSession.npc);
            result = t.getPlayerPredicate(predicateName).trim();        	
        }
		//MagicBooleans.trace("in AIMLProcessor.get, returning: " + result);
        return result;
    }

}
