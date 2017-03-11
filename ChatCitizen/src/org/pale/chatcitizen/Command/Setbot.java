package org.pale.chatcitizen.Command;

import org.bukkit.command.CommandSender;
import org.pale.chatcitizen.ChatTrait;
import org.pale.chatcitizen.ChatterWrapper;
import org.pale.chatcitizen.CommandAction;
import org.pale.chatcitizen.Plugin;

public class Setbot extends CommandAction {

	@Override
	public void run(CommandSender sender, String[] args) {
		ChatTrait ct = Plugin.getChatCitizenFor(sender);
		if(ct==null){
			Plugin.sendCmdMessage(sender, "No ChatCitizen selected");
		} else {
			ChatterWrapper b = Plugin.getInstance().getBot(args[0]);
			if(b==null){
				Plugin.sendCmdMessage(sender, "\""+args[0]+"\" is not installed on this server.");
			} else {
				ct.setBot(b);
				Plugin.sendCmdMessage(sender, ct.getNPC().getFullName()+" is now using bot \""+args[0]+"\".");
			}
		}
	}
}
