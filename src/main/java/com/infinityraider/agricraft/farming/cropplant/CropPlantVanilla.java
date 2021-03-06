package com.infinityraider.agricraft.farming.cropplant;

import com.infinityraider.agricraft.api.v1.IGrowthRequirement;
import com.infinityraider.agricraft.api.v1.RenderMethod;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.init.AgriCraftItems;
import com.infinityraider.agricraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantVanilla extends CropPlant {
	
    private final BlockCrops plant;
    private final ItemSeeds seed;
    private final String[] textures;

    public CropPlantVanilla(BlockCrops crop, ItemSeeds seed, String textureBase) {
        this.plant = crop;
        this.seed = seed;
        textures = new String[8];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = "minecraft:blocks/" + textureBase + "_stage_" + i;
        }
		AgriCraftItems.clipping.addPlant(this, textures[7]);
    }

    @Override
    public int tier() {
        return 1;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(seed);
    }

    @Override
    public Block getBlock() {
        return plant;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(plant.getItemDropped(plant.getStateFromMeta(7), null, 0)));
        return list;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(plant.getItemDropped(plant.getStateFromMeta(7), rand, 0));
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<>();
        while(amount>0) {
            list.add(getRandomFruit(rand));
            amount--;
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return true;
    }

    @Override
    protected IGrowthRequirement initGrowthRequirement() {
        return GrowthRequirementHandler.getNewBuilder().build();
    }

    @Override
    public void onAllowedGrowthTick(World world, BlockPos pos, int oldGrowthStage) {}

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.UNIT*13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderMethod getRenderMethod() {
        return RenderMethod.HASHTAG;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getPrimaryPlantTexture(int growthStage) {
        growthStage = Math.max(Math.min(growthStage, textures.length-1), 0);
        //TODO: get vanilla textures
        return null;
    }

    @Override
    public ResourceLocation getSecondaryPlantTexture(int growthStage) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = seed.getUnlocalizedName();
        int index = name.indexOf('_');
        if(index<0) {
            index = name.indexOf('.');
        }
        name = name.substring(index+1);
        return "agricraft_journal."+name;
    }
}
