package com.forgeessentials.api.permissions;

import java.util.ArrayList;

import net.minecraft.world.World;

import com.forgeessentials.api.areas.Selection;
import com.forgeessentials.api.areas.WorldArea;
import com.forgeessentials.api.areas.WorldPoint;
import com.forgeessentials.api.permissions.types.Zone;

public interface IZoneManager {

	Zone getWorldZone(World world);

	void deleteZone(String zoneID);

	boolean doesZoneExist(String zoneID);

	Zone getZone(String zoneID);

	boolean createZone(String zoneID, Selection sel, World world);

	Zone getWhichZoneIn(WorldPoint point);

	Zone getWhichZoneIn(WorldArea area);

	ArrayList<Zone> getZoneList();

	Zone getGLOBAL();

	Zone getSUPER();

}