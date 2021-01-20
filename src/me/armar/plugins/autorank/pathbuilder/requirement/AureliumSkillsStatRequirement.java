package me.armar.plugins.autorank.pathbuilder.requirement;

import com.archyx.aureliumskills.stats.Stat;
import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.autorank.Library;
import me.staartvin.utils.pluginlibrary.autorank.hooks.AureliumSkillsHook;

import java.util.Locale;
import java.util.UUID;

public class AureliumSkillsStatRequirement extends AbstractRequirement {

    private AureliumSkillsHook handler = null;
    private double requiredLevel = -1.0f;
    private String stat = "HEALTH";

    @Override
    public String getDescription() {

        return Lang.AURELIUM_SKILLS_STAT_REQUIREMENT.getConfigValue(requiredLevel,
                stat);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getStatLevel(uuid, stat) + "/" + requiredLevel;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isHooked())
            return false;

        return handler.getStatLevel(uuid, stat) >= requiredLevel;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.AURELIUM_SKILLS);

        handler = (AureliumSkillsHook) this.getDependencyManager().getLibraryHook(Library.AURELIUM_SKILLS).orElse(null);

        if (options.length > 0) {
            try {
                requiredLevel = Double.parseDouble(options[1]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }

            try {
                stat = Stat.valueOf(options[0].trim().toUpperCase(Locale.ROOT)).getDisplayName(Locale.ENGLISH);
            } catch (Exception e) {
                this.registerWarningMessage("The stat '" + options[0].trim() + "' does not exist!");
                return false;
            }
        }

        if (requiredLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return handler != null;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return handler.getStatLevel(uuid, stat) / requiredLevel;
    }
}
