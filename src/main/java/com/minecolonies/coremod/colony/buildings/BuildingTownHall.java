package com.minecolonies.coremod.colony.buildings;

import com.minecolonies.blockout.views.Window;
import com.minecolonies.coremod.achievements.ModAchievements;
import com.minecolonies.coremod.client.gui.WindowTownHall;
import com.minecolonies.coremod.colony.Colony;
import com.minecolonies.coremod.colony.ColonyView;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class BuildingTownHall extends AbstractBuildingHut
{
    private static final String TOWN_HALL = "TownHall";

    public BuildingTownHall(final Colony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return TOWN_HALL;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return 4;
    }

    @Override
    public void onUpgradeComplete(final int newLevel)
    {
        super.onUpgradeComplete(newLevel);

        if (newLevel == 1)
        {
            this.getColony().triggerAchievement(ModAchievements.achievementBuildingTownhall);
        }
        if (newLevel >= this.getMaxBuildingLevel())
        {
            this.getColony().triggerAchievement(ModAchievements.achievementUpgradeTownhallMax);
        }
    }

    public static class View extends AbstractBuildingHut.View
    {
        public View(final ColonyView c, final BlockPos l)
        {
            super(c, l);
        }

        @NotNull
        public Window getWindow()
        {
            return new WindowTownHall(this);
        }
    }
}
