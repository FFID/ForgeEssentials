package com.forgeessentials.core.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ModListMaker {
	public static void makeModList()
	{
		try
		{
			Calendar cal = Calendar.getInstance();
			File modListFile = new File(ForgeEssentials.FEDIR, CoreModule.modlistLocation);
			if (modListFile.exists())
			{
				modListFile.delete();
			}
			FileWriter fstream = new FileWriter(modListFile);
			PrintWriter out = new PrintWriter(fstream);
			out.println("# --- ModList ---");
			out.println("# Generated: " + cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.YEAR) + " (Server time)");
			out.println("# Change the location of this file in ForgeEssentials/main.cfg");
			out.println();

			for (ModContainer mod : Loader.instance().getModList())
			{
				String url = "";
				if (!mod.getMetadata().url.isEmpty())
				{
					url = mod.getMetadata().url;
				}
				if (!mod.getMetadata().updateUrl.isEmpty())
				{
					url = mod.getMetadata().updateUrl;
				}
				out.println(mod.getName() + ";" + mod.getVersion() + ";" + url);
			}

			out.close();
		}
		catch (Exception e)
		{
			Logger lof = OutputHandler.felog;
			lof.logp(Level.SEVERE, "FEConfig", "Generating modlist", "Error writing the modlist file: " + CoreModule.modlistLocation, e);
		}
	}

}
