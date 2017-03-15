package org.pale.chatcitizen.Command;

import org.bukkit.command.CommandSender;
import org.pale.chatcitizen.CommandAction;
import org.pale.chatcitizen.Plugin;

public class Help extends CommandAction {

	@Override
	public void run(CommandSender sender, String[] args) {
		Plugin.getInstance().showCommandHelp(sender);
	}

}
