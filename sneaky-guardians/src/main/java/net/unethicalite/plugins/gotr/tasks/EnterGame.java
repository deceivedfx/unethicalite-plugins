package net.unethicalite.plugins.gotr.tasks;

import net.runelite.api.DynamicObject;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.account.LocalPlayer;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.plugins.gotr.SneakyGoTRConfig;

public class EnterGame implements ScriptTask {

    public boolean game_started = false;
    private static final WorldArea INSIDE_GUARDIANS = new WorldArea(3586, 9484, 70, 70, 0);
    private static final WorldArea OUTSIDE_GUARDIANS = new WorldArea(3611, 9471, 9, 12, 0);

    SneakyGoTRConfig config = null;

    public EnterGame(SneakyGoTRConfig config) { this.config = config; }

    @Override
    public boolean validate() {
        return OUTSIDE_GUARDIANS.contains(LocalPlayer.get());
    }

    @Override
    public int execute() {
        if (!game_started) {

            if (OUTSIDE_GUARDIANS.contains(LocalPlayer.get())) {
                var barrier = TileObjects.getNearest(43700);
                if (barrier != null) {
                    var fuck = ((GameObject) barrier).getRenderable();

                    DynamicObject object = (DynamicObject) fuck;
                    if (object.getAnimationID() == 9366) {
                        barrier.interact("Pass");
                        return Rand.nextInt(350, 500);
                    }
                }
            }

        }
        return 1000;
    }
}