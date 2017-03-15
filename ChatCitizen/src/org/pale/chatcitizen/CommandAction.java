package org.pale.chatcitizen;

import org.bukkit.command.CommandSender;


public abstract class CommandAction {
	private String usage;
	private int argc; // arg count, or -1 if varargs
	
	// override this...
	public abstract void run(CommandSender sender,String args[]);
	

	public void execute(CommandSender sender, String[] args) {
		if(argc>=0 && args.length!=argc){
			Plugin.sendCmdMessage(sender, "Usage : "+usage);
		} else
			run(sender,args);
	}

	// called after instantiation to set the argc and usage.
	public void init(int argc, String usage) {
		this.argc=argc;
		this.usage=usage;
	}


	public String getUsage() {
		return usage;
	}
}
