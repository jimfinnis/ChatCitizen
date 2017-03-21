package org.pale.chatcitizen.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.pale.chatcitizen.Plugin;

public class Registry {
	class Entry {
		String name;
		String permission;
		Method m;
		Object obj;
		private Cmd cmd;
		
		
		Entry(String name,String perm,Cmd cmd,Object o,Method m){
			this.permission=perm;
			this.cmd=cmd;
			this.m=m;
			this.obj=o;
			this.name = name;
		}
		
		private boolean checkPermission(Player p){
			return p==null || permission==null || p.hasPermission(permission);
		}
		
		public void invoke(CallInfo c){
			try {
				Player p = c.getPlayer();
				if(p==null && cmd.player()){
					c.msg("That command requires a player");
					return;
				}
				if(c.getCitizen()==null && cmd.cz()){
					c.msg("That command requires a selected ChatCitizen");
					return;
				}
				if(!checkPermission(p)){
					c.msg("You do not have the permission "+permission);
					return;
				}
				if(cmd.argc()>=0 && c.getArgs().length!=cmd.argc()){
					showHelp(c,c.getCmd());
					return;
				}
				m.invoke(obj, c);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Map<String,Entry> registry = new HashMap<String,Entry>();
	
	
	public void register(Object handler){
		for(Method m : sortedMethods(handler)){
			Cmd cmd = m.getAnnotation(Cmd.class);
			if(cmd!=null){
				Class<?> params[] = m.getParameterTypes();
				if(params.length != 1 || !params[0].equals(CallInfo.class)){
					Plugin.warn("Error in @Sub on method "+m.getName()+": parameter must be one CallInfo");
				} else {					
					String name = cmd.name();
					if(name.equals(""))name = m.getName();
					String perm = cmd.permission();
					if(perm.equals(""))perm = null;
					registry.put(name, new Entry(name,perm,cmd,handler,m));
				}
			}
		}
	}
	
	/**
	 * Handle a command string, assuming the command name is correct for the plugin.
	 * @param s
	 */
	public void handleCommand(CommandSender sender,String[] args){
		if(args.length == 0){
			String cmds="";
			for(String cc: registry.keySet())cmds+=cc+" ";
			Plugin.sendCmdMessage(sender,cmds);
		} else {
			String cmdName = args[0];
			if(!registry.containsKey(cmdName)){
				Plugin.sendCmdMessage(sender,"unknown chatcitizen command: "+cmdName);
			} else {
				Entry e = registry.get(cmdName);
				args = Arrays.copyOfRange(args, 1, args.length);
				e.invoke(new CallInfo(cmdName,sender,args));
			}
		}
	}
	

    /**
     * Reflect the methods on this object, sorted by name.
     * @param handler
     * @return an ArrayList of methods.
     */
    private ArrayList<Method> sortedMethods(Object handler) {
        TreeMap<String, Method> methodMap = new TreeMap<String, Method>();
        for (Method method : handler.getClass().getDeclaredMethods()) {
            methodMap.put(method.getName(), method);
        }
        return new ArrayList<Method>(methodMap.values());
    }

	public void listCommands(CallInfo c) {
		for(String cc:registry.keySet())
			showHelp(c,cc);
	}

	public void showHelp(CallInfo c, String cmdName) {
		if(registry.containsKey(cmdName)){
			Entry e = registry.get(cmdName);
			c.msg(ChatColor.AQUA+"ccz "+e.name+" "+e.cmd.usage()+":"+ChatColor.GREEN+" "+e.cmd.desc());
		} else {
			c.msg("No such command. try \"ccz help\" on its own.");
		}
	}
}
