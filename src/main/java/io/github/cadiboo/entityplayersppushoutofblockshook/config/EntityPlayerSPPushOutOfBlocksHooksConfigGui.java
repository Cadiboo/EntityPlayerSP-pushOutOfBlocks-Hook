package io.github.cadiboo.renderchunkrebuildchunkhooks.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

import static io.github.cadiboo.entityplayersppushoutofblockshook.config.EntityPlayerSPPushOutOfBlocksHooksConfig.LANG_PREFIX;
import static io.github.cadiboo.entityplayersppushoutofblockshook.config.EntityPlayerSPPushOutOfBlocksHooksConfig.config;
import static io.github.cadiboo.entityplayersppushoutofblockshook.mod.EntityPlayerSPPushOutOfBlocksHookDummyModContainer.MOD_ID;
import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

public final class EntityPlayerSPPushOutOfBlocksHooksConfigGui extends GuiConfig {

	public EntityPlayerSPPushOutOfBlocksHooksConfigGui(final GuiScreen parentScreen) {
		super(parentScreen, getConfigElements(), MOD_ID, false, false, I18n.format(LANG_PREFIX + "title"));
	}

	private static List<IConfigElement> getConfigElements() {
		final ArrayList<IConfigElement> configElements = new ArrayList<>();
		for (String categoryName : config.getCategoryNames()) {
			final IConfigElement configElement = new ConfigElement(config.getCategory(categoryName).setLanguageKey(LANG_PREFIX + categoryName));
			// put all elements in the general category into the root category. Thanks Choonster!!!
			if (categoryName.equals(CATEGORY_GENERAL)) {
				configElements.addAll(configElement.getChildElements());
				continue;
			}
			configElements.add(configElement);
		}

		return configElements;
	}

}
