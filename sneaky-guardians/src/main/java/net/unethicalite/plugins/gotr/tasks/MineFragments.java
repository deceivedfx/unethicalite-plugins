package net.unethicalite.plugins.gotr.tasks;

import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldArea;
import net.unethicalite.api.account.LocalPlayer;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.plugins.gotr.SneakyGoTRConfig;

import javax.inject.Inject;

public class MineFragments implements ScriptTask
{
    @Inject
    private SneakyGoTRConfig config;

    public boolean has_enough_mats = false;
    private static final WorldArea INSIDE_GUARDIANS = new WorldArea(3586, 9484, 70, 70, 0);

    @Override
    public boolean validate() {
        return INSIDE_GUARDIANS.contains(LocalPlayer.get()) && !has_enough_mats;
    }

    @Override
    public int execute() {
        if(!has_enough_mats)
        {
            if (Inventory.getCount(true, ItemID.GUARDIAN_FRAGMENTS) < config.guardianFragments())
            {
                if(!LocalPlayer.get().isAnimating())
                {
                    TileObjects.getNearest("Rubble").interact("Climb");
                    TileObjects.getNearest(43719).interact("Mine");
                }
            }
            else
            {
                has_enough_mats = true;
            }
        }
        return 0;
    }
}
