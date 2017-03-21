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
Do this as usual with **/trait chatcitizen** with an NPC selected. They will be assigned the default bot, but each will have a different context so shouldn't get confused.

## Talking
Talk to the bot by standing near it and saying things in chat. There's
currently no standard way of talking to a non-player in private messaging, and
this seemed to be the best way to do it (any thoughts, anyone?) The bot will
assume you are talking to it if you are within 5 meters horizontally (XZ
plane) and 2 vertically (Y plane).



## Commands
* **ccz setbot [name]** will set the currently selected NPC to use the named bot (these are subdirectories of the **bots** directory).
* **ccz info** get info on the NPC's ChatCitizen parameters
* **ccz set [paramname] [value]** set a parameter

## Parameters
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
I'll have to assume you know some AIML, perhaps by playing with [pandorabots.com](http://pandorabots.com). 
You should end up with a set of AIML files. You should start by copying one of the smaller bots (perhaps Jokebot) and then
copy your files over its AIML files.

Feel free to just copy and modify all the files from **default**, of course. 
* Add the new directory to **config.yml** in the plugin directory. This tells the server which bot name is associated with which data directory.
* Once this has been done, restart the server. The plugin will show the AIML files being loaded, and you can refer to this if there are XML parsing problems.
* Then assign your bot to an NPC (after giving it the trait), and test it.
# ChatCitizen
This is a Citizens 2 Trait for chatbot NPCs, using a subset of AIML
2.0 (sans <sraix>, because I'm trying to keep the dependencies down,
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
Do this as usual with **/trait chatcitizen** with an NPC selected. They will be assigned the default bot, but each will have a different context so shouldn't get confused.

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

## Parameters
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
I'll have to assume you know some AIML, perhaps by playing with [pandorabots.com](http://pandorabots.com). 
You should end up with a set of AIML files. You should start by copying one of the smaller bots (perhaps Jokebot) and then
copy your files over its AIML files.

Feel free to just copy and modify all the files from **default**, of course. 
* Add the new directory to **config.yml** in the plugin directory. This tells the server which bot name is associated with which data directory.
* Once this has been done, restart the server. The plugin will show the AIML files being loaded, and you can refer to this if there are XML parsing problems.
* Then assign your bot to an NPC (after giving it the trait), and test it.

### Spontaneous speech patterns
Adding categories with certain special patterns will make the robot
produce spontaneous speech. If the category is not present, the speech
will not trigger.
* **RANDSAY** is fired off at random
* **GREETSAY** is fired off when a player moves close and hasn't been greeted for a while.
* **ENTITYHITME** triggers when the bot is hit by a non-player
* **PLAYERHITME** triggers when the bot is hit by player
* **HITSOMETHING** triggers when the bot hits something (fun with Sentinel!) 
There are properties associated with some of these: see above.

## AIML extensions
These have been added using the ```AIMLProcessorExtension``` class inside Program AB. These will hopefully increase over time.
### General
These are those tags which do not require any extra plugins.
* ```<mctime type=".."/>``` will give the in-game time of day
    * **type="digital"** (default) will give HH:MM
    * **type="raw"** will give raw ticks
    * **type="approx"** will give a string: dawn, dusk, noon, midnight, day or night.

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

## Dependencies
You will need add the following JARs if you want to build ChatCitizen yourself:
* Citizens 2
* NPC Destinations
* MondoCommand

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

