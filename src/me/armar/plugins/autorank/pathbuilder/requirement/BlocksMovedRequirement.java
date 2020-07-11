package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.query.parameter.implementation.MovementTypeParameter;

import java.util.UUID;

public class BlocksMovedRequirement extends AbstractRequirement {

    BlocksMovedWrapper wrapper = null;

    @Override
    public String getDescription() {

        String desc = Lang.BLOCKS_MOVED_REQUIREMENT.getConfigValue(wrapper.getBlocksMoved() + "",
                wrapper.getMovementType());

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            desc = desc.concat(" (in world '" + this.getWorld() + "')");
        }

        return desc;
    }

    @Override
    public String getProgressString(UUID uuid) {

        final int progressBar = this.getStatsPlugin().getBlocksMoved(uuid, this.getWorld());

        return progressBar + "/" + wrapper.getBlocksMoved() + " (" + wrapper.getMovementType() + ")";
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!getStatsPlugin().isEnabled()) {
            return false;
        }

        final int count = this.getStatsPlugin().getBlocksMoved(uuid, this.getWorld());

        return count >= wrapper.getBlocksMoved();
    }

    @Override
    public boolean initRequirement(final String[] options) {

        int blocksMoved = 0;
        int movementType = 0;

        if (options.length > 0) {
            blocksMoved = Integer.parseInt(options[0].trim());
        }
        if (options.length > 1) {
            movementType = Integer.parseInt(options[1].trim());
        }

        wrapper = new BlocksMovedWrapper(blocksMoved, movementType);

        if (wrapper == null) {
            this.registerWarningMessage("No valid block provided.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        final int progressBar = this.getStatsPlugin().getBlocksMoved(uuid, this.getWorld());

        return progressBar * 1.0d / wrapper.getBlocksMoved();
    }
}

class BlocksMovedWrapper {

    private int blocksMoved = 0;
    private String movementType = "";
    private int rawMovementType = 0;

    public BlocksMovedWrapper(final int blocksMoved, final int moveType) {
        this.blocksMoved = blocksMoved;
        this.movementType = getMovementString(moveType);
        this.rawMovementType = moveType;
    }

    public int getBlocksMoved() {
        return blocksMoved;
    }

    private String getMovementString(final int moveType) {
        switch (moveType) {
            case 1:
                return "by boat";
            case 2:
                return "by cart";
            case 3:
                return "by pig";
            case 4:
                return "by piggy-cart";
            case 5:
                return "by horse";
            default:
                return "by foot";
        }
    }

    public String getMovementType() {
        return movementType;
    }

    public MovementTypeParameter.MovementType getRawMovementType() {
        switch (rawMovementType) {
            case 1:
                return MovementTypeParameter.MovementType.BOAT;
            case 2:
                return MovementTypeParameter.MovementType.MINECART;
            case 3:
                return MovementTypeParameter.MovementType.PIG;
            case 4:
                return MovementTypeParameter.MovementType.PIG_IN_MINECART;
            case 5:
                return MovementTypeParameter.MovementType.HORSE;
            default:
                return MovementTypeParameter.MovementType.FOOT;
        }
    }
}
