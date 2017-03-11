# ChatCitizen
This is a Citizens 2 Trait for chatbot NPCs, using a (large) subset of AIML 1.0. The underlying code used is Chatterbean, which is extremely elderly but works fairly well. The AIML subset used is documented at the [Chatterbean website](http://www.geocities.ws/phelio/chatterbean/). I have modified the code, adding a few tags (nothing important) and removing the Applet and GUI aspects. Chatterbean has a GPL license, so ChatCitizen is also GPL (unfortunately).

## Caveats
* This is very, very, very preliminary work. Use at your own risk.
* You're going to need to know some AIML.


## Installing
You will (obviously) need Citizens 2 installed. Install the ChatCitizen JAR as usual, but you will need to take some extra steps. 
* Run the server once with the JAR installed to make the plugin directory.
* Move the **default** directory into the **plugins/ChatCitizen** directory - this is the default robot.
* Copy the **substitutions.xml** directory into the **plugins/ChatCitizen** directory.
* You should now be able to run the server and attach the trait. Note that the server will now take a little more time to boot - AIML files can be large and take a long time to load!

## Attaching the trait
Do this as usual with **/trait chatcitizen** with an NPC selected. They will be assigned the default bot, but each will have a different context so shouldn't get confused.

## Talking
Talk to the bot by standing near it and saying things in chat. There's currently no standard way of talking to a non-player in private messaging, and this seemed to be the best way to do it (any thoughts, anyone?) The bot will assume you are talking to it if you are within 10 meters horizontally (XZ plane) and 2 vertically (Y plane).

## Commands
Only one at the moment:
*	**ccz setbot [name]** will set the currently selected NPC to use the named bot.

## Adding bots
I'll have to assume you know some AIML, perhaps by playing with [pandorabots.com](http://pandorabots.com). You should end up with a set of AIML files. Note that the PandoraBots substitutions, maps and sets are supported, but you can edit **substitutions.xml** if required or use parts of the default bot.

Create a new directory inside **plugins/ChatCitizen** and put your AIML files in there, along with
* a **splitters.xml** file describing punctuation,
* a **substitutions.xml** for common substitutions,
* a **context.xml** file for some common properties (be aware that some may be changed by the plugin - "name" will be set to the NPC name, for example),
* a **list.txt** file (NOT XML) which lists all the AIML files in the order in which they should be loaded and parsed,
* a **config.xml** file which lists all the above.

Feel free to just copy and modify all the files from **default**, of course. 
* Add the new directory to **config.yml** in the plugin directory. This tells the server which bot name is associated with which data directory.
* Once this has been done, restart the server. The plugin will show the AIML files being loaded, and you can refer to this if there are XML parsing problems.
* Then assign your bot to an NPC (after giving it the trait), and test it.

You will probably run into problems. ChatterBean isn't ideal, but it does work fairly well for simpler bots.

## Future work
* Having the NPC say random things from time to time (this should be easy, so I'll probably do it first).
* It would be nice if ChatterBean used a more recent AIML spec (and I think there are a few bugs in there)
* Extensions to the tags to allow an NPC to be given orders or suggestions
* Some nice bots (you can help with this!)

