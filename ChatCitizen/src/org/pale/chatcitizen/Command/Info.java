package org.pale.chatcitizen.Command;

import org.bukkit.command.CommandSender;
import org.pale.chatcitizen.ChatTrait;
import org.pale.chatcitizen.CommandAction;
import org.pale.chatcitizen.Plugin;

public class Info extends CommandAction {

	@Override
	public void run(CommandSender sender, String[] args) {
		ChatTrait ct = Plugin.getChatCitizenFor(sender);
		if(ct==null)
			Plugin.sendCmdMessage(sender, "No ChatCitizen selected");
		else {
			String [] a;
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
			Plugin.sendCmdMessage(sender, b.toString());
		}
	}

}
