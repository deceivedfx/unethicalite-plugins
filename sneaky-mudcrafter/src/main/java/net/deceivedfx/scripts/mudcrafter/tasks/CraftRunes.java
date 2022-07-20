package net.deceivedfx.scripts.mudcrafter.tasks;

import lombok.extern.slf4j.Slf4j;
import net.deceivedfx.scripts.mudcrafter.SneakyMudCrafterScript;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

@Slf4j
public class CraftRunes implements ScriptTask
{
    private static final int EARTH_ALTAR_MAP_REGION = 10571;
    boolean clickedRuins;
    TileObject earthAltar;
    TileObject exitPortal;

    @Override
    public boolean validate()
    {
        return this.isInAltarRoom();
    }

    @Override
    public int execute()
    {
        earthAltar = TileObjects.getNearest(obj -> obj != null && obj.getId() == 34763);
        exitPortal = TileObjects.getNearest(obj -> obj != null && obj.getId() == 34751);
        if (Inventory.contains(1444))
        {
            Inventory.getFirst(1444).useOn(earthAltar);
            SneakyMudCrafterScript.status = "Crafting runes";
            log.info("using talisman on altar");
            Time.sleepUntil(() -> Inventory.contains(4698), 6000);
            return 2000;
        }
        if (Inventory.contains(4698) && !Inventory.contains(1444))
        {
            SneakyMudCrafterScript.status = "Leaving altar area";
            log.info("leaving altar room");
            exitPortal.interact("Use");
            Time.sleepUntil(() -> !isInAltarRoom(), 6000);
            return 2000;
        }
        return 800;
    }

    private boolean isInAltarRoom()
    {
        earthAltar = TileObjects.getNearest(obj -> obj != null && obj.getId() == 34763 && obj.distanceTo(Players.getLocal()) <= 20);
        if (this.earthAltar != null)
        {
            this.clickedRuins = false;
        }
        return this.earthAltar != null;
    }
}
