package org.pale.chatcitizen.Command;

import java.lang.reflect.Field;

import org.bukkit.command.CommandSender;
import org.pale.chatcitizen.ChatTrait;
import org.pale.chatcitizen.CommandAction;
import org.pale.chatcitizen.Plugin;

/**
 * This command is used to set some parameters in the Trait, not the bot itself. These parameters
 * must all currently be doubles, because we cheat horribly using reflection.
 * @author white
 *
 */
public class Set extends CommandAction {

	private static String[] paramNames={"saydist","sayint","sayprob","greetdist","greetint","greetprob","auddist"};

	// same ordering as paramNames - these are the actual field names!
	private static String[] paramFields={"sayDist","sayInterval","sayProbability","greetDist","greetInterval",
		"greetProbability","audibleDistance"
	};

	@Override
	public void run(CommandSender sender, String[] args) {
		if(args.length < 2){
			StringBuilder b = new StringBuilder();
			for(String s: paramNames){
				b.append(s);b.append(" ");
			}
			Plugin.sendCmdMessage(sender, b.toString());
		} else {
			ChatTrait ct = Plugin.getChatCitizenFor(sender);
			if(ct==null)
				Plugin.sendCmdMessage(sender, "No ChatCitizen selected");
			else {
				for(int i=0;i<paramNames.length;i++){
					if(args[0].equals(paramNames[i])){
						try {
							Field f = ChatTrait.class.getDeclaredField(paramFields[i]);
							double val = Double.parseDouble(args[1]);
							if(paramNames[i].contains("prob")){
								val *= 100; // convert "prob"abilities from percentages.
							}
							f.setDouble(ct,val);
						} catch (NumberFormatException e) {
							Plugin.sendCmdMessage(sender, "that is not a number");							
						} catch (NoSuchFieldException | SecurityException e) {
							Plugin.sendCmdMessage(sender, "no such field - this shouldn't happen:"+paramFields[i]);
						} catch (IllegalArgumentException e) {
							Plugin.sendCmdMessage(sender, "probably a type mismatch - this shouldn't happen:"+paramFields[i]);
						} catch (IllegalAccessException e) {
							Plugin.sendCmdMessage(sender, "illegal access to field - this shouldn't happen:"+paramFields[i]);
						}
						return; // found and handled, so exit.
					}
				}
				Plugin.sendCmdMessage(sender, "No parameter of that name found");
				return;
			}
		}
	}
}
