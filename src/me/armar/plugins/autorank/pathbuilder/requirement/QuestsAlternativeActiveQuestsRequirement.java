package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;

import java.util.UUID;

public class QuestsAlternativeActiveQuestsRequirement extends AbstractRequirement {

    private me.staartvin.plugins.pluginlibrary.hooks.QuestsAlternative handler = null;
    private int activeQuests = -1;

    @Override
    public String getDescription() {
        return Lang.QUESTS_ACTIVE_QUESTS_REQUIREMENT.getConfigValue(activeQuests);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getNumberOfActiveQuests(uuid) + "/" + activeQuests;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isAvailable())
            return false;

        return handler.getNumberOfActiveQuests(uuid) >= activeQuests;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.QUESTS_ALTERNATIVE);

        handler =
                (me.staartvin.plugins.pluginlibrary.hooks.QuestsAlternative) this.getDependencyManager().getLibraryHook(Library.QUESTS_ALTERNATIVE);

        if (options.length > 0) {
            try {
                activeQuests = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (activeQuests < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }
}
