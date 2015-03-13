package com.lulan.shincolle.network;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.lulan.shincolle.entity.BasicEntityShip;
import com.lulan.shincolle.proxy.ClientProxy;
import com.lulan.shincolle.reference.ID;
import com.lulan.shincolle.utility.EntityHelper;
import com.lulan.shincolle.utility.LogHelper;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**SERVER TO CLIENT : ENTITY SYNC PACKET
 * �Ω�entity����ƦP�B
 * packet handler�P�˫إߦb��class��
 * 
 * tut by diesieben07: http://www.minecraftforge.net/forum/index.php/topic,20135.0.html
 */
public class S2CEntitySync implements IMessage {
	
	private BasicEntityShip entity;
	private int entityID;
	private int type;

	
	public S2CEntitySync() {}	//�����n���ŰѼ�constructor, forge�~��ϥΦ�class
	
	//entity sync: 
	//type 0: all attribute
	//type 1: entity state only
	//type 2: entity flag only
	public S2CEntitySync(BasicEntityShip entity, int type) {
        this.entity = entity;
        this.type = type;
    }
	
	//����packet��k (CLIENT SIDE)
	@Override
	public void fromBytes(ByteBuf buf) {
		//get type and entityID
		this.type = buf.readByte();
		this.entityID = buf.readInt();
		this.entity = (BasicEntityShip) EntityHelper.getEntityByID(entityID, 0, true);

		if(entity != null) {
			switch(type) {
			case 0:	//sync all attr
				{
					entity.setStateMinor(ID.N.ShipLevel, buf.readInt());
					entity.setStateMinor(ID.N.Kills, buf.readInt());
					entity.setStateMinor(ID.N.ExpCurrent, buf.readInt());
					entity.setStateMinor(ID.N.NumAmmoLight, buf.readInt());
					entity.setStateMinor(ID.N.NumAmmoHeavy, buf.readInt());
					entity.setStateMinor(ID.N.NumGrudge, buf.readInt());
					entity.setStateMinor(ID.N.NumAirLight, buf.readInt());
					entity.setStateMinor(ID.N.NumAirHeavy, buf.readInt());
					
					entity.setStateFinal(ID.HP, buf.readFloat());
					entity.setStateFinal(ID.ATK, buf.readFloat());
					entity.setStateFinal(ID.DEF, buf.readFloat());
					entity.setStateFinal(ID.SPD, buf.readFloat());
					entity.setStateFinal(ID.MOV, buf.readFloat());
					entity.setStateFinal(ID.HIT, buf.readFloat());
					entity.setStateFinal(ID.ATK_H, buf.readFloat());
					entity.setStateFinal(ID.ATK_AL, buf.readFloat());
					entity.setStateFinal(ID.ATK_AH, buf.readFloat());
					
					entity.setStateEmotion(ID.S.State, buf.readByte(), false);
					entity.setStateEmotion(ID.S.Emotion, buf.readByte(), false);
					entity.setStateEmotion(ID.S.Emotion2, buf.readByte(), false);

					entity.setBonusPoint(ID.HP, buf.readByte());
					entity.setBonusPoint(ID.ATK, buf.readByte());
					entity.setBonusPoint(ID.DEF, buf.readByte());
					entity.setBonusPoint(ID.SPD, buf.readByte());
					entity.setBonusPoint(ID.MOV, buf.readByte());
					entity.setBonusPoint(ID.HIT, buf.readByte());

					entity.setStateFlag(ID.F.CanFloatUp, buf.readBoolean());
					entity.setStateFlag(ID.F.IsMarried, buf.readBoolean());
					entity.setStateFlag(ID.F.NoFuel, buf.readBoolean());
					entity.setStateFlag(ID.F.UseMelee, buf.readBoolean());
					entity.setStateFlag(ID.F.UseAmmoLight, buf.readBoolean());
					entity.setStateFlag(ID.F.UseAmmoHeavy, buf.readBoolean());
					entity.setStateFlag(ID.F.UseAirLight, buf.readBoolean());
					entity.setStateFlag(ID.F.UseAirHeavy, buf.readBoolean());
					
					entity.setEffectEquip(ID.EF_CRI, buf.readFloat());
					entity.setEffectEquip(ID.EF_DHIT, buf.readFloat());
					entity.setEffectEquip(ID.EF_THIT, buf.readFloat());
					entity.setEffectEquip(ID.EF_MISS, buf.readFloat());
				}
				break;
			case 1: //entity state only
				{
					entity.setStateEmotion(ID.S.State, buf.readByte(), false);
					entity.setStateEmotion(ID.S.Emotion, buf.readByte(), false);
					entity.setStateEmotion(ID.S.Emotion2, buf.readByte(), false);
				}
				break;
			case 2: //entity flag only
				{
					entity.setStateFlag(ID.F.CanFloatUp, buf.readBoolean());
					entity.setStateFlag(ID.F.IsMarried, buf.readBoolean());
					entity.setStateFlag(ID.F.NoFuel, buf.readBoolean());
					entity.setStateFlag(ID.F.UseMelee, buf.readBoolean());
					entity.setStateFlag(ID.F.UseAmmoLight, buf.readBoolean());
					entity.setStateFlag(ID.F.UseAmmoHeavy, buf.readBoolean());
					entity.setStateFlag(ID.F.UseAirLight, buf.readBoolean());
					entity.setStateFlag(ID.F.UseAirHeavy, buf.readBoolean());
				}
				break;
			}
		}
		else {
			LogHelper.info("DEBUG : packet handler: S2CEntitySync entity is null");
		}
	}

