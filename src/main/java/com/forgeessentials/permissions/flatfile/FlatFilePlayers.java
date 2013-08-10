package com.forgeessentials.permissions.flatfile;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.common.Configuration;

import com.forgeessentials.api.APIRegistry;

import cpw.mods.fml.common.FMLCommonHandler;

public class FlatFilePlayers
{
	File	file;

	public FlatFilePlayers(File file)
	{
		this.file = new File(file, "players.txt");
	}

	public ArrayList<String> load()
	{
		ArrayList<String> players = new ArrayList<String>();

		Configuration config = new Configuration(file);

		PlayerInfo info;
		for (String cat : config.getCategoryNames())
		{
			if (cat.contains("."))
			{
				continue;
			}
			else if (cat.equalsIgnoreCase(APIRegistry.perms.getEntryPlayer()))
			{
				APIRegistry.perms.setEPPrefix(config.get(cat, "prefix", " ").getString());
				APIRegistry.perms.setEPSuffix(config.get(cat, "suffix", " ").getString());
				continue;
			}

			info = PlayerInfo.getPlayerInfo(cat);

			if (info != null)
			{
				info.prefix = config.get(cat, "prefix", " ").getString();
				info.suffix = config.get(cat, "suffix", " ").getString();
			}

			players.add(cat);
			discardInfo(info, new String[] {});
		}

		return players;
	}

	public void save(ArrayList<String> players)
	{
		// clear it.
		if (file.exists())
		{
			file.delete();
		}

		String[] allPlayers = new String[0];
		MinecraftServer server = FMLCommonHandler.instance().getSidedDelegate().getServer();
		if (server != null)
		{
			ServerConfigurationManager manager = server.getConfigurationManager();
			if (manager != null)
			{
				allPlayers = manager.getAllUsernames();
			}
		}

		Configuration config = new Configuration(file);

		PlayerInfo info;
		for (String name : players)
		{
			if (name.equalsIgnoreCase(APIRegistry.perms.getEntryPlayer()))
			{
				config.get(name, "prefix", APIRegistry.perms.getEPPrefix());
				config.get(name, "suffix", APIRegistry.perms.getEPSuffix());
				continue;
			}

			info = PlayerInfo.getPlayerInfo(name);
			config.get(name, "prefix", info.prefix == null ? "" : info.prefix);
			config.get(name, "suffix", info.suffix == null ? "" : info.suffix);
			discardInfo(info, allPlayers);
		}

		config.save();
	}

	private void discardInfo(PlayerInfo info, String[] allPlayers)
	{
		for (String name : allPlayers)
			if (info.username.equalsIgnoreCase(name))
				return;

		// not logged in?? kill it.
		PlayerInfo.discardInfo(info.username);
	}

}
