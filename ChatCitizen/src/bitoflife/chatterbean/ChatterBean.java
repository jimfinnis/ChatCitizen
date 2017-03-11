/*
Copyleft (C) 2005-2006 H�lio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
 */

/*
<object classid="clsid:CAFEEFAC-0015-0000-0002-ABCDEFFEDCBA"
        codebase="http://java.sun.com/update/1.5.0/jinstall-1_5_0_02-windows-i586.cab#Version=5,0,20,9"
        width="350" height="200">
  <param name="code" value="bitoflife.chatterbean.util.Applet">
  <param name="type" value="application/x-java-applet;jpi-version=1.5.0_02">
  <param name="scriptable" value="false">
  <comment>
    <embed type="application/x-java-applet;jpi-version=1.5.0_02"
           code="bitoflife.chatterbean.util.Applet"
           width="350" height="200"
           scriptable="false"
           pluginspage="http://java.sun.com/products/plugin/index.html#download">
      <noembed></noembed>
    </embed>
  </comment>
</object>
 */

package bitoflife.chatterbean;

import bitoflife.chatterbean.parser.ChatterBeanParser;

public class ChatterBean
{
	/*
  Attribute Section
	 */

	/** The underlying AliceBot used to produce responses to user queries. */
	private AliceBot aliceBot;

	/*
  Constructor Section
	 */

	/**
  Default constructor.
	 */
	public ChatterBean()
	{
	}

	/**
  Creates a new ChatterBean configured with a set of properties.

  @param path Path of the properties file.
	 */
	public ChatterBean(String path)
	{
		this();
		configure(path);
	}

	
	/*
  Event Section
	 */

	/**
  Configures this object with a set of properties.

  @param path Path of the properties file.
	 */
	public void configure(String path)
	{
		try
		{
			if (getAliceBot() == null)
				setAliceBot(new AliceBot());
			ChatterBeanParser parser = new ChatterBeanParser();
			parser.parse(this, path);
		}
		catch (Exception e)
		{
			throw new ChatterBeanException(e);
		}
	}

	public String respond(String request)
	{
		String response = "";
		if(request != null && !"".equals(request.trim())) try
		{
			response = aliceBot.respond(request);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		return response;
	}

	/*
  Property Section
	 */

	/**
  Gets the AliceBot encapsulated by this bot.

  @return An AliceBot.
	 */
	public AliceBot getAliceBot()
	{
		return aliceBot;
	}

	/**
  Sets the AliceBot encapsulated by this bot.

  @param aliceBot An AliceBot.
	 */
	public void setAliceBot(AliceBot aliceBot)
	{
		this.aliceBot = aliceBot;
	}

}
