package com.minecolonies.colony.buildings;

import com.minecolonies.client.gui.WindowHutFarmer;
import com.minecolonies.colony.CitizenData;
import com.minecolonies.colony.Colony;
import com.minecolonies.colony.ColonyView;
import com.minecolonies.colony.Field;
import com.minecolonies.colony.jobs.AbstractJob;
import com.minecolonies.colony.jobs.JobFarmer;
import com.minecolonies.tileentities.ScarecrowTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class which handles the farmer building.
 */
public class BuildingFarmer extends AbstractBuildingWorker
{
    /**
     * Descriptive string of the profession.
     */
    private static final    String  FARMER      = "Farmer";

    /**
     * The maximum building level of the hut.
     */
    private static final    int     MAX_BUILDING_LEVEL = 3;

    /**
     * NBTTag to store the fields.
     */
    private static final String TAG_FIELDS = "fields";

    /**
     * NBTTag to store the currentField.
     */
    private static final String TAG_CURRENT_FIELD = "currentField";

    /**
     * The list of the fields the farmer manages.
     */
    private final ArrayList<Field> farmerFields = new ArrayList<>();

    /**
     * The field the farmer is currently working on.
     */
    private Field currentField;

    /**
     * Public constructor which instantiates the building.
     * @param c the colony the building is in.
     * @param l the position it has been placed (it's id).
     */
    public BuildingFarmer(Colony c, BlockPos l)
    {
        super(c, l);
    }


    /**
     * Returns list of fields of the farmer.
     * @return a list of field objects.
     */
    public List<Field> getFarmerFields()
    {
        return Collections.unmodifiableList(farmerFields);
    }

    /**
     * Checks if the farmer has any fields.
     * @return true if he has none.
     */
    public boolean hasNoFields()
    {
        return farmerFields.isEmpty();
    }

    /**
     * Assigns a field list to the field list.
     * @param field the field to add.
     */
    public void addFarmerFields(Field field)
    {
        farmerFields.add(field);
    }

    /**
     * Getter of the current field.
     * @return a field object.
     */
    public Field getCurrentField()
    {
        return currentField;
    }

    /**
     * Sets the field the farmer is currently working on.
     * @param currentField the field to work on.
     */
    public void setCurrentField(final Field currentField)
    {
        this.currentField = currentField;
    }

    /**
     * Retrieves a random field to work on for the farmer.
     * @return a field to work on.
     */
    public Field getFieldToWorkOn()
    {
        Collections.shuffle(farmerFields);
        for(Field field: farmerFields)
        {
            if(field.needsWork())
            {
                currentField = field;
                return field;
            }
        }
        return null;
    }

    @Override
    public String getSchematicName()
    {
        return FARMER;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @Override
    public String getJobName()
    {
        return FARMER;
    }

    /**
     * Synchronize field list with colony.
     * @param world the world the building is in.
     */
    public void synchWithColony(World world)
    {
        if(!farmerFields.isEmpty())
        {
            final ArrayList<Field> tempFields = new ArrayList<>(farmerFields);

            for(final Field field: tempFields)
            {
                final ScarecrowTileEntity scarecrow = (ScarecrowTileEntity) world.getTileEntity(field.getID());
                if(scarecrow == null)
                {
                    farmerFields.remove(field);
                    if (currentField != null && currentField.getID() == field.getID())
                    {
                        currentField = null;
                    }
                }
                else
                {
                    field.setInventoryField(scarecrow.inventoryField);
                    if(currentField != null && currentField.getID() == field.getID())
                    {
                        currentField.setInventoryField(scarecrow.inventoryField);
                    }
                }
            }
        }
    }

    /**
     * Resets the fields to need work again.
     */
    public void resetFields()
    {
        for(final Field field: farmerFields)
        {
            field.setNeedsWork(true);
            field.calculateSize(getColony().getWorld(), field.getLocation().down());
        }
    }

    @Override
    public void onDestroyed()
    {
        super.onDestroyed();
        for(final Field field: farmerFields)
        {
            field.setTaken(false);
        }
    }

    @Override
    public AbstractJob createJob(CitizenData citizen)
    {
        return new JobFarmer(citizen);
    }

    //we have to update our field from the colony!
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        final NBTTagList fieldTagList = compound.getTagList(TAG_FIELDS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < fieldTagList.tagCount(); ++i)
        {
            final NBTTagCompound fieldCompound = fieldTagList.getCompoundTagAt(i);
            final Field f = Field.createFromNBT(getColony(), fieldCompound);
            if (f != null)
            {
                farmerFields.add(f);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        final NBTTagList fieldTagList = new NBTTagList();
        for (final Field f : farmerFields)
        {
            final NBTTagCompound fieldCompound = new NBTTagCompound();
            f.writeToNBT(fieldCompound);
            fieldTagList.appendTag(fieldCompound);
        }
        compound.setTag(TAG_FIELDS, fieldTagList);
        compound.setInteger(TAG_CURRENT_FIELD,farmerFields.indexOf(currentField));
    }

    /**
     * Provides a view of the miner building class.
     */
    public static class View extends AbstractBuildingWorker.View
    {
        /**
         * Public constructor of the view, creates an instance of it.
         *
         * @param c the colony.
         * @param l the position.
         */
        public View(ColonyView c, BlockPos l)
        {
            super(c, l);
        }

        public com.blockout.views.Window getWindow()
        {
            return new WindowHutFarmer(this);
        }
    }
}

