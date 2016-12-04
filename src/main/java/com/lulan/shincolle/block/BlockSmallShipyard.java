package com.lulan.shincolle.block;

import java.util.Random;

import com.lulan.shincolle.client.render.block.RenderSmallShipyard;
import com.lulan.shincolle.tileentity.TileEntitySmallShipyard;
import com.lulan.shincolle.utility.CalcHelper;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSmallShipyard extends BasicBlockFacingContainer
{

	public static final String NAME = "BlockSmallShipyard";
	public static final String TILENAME = "TileEntitySmallShipyard";
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	private static final double[] smoke1 = new double[] {0.72, 1.1, 0.55};	//主煙囪 粒子位置
	private static final double[] smoke2 = new double[] {0.22, 0.8, 0.7};	//中煙囪 粒子位置
	private static final double[] smoke3 = new double[] {0.47, 0.6, 0.25};	//小煙囪 粒子位置
	
	
	public BlockSmallShipyard()
	{
	    super();
		this.setUnlocalizedName(NAME);
		this.setRegistryName(NAME);
		this.setHardness(10F);
	    this.setHarvestLevel("pickaxe", 3);
	    this.setLightLevel(4);
	    
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), this.getRegistryName());
        GameRegistry.registerTileEntity(TileEntitySmallShipyard.class, TILENAME);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntitySmallShipyard();
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel()
	{
		super.initModel();
		
		//prevent property mapping to blockstate
		ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(new IProperty[] { FACING, ACTIVE }).build());
		
        //register tile entity render
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySmallShipyard.class, new RenderSmallShipyard());
    
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
	
	@Override
    protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { FACING, ACTIVE });
	}
	
    /** meta轉state, 注意meta值只有0~15 */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
    	//all direction: 3 bits for FACING, 1 bit for ACTIVE
        return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7))
        						.withProperty(ACTIVE, (meta & 8) > 0);
    }

    /** state轉meta, 注意meta值只有0~15 */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        //bits: 0:facing 1:facing 2:facing 3:active
    	//ex: meta = 2  = north(2) + inactive(0)
    	//    meta = 11 = south(3) + active(8)
    	return state.getValue(FACING).getIndex() + (state.getValue(ACTIVE) ? 8 : 0);
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
        return false;
    }
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
	//spawn particle: largesmoke, posX, posY, posZ, motionX, motionY, motionZ
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		//取得旋轉後的新位置
		double[] smokeR1 = new double[3];
		double[] smokeR2 = new double[3];
		double[] smokeR3 = new double[3];
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		
		smokeR1 = CalcHelper.rotateParticleByFace(smoke1[0], smoke1[1], smoke1[2], state.getValue(FACING).getIndex(), 1);
		smokeR2 = CalcHelper.rotateParticleByFace(smoke2[0], smoke2[1], smoke2[2], state.getValue(FACING).getIndex(), 1);
		smokeR3 = CalcHelper.rotateParticleByFace(smoke3[0], smoke3[1], smoke3[2], state.getValue(FACING).getIndex(), 1);
		
		//if active -> spawn smoke
		if (state.getValue(ACTIVE))
		{
			//使三根煙囪分開冒煙
			switch (rand.nextInt(3))
			{
			case 0:
				//主煙囪特效
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR1[0], y+smokeR1[1], z+smokeR1[2], 0.0D, 0D, 0.0D, new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR1[0], y+smokeR1[1]+0.1D, z+smokeR1[2], 0.0D, 0.005D, 0.0D, new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR1[0], y+smokeR1[1]+0.2D, z+smokeR1[2], 0.0D, 0.01D, 0.0D, new int[0]);
				//小煙囪特效
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR3[0], y+smokeR3[1], z+smokeR3[2], 0.0D, 0D, 0.0D, new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR3[0], y+smokeR3[1]+0.1D, z+smokeR3[2], 0.0D, 0.01D, 0.0D, new int[0]);
				break;
			case 1:
				//主煙囪特效
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR1[0], y+smokeR1[1], z+smokeR1[2], 0.0D, 0D, 0.0D, new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR1[0], y+smokeR1[1]+0.1D, z+smokeR1[2], 0.0D, 0.005D, 0.0D, new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR1[0], y+smokeR1[1]+0.2D, z+smokeR1[2], 0.0D, 0.01D, 0.0D, new int[0]);
				//中煙囪特效
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR2[0], y+smokeR2[1], z+smokeR2[2], 0.0D, 0D, 0.0D, new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR2[0], y+smokeR2[1]+0.1D, z+smokeR2[2], 0.0D, 0.01D, 0.0D, new int[0]);
				break;
			case 2:
				//中煙囪特效
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR2[0], y+smokeR2[1], z+smokeR2[2], 0.0D, 0D, 0.0D, new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR2[0], y+smokeR2[1]+0.1D, z+smokeR2[2], 0.0D, 0.01D, 0.0D, new int[0]);
				//小煙囪特效
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR3[0], y+smokeR3[1], z+smokeR3[2], 0.0D, 0D, 0.0D, new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x+smokeR3[0], y+smokeR3[1]+0.1D, z+smokeR3[2], 0.0D, 0.01D, 0.0D, new int[0]);
				break;
			default:
				break;
			}//end switch		
		}//end if
	}

	//update block is building
	public static void updateBlockState(boolean isBuilding, World world, BlockPos pos)
	{
		//check block exists
		IBlockState state = world.getBlockState(pos);
		
		if (state != null && state.getBlock() instanceof BlockSmallShipyard)
		{
			//set state
			world.setBlockState(pos, world.getBlockState(pos).withProperty(ACTIVE, isBuilding), 2);
		}
		
	}
	

}
