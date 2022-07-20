package net.deceivedfx.scripts.mudcrafter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("sneaky-mudcrafter")
public interface SneakyMudCrafterConfig extends Config
{
    @ConfigSection(keyName = "config", name = "Configuration", description = "Configure your settings", position = 1)
    public static final String config = "Config";

    @ConfigItem(keyName = "instruction", name = "", description = "", position = 0, title = "instructionsTitle")
    default String instruction()
    {
        return "Requires a Earth tiara equipped, uses Binding necklaces. No stamina or magic imbue support currently.";
    }

    @ConfigItem(keyName = "showOverlay", name = "Show Overlay", description = "Show Overlay?", position = 2, section = "Config")
    default boolean showOverlay()
    {
        return false;
    }

    @ConfigItem(keyName = "staminaPotion", name = "Use Stamina Potions", description = "", position = 4, section = "Config", hidden = true)
    default boolean staminaPotion()
    {
        return false;
    }
}
