package net.deceivedfx.scripts.mudcrafter.tasks;

import lombok.extern.slf4j.Slf4j;
import net.deceivedfx.scripts.mudcrafter.SneakyMudCrafterScript;
import net.runelite.api.Item;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.movement.pathfinder.model.BankLocation;

import javax.inject.Inject;

// TODO add stamina support

@Slf4j
public class DoBank implements ScriptTask
{

    @Inject
    private SneakyMudCrafterScript script;
    TileObject bankBooth;

    @Override
    public boolean validate()
    {
        return BankLocation.VARROCK_EAST_BANK.getArea().contains(Players.getLocal()) && canRunecraft();
    }

    @Override
    public int execute()
    {
        bankBooth = TileObjects.getNearest(tileObject -> tileObject.getName().contains("Bank booth"));
        var local = Players.getLocal();
        switch (getProcess())
        {
            case OPEN:
                openBank();
                break;
            case DEPOSIT:
                Bank.depositAllExcept("Water rune");
                break;
            case WITHDRAW_NECKLACE:
                withdrawNecklace();
                break;
            case EQUIP_NECKLACE:
                equipNecklace();
                break;
            case WITHDRAW_RUNES:
                withdrawRunes();
                break;
            case WITHDRAW_TALISMAN:
                withdrawTalisman();
                break;
            case WITHDRAW_ESSENCE:
                withdrawEssence();
                Bank.close();
                break;
        }
        return 1000;
    }

    private bankProcessState getProcess()
    {
        var local = Players.getLocal();
        if (!Bank.isOpen() && this.canRunecraft())
        {
            return bankProcessState.OPEN;
        }
        if (Inventory.contains(4698)) {
            return bankProcessState.DEPOSIT;
        }
        if (!Equipment.contains(5521) && !Inventory.contains(5521))
        {
            return bankProcessState.WITHDRAW_NECKLACE;
        }
        if (!Equipment.contains(5521) && Inventory.contains(5521))
        {
            return bankProcessState.EQUIP_NECKLACE;
        }
        if (!Inventory.contains(555))
        {
            return bankProcessState.WITHDRAW_RUNES;
        }
        if (Inventory.contains(555) && !Inventory.contains(1444))
        {
            return bankProcessState.WITHDRAW_TALISMAN;
        }
        if (Inventory.contains(555) && Inventory.contains(1444) && !Inventory.contains(7936))
        {
            return bankProcessState.WITHDRAW_ESSENCE;
        }
        if (Inventory.contains(555) && Inventory.contains(1444) && Inventory.contains(7936) && Equipment.contains(5521))
        {
            return bankProcessState.CLOSE;
        }
        return bankProcessState.IDLE;
    }

    private boolean canRunecraft()
    {
        return !Inventory.contains(555) || !Inventory.contains(1444) || !Inventory.contains(7936) || !Equipment.contains(5521);
    }

    private void openBank()
    {
        if (bankBooth == null)
        {
            return;
        }
        if (Reachable.isInteractable(bankBooth))
        {
            log.info("open bank");
            SneakyMudCrafterScript.status = "Opening bank";
            bankBooth.interact("Bank");
            Time.sleepUntil(Bank::isOpen, 4000);
        }
    }

    private void withdrawNecklace()
    {
        final Item necklace = Bank.getFirst(5521);
        if (necklace == null)
        {
            log.info("no necklaces");
        }
        if (Bank.contains(5521))
        {
            log.info("withdraw necklace");
            SneakyMudCrafterScript.status = "Withdraw necklace";
            Bank.withdraw(5521, 1, Bank.WithdrawMode.ITEM);
            Time.sleepUntil(() -> Inventory.contains(5521), 2000);
        }
    }

    private void equipNecklace()
    {
        final Item necklace = Inventory.getFirst(5521);
        if (necklace == null)
        {
            log.info("no necklaces");
        }
        if (Inventory.contains(5521))
        {
            log.info("equip necklace");
            SneakyMudCrafterScript.status = "Equipping necklace";
            Inventory.getFirst(5521).interact("Wear");
            Time.sleepUntil(() -> Equipment.contains(5521), 2000);
        }
    }

    private void withdrawRunes()
    {
        final Item waterRune = Bank.getFirst(555);
        if (waterRune == null)
        {
            log.info("no water runes");
        }
        if (Bank.contains(555))
        {
            log.info("withdraw water runes");
            SneakyMudCrafterScript.status = "Withdraw runes";
            Bank.withdrawAll(555, Bank.WithdrawMode.ITEM);
            Time.sleepUntil(() -> Inventory.contains(555), 2000);
        }
    }

    private void withdrawTalisman()
    {
        final Item talisman = Bank.getFirst(1444);
        if (talisman == null)
        {
            log.info("no water talisman");
        }
        if (Bank.contains(1444))
        {
            log.info("withdraw water talisman");
            SneakyMudCrafterScript.status = "Withdraw talisman";
            Bank.withdraw(1444, 1, Bank.WithdrawMode.ITEM);
            Time.sleepUntil(() -> Inventory.contains(1444), 2000);
        }
    }

    private void withdrawEssence()
    {
        final Item essence = Bank.getFirst(7936);
        if (essence == null) {
            log.info("no essence");
        }
        if (Bank.contains(7936))
        {
            log.info("withdraw essence");
            SneakyMudCrafterScript.status = "Withdraw essence";
            Bank.withdrawAll(7936, Bank.WithdrawMode.ITEM);
            Time.sleepUntil(() -> Inventory.contains(7936), 2000);
        }
    }

    private enum bankProcessState
    {
        OPEN,
        DEPOSIT,
        WITHDRAW_NECKLACE,
        EQUIP_NECKLACE,
        WITHDRAW_STAMINA,
        DRINK_STAMINA,
        WITHDRAW_RUNES,
        WITHDRAW_TALISMAN,
        WITHDRAW_ESSENCE,
        IDLE,
        CLOSE
    }
}
