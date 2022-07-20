package net.deceivedfx.scripts.mudcrafter.tasks;

import lombok.extern.slf4j.Slf4j;
import net.deceivedfx.scripts.mudcrafter.SneakyMudCrafterScript;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

@Slf4j
public class WalkRuins implements ScriptTask
{

    WorldArea AREA;
    boolean clickedRuins;

    public WalkRuins()
    {
        this.clickedRuins = false;
    }

    @Override
    public boolean validate()
    {
        return Inventory.contains(555) && Inventory.contains(1444) && Inventory.contains(7936) && Equipment.contains(5521) && !isInAltarRoom();
    }

    @Override
    public int execute()
    {
        AREA = new WorldArea(3302, 3468, 6, 6, 0);
        if (Movement.isWalking())
        {
            return 600;
        }
        if (!Bank.isOpen() && !isAtMysteriousRuin())
        {
            SneakyMudCrafterScript.status = "Walking to ruins";
            Movement.walkTo(AREA);
            return 600;
        }
        TileObject mysterious_ruins = TileObjects.getNearest(tileObject -> tileObject.getName().contains("Mysterious ruins") && tileObject.getId() == 34816 && tileObject.hasAction("Enter"));
        if (mysterious_ruins != null)
        {
            mysterious_ruins.interact("Enter");
            log.info("click ruins");
            Time.sleepUntil(this::isInAltarRoom, 5000);
        }
        return 1000;
    }

    private boolean isAtMysteriousRuin()
    {
        final TileObject mysteriousRuins = TileObjects.getNearest(obj -> obj.getId() == 34816 && obj.distanceTo(Players.getLocal()) <= 15);
        return mysteriousRuins != null;
    }

    private boolean isInAltarRoom()
    {
        final TileObject earthAltar = TileObjects.getNearest(obj -> obj.getId() == 34763);
        return earthAltar != null;
    }
}
