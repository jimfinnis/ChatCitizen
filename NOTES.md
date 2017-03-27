# Stuff we can add custom tags for

## World information
* Nearby entities
* Weather
* Environment (overworld, nether, end)
* humidity/temperature (!)
* hasstorm (i.e. is it raining)
* thundering
* biome
* light level
* indoors?
* underground?

## Special actions
Is there any way I can give an NPC stuff, and have them give 
me stuff in return? NPCs can monitor clicks, we can then do
getInventory().getItemInHand() on the player. Perhaps the
NPC can have a CLICKEDWITHITEM ... special pattern,
say CLICKEDWITHITEM EMERALD. We can then arrange to be in
a topic/that to detect this.

This would give me shops!
