package io.github.cadiboo.entityplayersppushoutofblockshook.config;

import io.github.cadiboo.entityplayersppushoutofblockshook.mod.EntityPlayerSPPushOutOfBlocksHookDummyModContainer;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

import static io.github.cadiboo.entityplayersppushoutofblockshook.mod.EntityPlayerSPPushOutOfBlocksHookDummyModContainer.*;
import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

public final class EntityPlayerSPPushOutOfBlocksHooksConfig {

	public static final String CONFIG_VERSION = "0.0.0";
	public static final String LANG_PREFIX = MOD_ID + ".config.";

	public static final String POST_EVENTS_CATEGORY = "hooks";

	public static Configuration config;

	private static boolean enableHook = true;

	public static void load(File file) {
		config = new Configuration(file, CONFIG_VERSION);
		sync();
	}

	public static void sync() {        //general
		enableHook = config.get(
				CATEGORY_GENERAL, "enableHook", true
		).setLanguageKey(EntityPlayerSPPushOutOfBlocksHooksConfig.LANG_PREFIX + "enableHook").getBoolean();
		if (config.hasChanged()) {
			config.save();
		}
	}

	public static boolean enableHook() {
		return enableHook;
	}

}
