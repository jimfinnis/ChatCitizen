package org.pale.chatcitizen;

public class ExternalPluginInterface {
	org.bukkit.plugin.Plugin plugin;
	private String name;
	
	public boolean isValid(){
		return plugin!=null;
	}
	
	/**
	 * Find whether the version is high enough. Versions are dotted numerical strings,
	 * e.g. "2.0.4" > "1.5" > "1.4" > "0.0".
	 * @param required the required version
	 * @param level the usage level (there could be several, but the minimum should be called "usage").
	 * @return
	 */
	boolean isVersionOK(String required,String level){
		String vn = plugin.getDescription().getVersion();
		String[] version = vn.split("\\.");
		String[] mvarray = required.split("\\.");
		for(int i=0;i<mvarray.length;i++){
			if(i>=version.length)break;
			int got = Integer.parseInt(version[i]);
			int req = Integer.parseInt(mvarray[i]);
			if(got>req)break;
			if(got<req){
				Plugin.warn("Plugin "+name+" has version "+vn+", but "+required+" is required for "+level);
				return false;
			}
		}
		return true;
	}
	
	public ExternalPluginInterface(String name,String minversion){
		this.name = name;
		plugin = Plugin.getInstance().getServer().getPluginManager().getPlugin(name);
		if(plugin==null){
			Plugin.warn("Cannot load plugin "+name+", extensions for it will not work.");
		} else {
			String vn = plugin.getDescription().getVersion();
			// check we meet the minimum requirements.
			if(isVersionOK(minversion,"usage"))
				Plugin.log("Plugin "+name+" found (ver "+vn+", >"+minversion+" supported. OK!)");
			else {
				plugin = null;
			}
		}
	}
}
