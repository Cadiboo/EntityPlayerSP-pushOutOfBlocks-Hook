package io.github.cadiboo.entityplayersppushoutofblockshook.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public final class EntityPlayerSPPushOutOfBlocksHooksGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(final Minecraft minecraftInstance) {
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	@Nullable
	public GuiScreen createConfigGui(final GuiScreen parentScreen) {
		return new io.github.cadiboo.renderchunkrebuildchunkhooks.config.EntityPlayerSPPushOutOfBlocksHooksConfigGui(parentScreen);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return new HashSet<RuntimeOptionCategoryElement>();
	}

}
