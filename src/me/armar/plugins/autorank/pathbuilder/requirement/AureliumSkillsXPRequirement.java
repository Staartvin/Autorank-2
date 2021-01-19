package me.armar.plugins.autorank.pathbuilder.requirement;

import com.archyx.aureliumskills.skills.Skill;
import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.AureliumSkillsHook;

import java.util.Locale;
import java.util.UUID;

public class AureliumSkillsXPRequirement extends AbstractRequirement {

    private AureliumSkillsHook handler = null;
    private double requiredXP = -1.0f;
    private String skill = "AGILITY";

    @Override
    public String getDescription() {

        return Lang.AURELIUM_SKILLS_XP_REQUIREMENT.getConfigValue(requiredXP, skill);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getXP(uuid, skill) + "/" + requiredXP;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isHooked())
            return false;

        return handler.getXP(uuid, skill) >= requiredXP;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.AURELIUM_SKILLS);

        handler = (AureliumSkillsHook) this.getDependencyManager().getLibraryHook(Library.AURELIUM_SKILLS).orElse(null);

        if (options.length > 0) {
            try {
                requiredXP = Double.parseDouble(options[1]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }

            try {
                skill = Skill.valueOf(options[0].trim().toUpperCase(Locale.ROOT)).getDisplayName(Locale.ENGLISH);
            } catch (Exception e) {
                this.registerWarningMessage("The skill '" + options[0].trim() + "' does not exist!");
                return false;
            }
        }

        if (requiredXP < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return handler != null;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return handler.getXP(uuid, skill) / requiredXP;
    }
}
