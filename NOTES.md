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
NPC can have a RIGHTCLICKED special pattern,
say RIGHTCLICKED EMERALD. Except that's a bit iffy, because 
it makes it hard to detect a special pattern. What we can do
is just have RIGHTCLICKED and put the item into a predicate.

This would give me shops, sort of.

### 
