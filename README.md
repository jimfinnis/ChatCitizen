# ChatCitizen
This is a Citizens 2 Trait for chatbot NPCs, using a subset of AIML
2.0 (sans ```<sraix>```, because I'm trying to keep the dependencies down,
and the Japanese stuff for the same reason). The underlying code used is Richard Wallace's
[Program AB](http://alicebot.blogspot.co.uk/2013/01/program-ab-aiml-20-reference.html).
Program AB has a GPL license, so ChatCitizen is also GPL.

## Caveats
* This is very, very, very preliminary work. Use at your own risk.
* You're going to need to know some AIML.
## Installing
You will (obviously) need Citizens 2 installed. Install the ChatCitizen JAR as usual, but you will need to take some extra steps. 
* Run the server once with the JAR installed to make the plugin directory.
* Move the **bots** directory into the **plugins/ChatCitizen** directory - these are the robot definitions.
* You should now be able to run the server and attach the trait. Note that the server will now take a little more time to boot - AIML files can be large and take a long time to load!

## Attaching the trait
Do this as usual with **/trait chatcitizen** with an NPC selected. All NPCs will initially be assigned the default bot, but each will have a different context so shouldn't get confused. It's important to remember that multiple NPCs can share the same bot, making them respond the same way, but that each NPC has a different context for its bot. For example, you could have two bots ```soldier``` and ```idiot```. Your guards could then all use the ```soldier``` bot, and your idiots could use the ```idiot``` bot. 

You can change the bot used by an NPC by selecting the NPC and using ```ccz setbot [botname]```. To find out which NPCs are using which bot, use ```ccz info``` to get info for a selected NPC, or ```ccz bots``` which will list all the bots and their NPCs.

## Talking
Talk to the bot by standing near it and saying things in chat. There's
currently no standard way of talking to a non-player in private messaging, and
this seemed to be the best way to do it (any thoughts, anyone?) The bot will
assume you are talking to it if you are within 5 meters horizontally (XZ
plane) and 2 vertically (Y plane).

## Commands
* **ccz help [commandname]** get help on a command - with no arguments, list the commands.
* **ccz setbot [name]** will set the currently selected NPC to use the named bot (these are subdirectories of the **bots** directory). (Required permission ```chatcitizen.set```.)
* **ccz info** get info on the NPC's ChatCitizen parameters.
* **ccz set [paramname] [value]** set a parameter (requires ```chatcitizen.set```).
* **ccz reloadall** reload all AIML and data files (requires ```chatcitizen.reloadall```).
* **ccz reload [botname]** reload the AIML and data files for a bot (requires ```chatcitizen.reload```). Note that the name is that of the bot as given in config.yml, not the name of an NPC using that bot: if ```ccz bots``` says that NPC Steve, Graham and Betty are all using the "soldier" bot, doing ```ccz reload soldier``` will work and reset them all, but ```ccz reload Betty``` will give an error.
* **ccz bots** list all bots and the NPCs which use them.
* **ccz subbot [subbotname]** tells the currently selected NPC to use the given sub-bot (each bot can have a number of sub-bots, each of which has its own sets and a map, to allow variations within bots).

## Parameters used by ccz set
Many of these require spontaneous speech to be enabled by adding RANDSAY and GREETSAY to the bot's categories. The default
bot doesn't have these.
* **saydist** how far the bot will look for someone to randomly
talk to - if there's no-one nearby, it stays quiet.
* **sayint** the time it will wait between random speech events.
* **sayprob** the chance (%) that it will try to speak, once every 5
seconds after the interval has elapsed.
* **greetdist** how close a player has to be before the bot will
greet it.
* **greetinterval** how long between greeting each player (i.e. how
long the player has to go away for).
* **greetprob** the probability the bot will greet an appearing
player, or just ignore them.
* **auddist** the distance the bot's speech can be heard over.


## Adding bots
Bots each have their own directory under ```plugins/ChatCitizens/bots``` in your plugins directory, so copy the ones you want.
I'll have to assume you know some AIML, perhaps by playing with [pandorabots.com](http://pandorabots.com). 
You should end up with a set of AIML files. You should start by copying one of the smaller bots (perhaps Jokebot) and then
copy your files over its AIML files.

Feel free to just copy and modify all the files from **default**, of course. 
* Add the new directory to **config.yml** in the plugin directory. This tells the server which bot name is associated with which data directory.
* Once this has been done, restart the server. The plugin will show the AIML files being loaded, and you can refer to this if there are XML parsing problems.
* Then assign your bot to an NPC (after giving it the trait), and test it.

### Using pandorabots
You can use a Pandorabots chatbot directory (provided it doesn't do any really clever stuff) by unzipping it into
its own directory under ```bots``` and moving all the AIML files into a subdirectory called ```aiml```. See the
existing files for examples. Note that "clever stuff" includes sets and
maps (I think).

### My stuff
Many of my bots use an [Angort](https://github.com/jimfinnis/angort) script to generate the AIML files, run from a script called ```build```. Don't
worry too much about this unless you're interested in my weird little language; the ```auto.aiml``` files
are just AIML files (but with all the newlines missing).

### Spontaneous speech patterns
Adding categories with certain special patterns will make the robot
produce spontaneous speech. If the category is not present, the speech
will not trigger.
* **RANDSAY** is fired off at random
* **GREETSAY** is fired off when a player moves close and hasn't been greeted for a while.
* **ENTITYHITME** triggers when the bot is hit by a non-player
* **PLAYERHITME** triggers when the bot is hit by player
* **HITSOMETHING** triggers when the bot hits something (fun with Sentinel!) 
* **RIGHTCLICK** triggers when the bot is right-clicked. See **Right Clicking** below.
There are properties associated with some of these: see above.

## Right Clicking
When a player right-clicks on a bot, the RIGHTCLICK pattern is sent.
If there is something in the player's main hand, the name will be available as ```<get name="itemname"/>```. Otherwise this will produce "air".
Additionally, the item stack will be stashed away for use by other commands. A brief idea of how you might use it to not respond when you click
anything other than a weapon, and to say something when a weapon is used:
```
  <category><pattern>RCLICK *</pattern><template/></category>

  <category><pattern>RCLICKWEAPON</pattern>
    <template>Ah, a very nice and deadly item.</template>
  </category>

  <category><pattern>RCLICK * SWORD</pattern>
    <template><srai>RCLICKWEAPON</srai></template>
  </category>
  <category><pattern>RCLICK * AXE</pattern>
    <template><srai>RCLICKWEAPON</srai></template>
  </category>

  <category>
    <pattern>RIGHTCLICK</pattern>
    <template>
      <srai>RCLICK <getpl name="itemheld"/> </srai>
    </template>
  </category>
```
It might seem better to use a ```RIGHTCLICK <ITEM NAME>``` pattern, but that
would be pretty messy to do internally. Note the use of ```getpl``` - this is a private variable between the chatbot and the player.

## AIML extensions
These have been added using the ```AIMLProcessorExtension``` class inside Program AB. These will hopefully increase over time.
### Extensions to the core AIML language
These have been added to make life easier for bot writers.
* ```<li min="..." max="...">...</li>``` tags in conditions are permitted, so you can check over a range of values. The condition will be false if the value or either extreme is not a number. The interval is closed at the lower end (i.e. it tests for min<=x<max). Max or min may be omitted.
* ```<clean opts="..."></clean>``` will remove spurious whitespace from the text it wraps, replacing all whitespace runs with a single space. Any "." immediately followed by a word character will be also replaced with ". ". If opts contains "s", the resulting text will have the first character capitalised.
### General
These are those tags which do not require any extra plugins.
* ```<mctime type=".."/>``` will give the in-game time of day
    * **type="digital"** (default) will give HH:MM
    * **type="raw"** will give raw ticks
    * **type="approx"** will give a string: dawn, dusk, noon, midnight, day or night.
    * **type="todstring"** will also return a string: morning, afternoon, evening or night.
* ```<getpl name="..."/>``` and ```<setpl name="...">...</setpl>``` provide player predicates: predicates which are private to a particular player in conversation with a particular NPC. The default value (as with all predicates) is "unknown". This allows each bot to build up a set of predicates for each player. They are not persistent.
* ```<insbset set=".." yes="yesstring" no="nostring">...</insbset>``` determines whether the text is a member of one of a bot's sub-bot sets (see sub-bots, below). The yes and no strings may be omitted, in which case the tag returns "yes" or "no".
* ```<randsbset set="..."/>``` generate a random value from the bot's given sub-bot set
* ```<getsb name="..."/>``` get a value from the bot's sub-bot map (see sub-bots, below)
### NPC Destinations
These will only work if a recent version (at least 1.43) of nuNPC Destinations is installed.
* ```<npcdest cmd="go" loc="location" time="staytime"/>``` will tell the bot to go to a given location, specified by name or number. The time is how long the NPC should linger in milliseconds, and is 1 day if not specified.  If this fails for any reason (trait not present, can't find location) the command will return NO. If it succeeds, it will return YES. A suitable usage might be:
 ```
<category><pattern>go home</pattern>
    <template><think>
        <set name="topic">npcgo</set>
        <set name="dest">home</set>
        </think>
        <srai>
            <npcdest cmd="go" loc="0"/>
        </srai>
    </template>
</category>

<topic name="npcgo">
    <category><pattern>YES</pattern>
    <template>OK, I'll go <get name="dest"/>.
    <set name="topic"/></template>
    </category>
    <category><pattern>NO</pattern>
    <template>I don't know where <get name="dest"/> is!
    <set name="topic"/></template>
    </category>
</topic>
```
Bear in mind I am far from an expert in AIML!

### Sentinel
* ```<sentinel cmd="..."/>``` will return information about a Sentinel, or "NO" if the NPC is not a sentinel. The commands are
    * **cmd="timeSinceAttack"** to give the time since the NPC last attacked something
    * **cmd="timeSinceSpawn"** to give the time since the sentinel was created or died and respawned
    * **cmd="guarding"** to describe the sentinel's guard: either a player name, or "something" (for a non-player entity) or "nothing" for normal behaviour.
    * **cmd="health"** to give the health as a percentage. **TODO: THIS SHOULD BE A GENERAL COMMAND!**


## Sub-bots
Because loadings bots is expensive, and you might want to have quite
a few bots sharing most of their data, we have sub-bots. Each bot type
can have a number of files in its ```subbots``` directory, called
```something.yml``` where "something" is the name of the sub-bot.
This contains:
* ```sets``` - a set of lists, where each list is a special set of strings (see the ```insbset``` and ```randsbset``` tags)
* ```map``` - a map of strings to strings.

Each NPC using a bot can be using a different sub-bot, so one "soldier"
might be very obscene while another chooses his swear words from a milder
set, by just defining different ```swearword``` sets in the sub-bots files.
Another usage might be setting variables to different values in the map.

## Dependencies
You will need to add the following JARs to your build path if you want to build ChatCitizen yourself:
* Citizens 2
* NPC Destinations
* Sentinel 1.0

## Future work
* Make sure that **sayprob** works as advertised. It doesn't right now.
* Line of sight for audibility?
* More extensions! **Any ideas?**
* Scripting language, probably through JSR-223 and Javascript (ugh).
This will need some way of getting at bot properties, including the
server properties alluded to above.
* Sharing of common AIML files across all bots - for example, Pandorabots tend to share a set of common reductions which will currently be loaded separately. 

And above all
* **Some nice bots (you can help with this!)**

# Build instructions
* Make sure you have:
    * The source code!
    * Citizens 2
    * NPC Destinations
    * Sentinel 1.0
* Install maven (or you could work out how to do it in Eclipse)
* Create a symbolic link called "plugins" from your server's plugins directory to the same directory as the pom.xml (or just copy the directory there if you're on a rubbish operating system)
* From the directory with the pom.xml, run "mvn compile package"
* Copy the jar which should now be the "target" directory to your server plugin directory
* Copy config.yml and the bots directory into the "plugins/ChatCitizen" directory.
