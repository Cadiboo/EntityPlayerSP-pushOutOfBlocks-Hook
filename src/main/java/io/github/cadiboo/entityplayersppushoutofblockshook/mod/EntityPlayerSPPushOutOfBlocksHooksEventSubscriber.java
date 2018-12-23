package io.github.cadiboo.entityplayersppushoutofblockshook.mod;

import io.github.cadiboo.entityplayersppushoutofblockshook.config.EntityPlayerSPPushOutOfBlocksHooksConfig;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static io.github.cadiboo.entityplayersppushoutofblockshook.mod.EntityPlayerSPPushOutOfBlocksHookDummyModContainer.MOD_ID;

@SideOnly(Side.CLIENT)
public final class EntityPlayerSPPushOutOfBlocksHooksEventSubscriber {

	/**
	 * Inject the new values and save to the config file when the config has been changed from the GUI.
	 *
	 * @param event The event
	 */
	@SubscribeEvent
	public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(MOD_ID)) {
			EntityPlayerSPPushOutOfBlocksHooksConfig.sync();
		}
	}

}
