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

	private HashMap<String,Set<String>> sets = new HashMap<String,Set<String>>();
	
	// in order that we can get defaults set in "default.yml" we need access to the default data.
	// This is set on loading.
	SubBotData deflt;

	// just one map for now, see how it goes.
	private HashMap<String,HashMap<String,String>> maps = new HashMap<String,HashMap<String,String>>();

	public SubBotData(String path){
		File file = new File(path);
		if(file.exists()){
			FileConfiguration f = YamlConfiguration.loadConfiguration(file);

			// there's an object called "maps" that contains string->string mappings.
			if(f.isConfigurationSection("maps")){
				ConfigurationSection sec = f.getConfigurationSection("maps");
				for(String mapName : sec.getKeys(false)){
					ConfigurationSection map = sec.getConfigurationSection(mapName);
					HashMap<String,String> m = new HashMap<String,String>();
					maps.put(mapName, m);
					Plugin.log("MAP ADDED: "+mapName);
					for(Map.Entry<String, Object> e: map.getValues(false).entrySet()){
						String k = e.getKey().trim();
						String v = (e.getValue().toString()).trim();
						Plugin.log("MAP PUT "+k+" : "+v);
						m.put(k,v);
					}
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
		Set<String> s;
		
		if(sets.containsKey(setName)){
			Plugin.log("Set "+setName+" OK");
			s = sets.get(setName);
		} else if(deflt!=null && deflt.sets.containsKey(setName)){
			Plugin.log("Set "+setName+" in default");
			s = deflt.sets.get(setName);
		} else
			return "";
			
		int sz = s.size();
		int idx = rnd.nextInt(sz);
		int i=0;
		for(String ss: s){
			if(i==idx)return ss.trim();
			i++;
		}
		return "";
	}

	public String getFromMap(String mapName,String key) {
		HashMap<String,String> m;
		if(maps.containsKey(mapName)){
			 m = maps.get(mapName);
		} else if(deflt!=null && deflt.maps.containsKey(mapName)) {
			m = deflt.maps.get(mapName);
		} else
			return "unknownmap";

		if(m.containsKey(key))
			return m.get(key);
		return "unknown";
	}
	
	public boolean hasMap(String mapName){
		return maps.containsKey(mapName) || (deflt!=null && deflt.maps.containsKey(mapName));
	}

	public String randKeyFromMap(String mapName) {
		HashMap<String,String> m;
		if(maps.containsKey(mapName)){
			 m = maps.get(mapName);
		} else if(deflt!=null && deflt.maps.containsKey(mapName)) {
			m = deflt.maps.get(mapName);
		} else
			return "unknownmap";
		
		Set<String> s = m.keySet();
		int sz = s.size();
		int idx = rnd.nextInt(sz);
		int i=0;
		for(String ss: s){
			if(i==idx)return ss.trim();
			i++;
		}
		return "";
	}
}
