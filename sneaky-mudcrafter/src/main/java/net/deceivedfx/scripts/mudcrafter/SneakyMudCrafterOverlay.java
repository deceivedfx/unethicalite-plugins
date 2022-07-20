package net.deceivedfx.scripts.mudcrafter;

import com.openosrs.client.ui.overlay.components.table.TableAlignment;
import com.openosrs.client.ui.overlay.components.table.TableComponent;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;
import net.unethicalite.api.commons.Time;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Slf4j
@Singleton
class SneakyMudCrafterOverlay extends OverlayPanel
{

    private final Client client;
    private final SneakyMudCrafterScript script;
    private final SneakyMudCrafterConfig config;
    String timeFormat;

    @Inject
    private SneakyMudCrafterOverlay(final Client client, final SneakyMudCrafterScript script, final SneakyMudCrafterConfig config)
    {
        super(script);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.client = client;
        this.script = script;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Mud Crafter Overlay"));
    }

    public Dimension render(final Graphics2D graphics)
    {
        if (!this.config.showOverlay()) {
            SneakyMudCrafterOverlay.log.debug("Overlay conditions not met, not starting overlay");
            return null;
        }
        final TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
        tableComponent.addRow("Status:", SneakyMudCrafterScript.status);
        if (script.isRunning()) {
            final Duration duration = Duration.between(script.botTimer, Instant.now());
            timeFormat = ((duration.toHours() < 1L) ? "mm:ss" : "HH:mm:ss");
            tableComponent.addRow("Runtime:", Time.format(duration));
            tableComponent.addRow("Runes:", NumberFormat.getNumberInstance(Locale.US).format(script.totalCraftedRunes)
                    + " / " + NumberFormat.getNumberInstance(Locale.US).format(script.runesPH));
            tableComponent.addRow("Levels gained:", String.valueOf(client.getRealSkillLevel(Skill.RUNECRAFT) - script.initialLevel));
            if (duration.toSeconds() != 0L) {
                final double hoursIn = duration.toSeconds() * 2.77777778E-4;
                tableComponent.addRow("Gp/hr:", String.valueOf(Math.floor(script.mudRunePrice * script.runesCrafted / hoursIn - script.suppliesCost)));
            } else {
                tableComponent.addRow("Gp/hr:", "N/A");
            }
        } else {
            tableComponent.addRow("Time running:", "00:00");
            tableComponent.addRow("Runes crafted:", "N/A");
            tableComponent.addRow("Levels gained:", "N/A");
            tableComponent.addRow("Gp/hr:", "N/A");
        }
        if (!tableComponent.isEmpty())
        {
            panelComponent.setBackgroundColor(ColorUtil.fromHex("#B3121212"));
            panelComponent.setPreferredSize(new Dimension(250, 200));
            panelComponent.setBorder(new Rectangle(5, 5, 5, 5));
            panelComponent.getChildren().add(TitleComponent.builder().text("Sneaky Mud Crafter").color(ColorUtil.fromHex("#a5492a")).build());
           /* if (script.run) {
                panelComponent.getChildren().add(LineComponent.builder().left("Active:").right(String.valueOf(script.isRunning())).rightColor(Color.GREEN).build());
            } else {
                panelComponent.getChildren().add(LineComponent.builder().left("Active:").right(String.valueOf(script.isRunning())).rightColor(Color.RED).build());
            }*/
            panelComponent.getChildren().add(tableComponent);
        }
        return super.render(graphics);
    }
}
