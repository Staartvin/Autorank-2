package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.autorank.Library;
import me.staartvin.utils.pluginlibrary.autorank.hooks.AureliumSkillsHook;

import java.util.UUID;

public class AureliumSkillsManaRequirement extends AbstractRequirement {

    private AureliumSkillsHook handler = null;
    private double requiredMana = -1.0f;

    @Override
    public String getDescription() {

        return Lang.AURELIUM_SKILLS_MANA_REQUIREMENT.getConfigValue(requiredMana);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getMana(uuid) + "/" + requiredMana;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isHooked())
            return false;

        return handler.getMana(uuid) >= requiredMana;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.AURELIUM_SKILLS);

        handler = (AureliumSkillsHook) this.getDependencyManager().getLibraryHook(Library.AURELIUM_SKILLS).orElse(null);

        if (options.length > 0) {
            try {
                requiredMana = Double.parseDouble(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (requiredMana < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return handler != null;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return handler.getMana(uuid) / requiredMana;
    }
}
