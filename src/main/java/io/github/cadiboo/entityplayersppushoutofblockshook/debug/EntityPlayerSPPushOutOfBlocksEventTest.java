package io.github.cadiboo.renderchunkrebuildchunkhooks.debug;

import io.github.cadiboo.entityplayersppushoutofblockshook.event.EntityPlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Mod(modid = io.github.cadiboo.renderchunkrebuildchunkhooks.debug.EntityPlayerSPPushOutOfBlocksEventTest.MODID, name = "EntityPlayerSPPushOutOfBlocksEventTest", version = "1.0", acceptableRemoteVersions = "*", clientSideOnly = true)
public final class EntityPlayerSPPushOutOfBlocksEventTest {

	public static final String MODID = "_event_test";
	public static final boolean ENABLED = false;

	@SubscribeEvent
	public static void onEntityPlayerSPPushOutOfBlocksEvent(final EntityPlayerSPPushOutOfBlocksEvent event) {
		if (!ENABLED) {
			return;
		}

	}

}
