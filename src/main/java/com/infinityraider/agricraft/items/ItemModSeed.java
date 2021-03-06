package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.IAgriCraftSeed;
import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.blocks.BlockModPlant;
import com.infinityraider.agricraft.creativetab.AgriCraftTab;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.handler.config.MutationConfig;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.utility.LogHelper;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ItemModSeed extends ItemSeeds implements IAgriCraftSeed {

	@SideOnly(Side.CLIENT)
	private String information;

	//@SideOnly(Side.CLIENT)
	private final String seedName;

	/**
	 * This constructor shouldn't be called from anywhere except from the
	 * BlockModPlant public constructor, if you create a new BlockModPlant, its
	 * contructor will create the seed for you
	 */
	public ItemModSeed(BlockModPlant plant, String information) {
		super(plant, plant.getGrowthRequirement().getSoil() == null ? Blocks.farmland : plant.getGrowthRequirement().getSoil().getBlock());
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.information = information;
		}
		this.setCreativeTab(AgriCraftTab.agriCraftTab);

		this.seedName = "seed" + plant.getRegistryName().replaceFirst(".*:crop", "");
		
		//register seed
		RegisterHelper.registerSeed(this, plant, this.seedName);
	}

	@SideOnly(Side.CLIENT)
	public void registerItemRenderer() {
	}

	@Override
	public List<IMutation> getMutations() {
		List<IMutation> list = new ArrayList<>();
		list.add(new Mutation(new ItemStack(this), new ItemStack(Items.pumpkin_seeds), new ItemStack(Items.wheat_seeds)));
		IMutation mutation = MutationConfig.getInstance().getDefaultMutation(new ItemStack(this));
		if (mutation != null) {
			mutation.setChance(((double) tier()) / 100.0D);
			list.add(mutation);
		}
		return list;
	}

	@Override
	public BlockModPlant getPlant() {
		return (BlockModPlant) (this.getPlant(null, null).getBlock());
	}

	@Override
	public int tier() {
		return (this.getPlant()).tier;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getInformation() {
		return this.information;
	}

	@SideOnly(Side.CLIENT)
	public void setInformation(String information) {
		this.information = information;
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.getBlockState(pos).getBlock() == AgriCraftBlocks.blockCrop) {
			LogHelper.debug("Trying to plant seed " + stack.getItem().getUnlocalizedName() + " on crops");
			return EnumActionResult.SUCCESS;
		}
		if (CropPlantHandler.getGrowthRequirement(stack.getItem(), stack.getItemDamage()).isValidSoil(world, pos)) {
			BlockPos blockPosUp = pos.add(0, 1, 0);
			if (side != EnumFacing.UP) {
				return EnumActionResult.PASS;
			} else if (player.canPlayerEdit(pos, side, stack) && player.canPlayerEdit(blockPosUp, side, stack)) {
				if (world.isAirBlock(blockPosUp)) {
					world.setBlockState(blockPosUp, this.getPlant().getStateFromMeta(0), 3);
					--stack.stackSize;
					return EnumActionResult.SUCCESS;
				} else {
					return EnumActionResult.PASS;
				}
			} else {
				return EnumActionResult.PASS;
			}
		}
		return EnumActionResult.PASS;
	}
}
