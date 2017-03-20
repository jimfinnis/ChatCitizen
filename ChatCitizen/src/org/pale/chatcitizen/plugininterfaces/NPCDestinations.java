package org.pale.chatcitizen.plugininterfaces;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_destinations.citizens.NPCDestinationsTrait;
import net.livecar.nuttyworks.npc_destinations.citizens.NPCDestinationsTrait.en_RequestedAction;

import org.pale.chatcitizen.ExternalPluginInterface;
import org.pale.chatcitizen.Plugin;

public class NPCDestinations extends ExternalPluginInterface{
	
	/**
	 * Wrapper object for NPCDestinations data. Most stuff is done through this class.
	 * @author white
	 *
	 */
	public class NPCDestData {
		NPCDestinationsTrait trait;
		public NPC npc;
		
		private int getLoc(String name){
			int locNum=-1;
			if(name.matches("\\d+")){
				locNum = Integer.parseInt(name);
			} else {
				for(int i=0;i<trait.NPCLocations.size();i++){
					if(trait.NPCLocations.get(i).Alias_Name.equalsIgnoreCase(name)){
						locNum = i;
					}
				}
			}
			return locNum;
		}
		
		private String go(int locnum,long forTime){
			if(locnum < trait.NPCLocations.size()){
				npc.getNavigator().cancelNavigation();
				trait.clearPendingDestinations();
				trait.lastResult="forced location (ChatCitizen)";
				trait.setLocation = trait.NPCLocations.get(locnum);
				trait.currentLocation = trait.setLocation;
				trait.locationLockUntil = new java.util.Date(System.currentTimeMillis()+forTime);
				trait.lastPositionChange = new java.util.Date();
				trait.setRequestedAction(en_RequestedAction.SET_LOCATION);
				return "YES";
			} else {
				Plugin.warn("Location "+locnum+" out of range ["+npc.getFullName()+"]");
				return "NO";
			}
		}
		
		/**
		 * Go to a named or numbered location.
		 * @param name
		 * @param forTime how long to hold the location for.
		 */
		public String go(String name,long forTime){
			int locNum = getLoc(name);
			if(locNum>=0){
				return go(locNum,forTime);
			} else {
				Plugin.warn("Cannot find location to go to: "+name+" ["+npc.getFullName()+"]");
				return "NO";
			}
		}
	}

	public NPCDestinations() {
		super("NPC_Destinations", "1.43");
	}
	
	/**
	 * Create a new wrapper object for NPCDestinations. Called by each bot.
	 * @param n
	 * @return null if there's no data!
	 */
	public NPCDestData makeData(NPC n){
		if(isValid()){
			NPCDestinationsTrait t = n.getTrait(NPCDestinationsTrait.class);
			NPCDestData d = new NPCDestData();
			d.trait = t;
			d.npc = n;
			return d;
		} else
			return null;
	}

}
