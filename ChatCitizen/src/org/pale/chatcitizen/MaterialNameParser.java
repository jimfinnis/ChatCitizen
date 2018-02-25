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
		itemName=itemName
				.replaceAll(" UNDERSCORE ", "_")
				.replaceAll("\\s+", "_")
				.trim();
		if(itemName.equals("GOLD"))itemName="GOLD_INGOT";
		if(itemName.equals("IRON"))itemName="IRON_INGOT";
		
		Plugin.log("Post processing mat name: "+itemName);

		try {
			return Material.valueOf(itemName);
		} catch(IllegalArgumentException e){
			// if that didn't work, try eliminating plurals.
			try {
				itemName = itemName.replaceAll("S$", "");
				Plugin.log("Didn't work, trying "+itemName);
				return Material.valueOf(itemName);
			} catch(IllegalArgumentException e2) {
				return null;
			}
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
