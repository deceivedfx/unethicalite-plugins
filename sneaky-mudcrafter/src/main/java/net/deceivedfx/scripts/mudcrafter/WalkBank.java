package net.deceivedfx.scripts.mudcrafter;

import lombok.extern.slf4j.Slf4j;
import net.deceivedfx.scripts.mudcrafter.tasks.ScriptTask;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.pathfinder.model.BankLocation;

@Slf4j
public class WalkBank implements ScriptTask
{

    TileObject earthAltar;
    boolean clickedRuins;
    private static final WorldPoint bankTile = new WorldPoint(3254, 3420, 0);

    public WalkBank()
    {
        clickedRuins = false;
    }

    @Override
    public boolean validate()
    {
        return !isInAltarRoom() && Inventory.contains(4698) && !BankLocation.VARROCK_EAST_BANK.getArea().contains(Players.getLocal());
    }

    @Override
    public int execute()
    {
        if (Movement.isWalking())
        {
            return 600;
        }
        if (!BankLocation.VARROCK_EAST_BANK.getArea().contains(Players.getLocal()))
        {
            SneakyMudCrafterScript.status = "Walking to bank";
            Movement.walkTo(BankLocation.VARROCK_EAST_BANK);
        }
        return 800;
    }

    private boolean isInAltarRoom()
    {
        earthAltar = TileObjects.getNearest(obj -> obj != null && obj.getId() == 34763 && obj.distanceTo(Players.getLocal()) <= 20);
        if (earthAltar != null)
        {
            clickedRuins = false;
        }
        return earthAltar != null;
    }
}
