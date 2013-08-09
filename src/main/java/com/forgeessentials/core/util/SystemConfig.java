package com.forgeessentials.core.util;

import java.io.File;
import java.util.logging.Level;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.OutputHandler;

public class SystemConfig {
	public static final File mainconfig = new File(ForgeEssentials.FEDIR,
			"main.cfg");

	public final Configuration config;

	public static String largeComment_Cat_Groups, groupPrefixFormat,
			groupSuffixFormat, groupRankFormat;
	static {
		largeComment_Cat_Groups = "You may put enything here that you want displaed as part of the group prefixes, suffixes, or ranks.";
		largeComment_Cat_Groups += "\n {ladderName<:>Zone} will display the data for the highest priority group that the player is in that is part of the specified ladder and specified zone.";
		largeComment_Cat_Groups += "\n {...<:>...} will display the data of each group the player is in in order of priority";
		largeComment_Cat_Groups += "\n you may put contsraints with ladders or zones with {...<:>zoneName} or {ladderName<:>...}";
		largeComment_Cat_Groups += "\n you may also use the color and MCFormat codes above.";
	}

	// this is designed so it will work for any class.
	public SystemConfig() {
		OutputHandler.felog.finer("Loading configs");

		config = new Configuration(mainconfig, true);

		config.addCustomCategoryComment("Core",
				"Configure ForgeEssentials Core.");

		/*
		 * Property prop = config.get("Core", "mcstats", true); prop.comment =
		 * "If you don't want to send feedback to MCstats, set to false. Optionally, use the opt-out setting located in PluginMetrics.cfg in your minecraft folder."
		 * ; ForgeEssentials.mcstats = prop.getBoolean(true);
		 */

		Property prop = config.get("Core", "logLevel", "" + Level.FINE);
		prop.comment = "ForgeEssentials LogLevel. Valid values: OFF, FINE, FINER, FINEST, WARNING, SEVERE, ALL, OFF";
		OutputHandler.felog.setLevel(getLevel(prop.getString()));

		prop = config.get("Core", "removeDuplicateCommands", true);
		prop.comment = "Remove commands from the list if they already exist outside of FE.";
		CommandRemover.removeDuplicateCommands = prop.getBoolean(true);

		/*
		 * prop = config.get("Core.Misc", "tpWarmup", 5); prop.comment =
		 * "The amount of time you need to stand still to TP.";
		 * TeleportCenter.tpWarmup = prop.getInt(5);
		 * 
		 * prop = config.get("Core.Misc", "tpCooldown", 5); prop.comment =
		 * "The amount of time you need to wait to TP again.";
		 * TeleportCenter.tpCooldown = prop.getInt(5);
		 */

		config.addCustomCategoryComment("Core.groups", largeComment_Cat_Groups);

		groupPrefixFormat = config.get("Core.groups", "groupPrefix",
				"{...<:>_GLOBAL_}").getString();
		groupSuffixFormat = config.get("Core.groups", "groupSuffix",
				"{...<:>_GLOBAL_}").getString();
		groupRankFormat = config.get("Core.groups", "rank",
				"[{...<:>_GLOBAL_}]").getString();

		config.save();
	}

	private Level getLevel(String val) {
		if (val.equalsIgnoreCase("INFO"))
			return Level.INFO;
		else if (val.equalsIgnoreCase("WARNING"))
			return Level.WARNING;
		else if (val.equalsIgnoreCase("SEVERE"))
			return Level.SEVERE;
		else if (val.equalsIgnoreCase("FINE"))
			return Level.FINE;
		else if (val.equalsIgnoreCase("FINER"))
			return Level.FINER;
		else if (val.equalsIgnoreCase("FINEST"))
			return Level.FINEST;
		else if (val.equalsIgnoreCase("ALL"))
			return Level.ALL;
		else if (val.equalsIgnoreCase("OFF"))
			return Level.OFF;
		else
			return Level.INFO;
	}

	/**
	 * will overwrite the current physical file.
	 */
	public void forceSave() {
		config.save();

		Property prop = config.get("general", "removeDuplicateCommands", true);
		prop.comment = ("Remove commands from the list if they already exist outside of FE.");
		CommandRemover.removeDuplicateCommands = prop.getBoolean(true);

		config.addCustomCategoryComment("Core.groups", largeComment_Cat_Groups);

		config.get("Core.groups", "groupPrefix", "").set(groupPrefixFormat);
		config.get("Core.groups", "groupSuffix", "").set(groupSuffixFormat);
		config.get("Core.groups", "rank", "").set(groupRankFormat);

	}

}
