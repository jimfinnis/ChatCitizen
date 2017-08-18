package org.pale.chatcitizen;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;

public class MaterialNameParser {
	
	/**
	 * given a material name in "friendly" form, as it appears in conversation
	 * or in a material list, get its Material.
	 * @param itemName
	 * @return
	 */
	public static Material get(String itemName) {
		itemName = itemName.toUpperCase();
		itemName=itemName.replaceAll("INGOTS", "INGOT")
				.replaceAll("BLOCKS", "BLOCK")
				.replaceAll(" UNDERSCORE ", "_")
				.replaceAll("\\s+", "_")
				.trim();
		if(itemName.equals("GOLD"))itemName="GOLD_INGOT";
		if(itemName.equals("IRON"))itemName="IRON_INGOT";
		
		Plugin.log("Post processing mat name: "+itemName);

		try {
			return Material.valueOf(itemName);
		} catch(IllegalArgumentException e){
			return null;
		}
	}
	
	
	// not using this right now but might be handy.
	private static Set<String> makeSet(String... strings) {
	    HashSet<String> set = new HashSet<String>();

	    for (String s : strings) {
	        set.add(s);
	    }
	    return set;
	}

}