	//�o�Xpacket��k
	@Override
	public void toBytes(ByteBuf buf) {
		switch(this.type) {
		case 0:	//sync all data
			{
				buf.writeByte(0);	//type 0
				buf.writeInt(this.entity.getEntityId());
				buf.writeInt(this.entity.getStateMinor(ID.N.ShipLevel));
				buf.writeInt(this.entity.getStateMinor(ID.N.Kills));
				buf.writeInt(this.entity.getStateMinor(ID.N.ExpCurrent));
				buf.writeInt(this.entity.getStateMinor(ID.N.NumAmmoLight));
				buf.writeInt(this.entity.getStateMinor(ID.N.NumAmmoHeavy));
				buf.writeInt(this.entity.getStateMinor(ID.N.NumGrudge));
				buf.writeInt(this.entity.getStateMinor(ID.N.NumAirLight));
				buf.writeInt(this.entity.getStateMinor(ID.N.NumAirHeavy));

				buf.writeFloat(this.entity.getStateFinal(ID.HP));
				buf.writeFloat(this.entity.getStateFinal(ID.ATK));
				buf.writeFloat(this.entity.getStateFinal(ID.DEF));
				buf.writeFloat(this.entity.getStateFinal(ID.SPD));
				buf.writeFloat(this.entity.getStateFinal(ID.MOV));
				buf.writeFloat(this.entity.getStateFinal(ID.HIT));
				buf.writeFloat(this.entity.getStateFinal(ID.ATK_H));
				buf.writeFloat(this.entity.getStateFinal(ID.ATK_AL));
				buf.writeFloat(this.entity.getStateFinal(ID.ATK_AH));
				
				buf.writeByte(this.entity.getStateEmotion(ID.S.State));
				buf.writeByte(this.entity.getStateEmotion(ID.S.Emotion));
				buf.writeByte(this.entity.getStateEmotion(ID.S.Emotion2));

				buf.writeByte(this.entity.getBonusPoint(ID.HP));
				buf.writeByte(this.entity.getBonusPoint(ID.ATK));
				buf.writeByte(this.entity.getBonusPoint(ID.DEF));
				buf.writeByte(this.entity.getBonusPoint(ID.SPD));
				buf.writeByte(this.entity.getBonusPoint(ID.MOV));
				buf.writeByte(this.entity.getBonusPoint(ID.HIT));

				buf.writeBoolean(this.entity.getStateFlag(ID.F.CanFloatUp));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.IsMarried));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.NoFuel));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.UseMelee));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.UseAmmoLight));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.UseAmmoHeavy));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.UseAirLight));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.UseAirHeavy));
				
				buf.writeFloat(this.entity.getEffectEquip(ID.EF_CRI));
				buf.writeFloat(this.entity.getEffectEquip(ID.EF_DHIT));
				buf.writeFloat(this.entity.getEffectEquip(ID.EF_THIT));
				buf.writeFloat(this.entity.getEffectEquip(ID.EF_MISS));
			}
			break;
		case 1:	//entity state only
			{
				buf.writeByte(1);	//type 1
				buf.writeInt(this.entity.getEntityId());
				buf.writeByte(this.entity.getStateEmotion(ID.S.State));
				buf.writeByte(this.entity.getStateEmotion(ID.S.Emotion));
				buf.writeByte(this.entity.getStateEmotion(ID.S.Emotion2));
			}
			break;
		case 2:	//entity flag only
			{
				buf.writeByte(2);	//type 2
				buf.writeInt(this.entity.getEntityId());
				buf.writeBoolean(this.entity.getStateFlag(ID.F.CanFloatUp));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.IsMarried));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.NoFuel));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.UseMelee));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.UseAmmoLight));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.UseAmmoHeavy));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.UseAirLight));
				buf.writeBoolean(this.entity.getStateFlag(ID.F.UseAirHeavy));	
			}
			break;
		}
	}
	
	//packet handler (inner class)
	public static class Handler implements IMessageHandler<S2CEntitySync, IMessage> {
		//����ʥ]�����debug�T��
		@Override
		public IMessage onMessage(S2CEntitySync message, MessageContext ctx) {
//          System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
//			LogHelper.info("DEBUG : recv Entity Sync packet : type "+recvType+" id "+entityID);
			return null;
		}
    }

}