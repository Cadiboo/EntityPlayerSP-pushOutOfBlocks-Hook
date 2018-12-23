package io.github.cadiboo.entityplayersppushoutofblockshook.event;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called when a {@link EntityPlayerSP#pushOutOfBlocks(double, double, double)} is called.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS EVENT_BUS}.
 *
 * @author Cadiboo
 * @see EntityPlayerSP#pushOutOfBlocks(double, double, double)
 */
@Cancelable
public class EntityPlayerSPPushOutOfBlocksEvent extends Event {

}
