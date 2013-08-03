package com.forgeessentials.core.modulelauncher;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.modulelauncher.util.CallableMap;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class ModuleLauncher {
	public ModuleLauncher()
	{
		instance = this;
	}

	public static ModuleLauncher					instance;
	private static TreeMap<String, ModuleContainer>	containerMap	= new TreeMap<String, ModuleContainer>();

	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.felog.info("Discovering and loading modules...");

		// started ASM handling for the module loading.
		Set<ASMData> data = e.getAsmData().getAll(FEComponent.class.getName());

		// LOAD THE MODULES!
		ModuleContainer temp, other;
		for (ASMData asm : data)
		{
			temp = new ModuleContainer(asm);
			if (temp.isLoadable)
			{
				if (containerMap.containsKey(temp.name))
				{
					other = containerMap.get(temp.name);
					if (temp.doesOverride && other.mod == ForgeEssentials.instance)
					{
						containerMap.put(temp.name, temp);
					}
					else if (temp.mod == ForgeEssentials.instance && other.doesOverride)
					{
						continue;
					}
					else
						throw new RuntimeException("{FE-Module-Launcher} " + temp.name + " is conflicting with " + other.name);
				}
				else
				{
					containerMap.put(temp.name, temp);
				}

				temp.createAndPopulate();
				OutputHandler.felog.info("Loaded " + temp.name);
			}
		}

		Collection<ModuleContainer> modules = containerMap.values();

		CallableMap map = new CallableMap();

		data = e.getAsmData().getAll(ForgeEssentialsRegistrar.class.getName());
		Class<?> c;
		Object obj = null;
		for (ASMData asm : data)
		{
			try
			{
				obj = null;
				c = Class.forName(asm.getClassName());

				try
				{
					obj = c.newInstance();
					map.scanObject(obj);
					// this works?? skip everything else and go on to the next one.
					continue;
				}
				catch (Exception e1)
				{
					// do nothing.
				}

				// if this isn't skipped.. it grabs the class, and all static methods.
				map.scanClass(c);

			}
			catch (ClassNotFoundException e1)
			{
				// nothing needed.
			}
		}

		for (ModContainer container : Loader.instance().getModList())
		{
			if (container.getMod() != null)
			{
				map.scanObject(container);
			}
		}

		// check modules for the CallableMap stuff.
		for (ModuleContainer module : modules)
		{
			map.scanObject(module);
		}

		// run the preinits.
		for (ModuleContainer module : modules)
		{
			module.runPreInit(e, map);
		}

		// run the config init methods..
		boolean generate = false;
		for (ModuleContainer module : modules)
		{
			BaseConfig cfg = module.getConfig();

			if (cfg != null)
			{
				File file = cfg.getFile();

				if (!file.getParentFile().exists())
				{
					generate = true;
					file.getParentFile().mkdirs();
				}

				if (!generate && (!file.exists() || !file.isFile()))
				{
					generate = true;
				}

				cfg.setGenerate(generate);
				cfg.load();
			}
		}
	}

	public void load(FMLInitializationEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runInit(e);
		}
	}

	public void postLoad(FMLPostInitializationEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runPostInit(e);
		}
	}

	public void serverStarting(FMLServerStartingEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runServerInit(e);
		}
	}

	public void serverStarted(FMLServerStartedEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runServerPostInit(e);
		}
	}

	public void serverStopping(FMLServerStoppingEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runServerStop(e);
		}
	}
	
	public void serverStopped(FMLServerStoppedEvent e){
		for (ModuleContainer module : containerMap.values()){
			module.runServerStopped(e);
		}
	}

	public void reloadConfigs(ICommandSender sender)
	{
		BaseConfig config;
		for (ModuleContainer module : containerMap.values())
		{
			config = module.getConfig();
			if (config != null)
			{
				config.load();
			}
		}
	}

	public static String[] getModuleList()
	{
		return containerMap.keySet().toArray(new String[] {});
	}
}


