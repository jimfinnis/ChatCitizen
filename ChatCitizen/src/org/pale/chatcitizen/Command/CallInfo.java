package org.pale.chatcitizen.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.pale.chatcitizen.ChatTrait;
import org.pale.chatcitizen.Plugin;

public class CallInfo {
	private final CommandSender sender;
	private final Player p;
	private final String[] args;
	private String cmd;
	private ChatTrait cz;
	
	public CallInfo (String cmd,CommandSender p, String[] args){
		this.sender = p;
		this.p = (p instanceof Player)?(Player)p:null;
		this.args = args;
		this.cmd=cmd;
		this.cz = Plugin.getChatCitizenFor(p);
	}
	
	public String getCmd(){
		return cmd;
	}
	
	public Player getPlayer(){
		return p;
	}
	
	public String[] getArgs(){
		return args;
	}
	
	public void msg(String s){
		Plugin.sendCmdMessage(sender,s);
	}

	public ChatTrait getCitizen() {
		return cz;
	}

}
