package net.deceivedfx.scripts.mudcrafter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.deceivedfx.scripts.mudcrafter.tasks.CraftRunes;
import net.deceivedfx.scripts.mudcrafter.tasks.DoBank;
import net.deceivedfx.scripts.mudcrafter.tasks.ScriptTask;
import net.deceivedfx.scripts.mudcrafter.tasks.WalkRuins;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.pathfinder.model.BankLocation;
import net.unethicalite.api.plugins.Script;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

// TODO fix runes crafted and gold values

@Extension
@PluginDescriptor(
        name = "Sneaky Mud Crafter",
        description = "Crafts mud runes",
        enabledByDefault = false,
        tags = {"sneaky", "runecraft", "mud"})
@Slf4j
public class SneakyMudCrafterScript extends Script
{
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SneakyMudCrafterConfig config;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private ItemManager itemManager;
    @Inject
    SneakyMudCrafterOverlay overlay;

    Set<Integer> DUEL_RINGS;
    Set<Integer> BINDING_NECKLACE;
    Set<Integer> STAMINA_POTIONS;
    Set<Integer> TIARAS;
    List<Integer> REQUIRED_ITEMS;
    Player player;
    MenuEntry targetMenu;
    boolean run;
    boolean outOfNecklaces;
    int tickDelay;
    boolean threadFix;
    boolean setTalisman;
    boolean clickedRuins;
    int clickedRuinsResetCount;
    public static String status;
    int runesCrafted;
    int xpGained;
    int initialLevel;
    int mudRunePrice;
    Instant botTimer;
    LocalPoint beforeLoc;
    WorldArea AREA;
    TileObject ruins;
    TileObject altar;
    int coinsPH;
    int beforeEssence;
    int totalEssence;
    int beforeMaterialRunes;
    int totalMaterialRunes;
    int beforeTalisman;
    int totalTalisman;
    public int totalCraftedRunes;
    int beforeCraftedRunes;
    int currentCraftedRunes;
    int totalDuelRings;
    int totalNecklaces;
    int totalStaminaPots;
    int runesPH;
    int profitPH;
    int totalProfit;
    int runesCost;
    int essenceCost;
    int talismanCost;
    int necklaceCost;
    int staminaPotCost;
    int materialRuneCost;
    int essenceTypeID;
    int talismanID;
    int materialRuneID;
    int createdRuneTypeID;
    int suppliesCost;

    private static final ScriptTask[] TASKS =  { new DoBank(), new WalkRuins(), new CraftRunes(), new WalkBank() };

    @Provides
    SneakyMudCrafterConfig provideConfig(final ConfigManager configManager) {

        return configManager.getConfig(SneakyMudCrafterConfig.class);
    }

    protected void shutDown()
    {
        runesCrafted = 0;
        overlayManager.remove(overlay);
        botTimer = null;
    }

    protected int loop()
    {
        player = client.getLocalPlayer();
        for (final ScriptTask task : TASKS)
        {
            if (task.validate()) {
                final int sleep = task.execute();
                if (task.blocking()) {
                    return sleep;
                }
            }
        }
        return 1000;
    }

    public void onStart(final String... args)
    {
        overlayManager.add(overlay);
        botTimer = Instant.now();
        initialLevel = Skills.getLevel(Skill.RUNECRAFT);
    }

    public SneakyMudCrafterState getState()
    {
        var local = Players.getLocal();
        if (local.isAnimating() || local.isMoving())
        {

            return SneakyMudCrafterState.IDLE;
        }
        if (!Inventory.contains(5521) && !Equipment.contains(item -> item.getId() == 5521))
        {
            return SneakyMudCrafterState.WITHDRAW_NECKLACE;
        }
        if (!Inventory.contains(1444)) {
            return SneakyMudCrafterState.WITHDRAW_TALISMAN;
        }
        if (Movement.getRunEnergy() <= 40 && BankLocation.VARROCK_EAST_BANK.getArea().contains(local))
        {
            return SneakyMudCrafterState.WITHDRAW_STAMINA;
        }
        if (Inventory.contains(5521))
        {
            return SneakyMudCrafterState.EQUIP_NECKLACE;
        }
        if (Inventory.contains(1444) && Inventory.contains(555) && Inventory.contains(7936) && Equipment.contains(5521))
        {
            return SneakyMudCrafterState.ALTAR_WALK;
        }
        return SneakyMudCrafterState.IDLE;
    }

    public long getRunesPH()
    {
        final Duration timeSinceStart = Duration.between(this.botTimer, Instant.now());
        if (!timeSinceStart.isZero())
        {
            return (int)(this.runesCrafted * (double)Duration.ofHours(1L).toMillis() / timeSinceStart.toMillis());
        }
        return 0L;
    }

    private void initCounters()
    {
        coinsPH = 0;
        beforeEssence = 0;
        totalEssence = 0;
        beforeMaterialRunes = 0;
        totalMaterialRunes = 0;
        beforeTalisman = 0;
        totalTalisman = 0;
        beforeCraftedRunes = 0;
        totalCraftedRunes = 0;
        totalDuelRings = 0;
        totalNecklaces = 0;
        totalStaminaPots = 0;
        runesPH = 0;
        profitPH = 0;
        totalProfit = 0;
        currentCraftedRunes = 0;
        suppliesCost = 0;
    }

    private int itemTotals(final int itemID, final int beforeAmount, final boolean stackableItem)
    {
        final int currentAmount = Inventory.getCount(true, itemID);
        return (beforeAmount > currentAmount) ? (beforeAmount - currentAmount) : 0;
    }

    private void updateTotals() {
        totalEssence += itemTotals(essenceTypeID, beforeEssence, false);
        beforeEssence = Inventory.getCount(false, essenceTypeID);
        beforeMaterialRunes = Inventory.getCount(true, materialRuneID);
        totalTalisman += itemTotals(talismanID, beforeTalisman, true);
        beforeTalisman = Inventory.getCount(true, talismanID);
        currentCraftedRunes = Inventory.getCount(true, createdRuneTypeID);
        if (beforeCraftedRunes < currentCraftedRunes) {
            totalCraftedRunes += currentCraftedRunes;
        }
        beforeCraftedRunes = currentCraftedRunes;
    }

    public void updateStats() {
        updateTotals();
        runesPH = (int) getPerHour(totalCraftedRunes);
        totalProfit = (int) (totalCraftedRunes * runesCost - (totalEssence * essenceCost + totalMaterialRunes * materialRuneCost + totalTalisman * talismanCost + totalNecklaces * necklaceCost + totalStaminaPots * 0.25 * staminaPotCost));
        profitPH = (int) getPerHour(totalProfit);
    }

    public long getPerHour(final int quantity) {
        final Duration timeSinceStart = Duration.between(this.botTimer, Instant.now());
        if (!timeSinceStart.isZero()) {
            return (int)(quantity * (double)Duration.ofHours(1L).toMillis() / timeSinceStart.toMillis());
        }
        return 0L;
    }

}
