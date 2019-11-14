package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.QuestsAlternative;
import org.bukkit.entity.Player;

public class QuestsAlternativeCompletedQuestsRequirement extends AbstractRequirement {

    private QuestsAlternative handler = null;
    private int completedQuests = -1;

    @Override
    public String getDescription() {
        return Lang.QUESTS_COMPLETED_QUESTS_REQUIREMENT.getConfigValue(completedQuests);
    }

    @Override
    public String getProgress(final Player player) {
        return handler.getNumberOfCompletedQuests(player.getUniqueId()) + "/" + completedQuests;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!handler.isAvailable())
            return false;

        return handler.getNumberOfCompletedQuests(player.getUniqueId()) >= completedQuests;
    }

    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.QUESTS_ALTERNATIVE);

        handler = (QuestsAlternative) this.getDependencyManager().getLibraryHook(Library.QUESTS_ALTERNATIVE);

        if (options.length > 0) {
            try {
                completedQuests = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (completedQuests < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (handler == null || !handler.isAvailable()) {
            this.registerWarningMessage("Quests is not available!");
            return false;
        }

        return true;
    }
}
