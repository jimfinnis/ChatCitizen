package org.pale.chatcitizen;

import java.util.Set;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.AIMLProcessorExtension;
import org.alicebot.ab.ParseState;
import org.alicebot.ab.Utilities;
import org.w3c.dom.Node;

public class ChatBotAIMLExtension implements AIMLProcessorExtension {
    public Set<String> extensionTagNames = Utilities.stringSet("mctime");
    public Set <String> extensionTagSet() {
        return extensionTagNames;
    }


	@Override
	public String recursEval(Node node, ParseState ps) {
        try {
            String nodeName = node.getNodeName();
            if (nodeName.equals("mctime"))
                return mctime(node, ps);
            else return (AIMLProcessor.genericXML(node, ps));
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
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
