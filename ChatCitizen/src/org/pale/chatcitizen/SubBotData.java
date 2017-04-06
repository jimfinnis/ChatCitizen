package org.pale.chatcitizen;

import java.io.File;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Bots can have multiple incarnations which have slightly different data stored in sets and maps.
 * Each ChatterWrapper has a bunch of these objects to manage them.
 * @author white
 *
 */
public class SubBotData {
	
	static Random rnd = new Random();

	private HashMap<String,Set<String>> sets = new HashMap<String,Set<String>>();;

	// just one map for now, see how it goes.
	private HashMap<String,String> map = new HashMap<String,String>();

	public SubBotData(String path){
		File file = new File(path);
		if(file.exists()){
			FileConfiguration f = YamlConfiguration.loadConfiguration(file);

			// there's an object called "map" that contains a list of string->string mappings.
			if(f.isConfigurationSection("map")){
				ConfigurationSection sec = f.getConfigurationSection("map");
				for(Map.Entry<String, Object> e: sec.getValues(false).entrySet()){
					String k = e.getKey().trim();
					String v = (e.getValue().toString()).trim();
					Plugin.log("MAP PUT "+k+" : "+v);
					map.put(k,v);
				}
			}
			
			// and an object called "sets" that contains a list of string -> stringlist mappings
			if(f.isConfigurationSection("sets")){
				ConfigurationSection sec = f.getConfigurationSection("sets");
				for(String key: sec.getKeys(false)){
					if(sec.isList(key)){
						List<String> list = sec.getStringList(key);
						HashSet<String> set = new HashSet<String>(list);
						sets.put(key, set);
					}
				}
			}
		}
	}

	public boolean isInSet(String set,String v){
		if(sets.containsKey(set)){
			return sets.get(set).contains(v);
		}
		return false;
	}

	public String randFromSet(String setName) {
		Plugin.log("Set "+setName);
		if(sets.containsKey(setName)){
			Plugin.log("Set "+setName+" OK");
			Set<String> s = sets.get(setName);
			// this is grim.
			int sz = s.size();
			int idx = rnd.nextInt(sz);
			int i=0;
			for(String ss: s){
				if(i==idx)return ss.trim();
				i++;
			}
		}
		return "";
	}

	public String getFromMap(String name) {
		if(map.containsKey(name))
			return map.get(name);
		return "";
	}
}
