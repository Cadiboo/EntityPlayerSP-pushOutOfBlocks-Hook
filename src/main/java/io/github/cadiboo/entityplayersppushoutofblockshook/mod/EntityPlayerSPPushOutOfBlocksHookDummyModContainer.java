package io.github.cadiboo.entityplayersppushoutofblockshook.mod;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.github.cadiboo.entityplayersppushoutofblockshook.config.EntityPlayerSPPushOutOfBlocksHooksConfig;
import joptsimple.internal.Strings;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static io.github.cadiboo.entityplayersppushoutofblockshook.core.EntityPlayerSPPushOutOfBlocksHookLoadingPlugin.MOD_LOCATION;
import static io.github.cadiboo.entityplayersppushoutofblockshook.core.EntityPlayerSPPushOutOfBlocksHookLoadingPlugin.OBFUSCATION_LEVEL;
import static io.github.cadiboo.entityplayersppushoutofblockshook.core.util.ObfuscationHelper.ObfuscationLevel.DEOBFUSCATED;

public final class EntityPlayerSPPushOutOfBlocksHookDummyModContainer extends DummyModContainer {

	public static final String MOD_ID = "render_chunk_rebuild_chunk_hooks";
	public static final String MOD_NAME = "EntityPlayerSP pushOutOfBlocks Hook";
	public static final String MOD_VERSION = "@VERSION@";
	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	static {
		if (MOD_ID.length() > 64) {
			final IllegalStateException exception = new IllegalStateException("Mod Id is too long!");
			CrashReport crashReport = new CrashReport("Mod Id must be 64 characters or shorter!", exception);
			crashReport.makeCategory("Constructing Mod");
		}
	}

	public EntityPlayerSPPushOutOfBlocksHookDummyModContainer() {
		super(new ModMetadata());

		final ArrayList<String> description = new ArrayList<>();
		description.add("A small(ish) coremod for 1.12.2 to inject hooks into RenderChunk#rebuildChunk to allow modders to add their own custom chunk rendering logic and other chunk rendering related modifications.");
		description.add("This mod provides configurable events that Modders can use for various chunk/world-related rendering logic");
		description.add(" - The RebuildChunkPreEvent is called before any chunk rebuilding is done");
		description.add("    - RebuildChunkPreOptifineEvent is the same as the RebuildChunkPreEvent but allows access to Optifine-related objects");
		description.add(" - The RebuildChunkBlockRenderInLayerEvent allows modders to modify the BlockRenderLayers that blocks can render in");
		description.add("    - RebuildChunkBlockRenderInLayerOptifineEvent is the same as the RebuildChunkBlockRenderInLayerEvent but allows access to Optifine-related objects");
		description.add(" - The RebuildChunkBlockRenderInTypeEvent allows modders to modify the EnumBlockRenderType that blocks can render in");
		description.add("    - RebuildChunkBlockRenderInTypeOptifineEvent is the same as the RebuildChunkBlockRenderInTypeEvent but allows access to Optifine-related objects");
		description.add(" - The RebuildChunkBlockEvent is called for each BlockRenderLayers of each block and allows Modders to add their own logic");
		description.add("    - RebuildChunkBlockOptifineEvent is the same as the RebuildChunkBlockEvent but allows access to Optifine-related objects");
		description.add(" - The RebuildChunkPostEvent is called after all chunk rebuilding logic is done");
		description.add("    - RebuildChunkPostOptifineEvent is the same as the RebuildChunkPostEvent but allows access to Optifine-related objects");

		final ModMetadata meta = this.getMetadata();
		meta.modId = MOD_ID;
		meta.name = MOD_NAME;
		meta.version = MOD_VERSION;
		meta.credits = "The Forge and FML guys for Forge and FML and Cadiboo for making the mod";
		meta.authorList = Arrays.asList("Cadiboo", "CosmicDan");
		meta.description = Strings.join(description, "\n");
		meta.url = "https://cadiboo.github.io/projects/" + MOD_ID;
		meta.updateJSON = "https://github.com/Cadiboo/RenderChunk-rebuildChunk-Hooks/update.json";
		meta.screenshots = new String[0];
		meta.logoFile = "/" + MOD_ID + "_logo.png";
	}

	@Subscribe
	public void preInit(final FMLPreInitializationEvent event) {
		if (!event.getSide().isClient()) {
			return;
		}

		EntityPlayerSPPushOutOfBlocksHooksConfig.load(event.getSuggestedConfigurationFile());

		MinecraftForge.EVENT_BUS.register(new EntityPlayerSPPushOutOfBlocksHooksEventSubscriber());

	}

	//will always be null in dev environment, will never be null in release environment
	@Override
	public File getSource() {
		return MOD_LOCATION;
	}

	@Override
	public boolean registerBus(final EventBus bus, final LoadController controller) {
		bus.register(this);
		return true;
	}

	// load our lang file
	@Override
	public Class<?> getCustomResourcePackClass() {
		// without this it crashes in dev, even though it works perfectly in release environment
		if (OBFUSCATION_LEVEL == DEOBFUSCATED)
			return super.getCustomResourcePackClass();
		return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
	}

	@Override
	public String getGuiClassName() {
		return EntityPlayerSPPushOutOfBlocksHooksGuiFactory.class.getName();
	}

	@Override
	public boolean shouldLoadInEnvironment() {
		return FMLCommonHandler.instance().getSide().isClient();
	}

}
