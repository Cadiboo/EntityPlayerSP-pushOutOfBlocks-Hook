package io.github.cadiboo.entityplayersppushoutofblockshook.core;

import io.github.cadiboo.entityplayersppushoutofblockshook.core.classtransformer.EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer;
import io.github.cadiboo.entityplayersppushoutofblockshook.core.classtransformer.EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformerVanillaForge;
import io.github.cadiboo.entityplayersppushoutofblockshook.core.util.ObfuscationHelper;
import io.github.cadiboo.entityplayersppushoutofblockshook.event.EntityPlayerSPPushOutOfBlocksEvent;
import io.github.cadiboo.entityplayersppushoutofblockshook.mod.EntityPlayerSPPushOutOfBlocksHookDummyModContainer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

import static io.github.cadiboo.entityplayersppushoutofblockshook.mod.EntityPlayerSPPushOutOfBlocksHookDummyModContainer.MOD_ID;
import static io.github.cadiboo.entityplayersppushoutofblockshook.mod.EntityPlayerSPPushOutOfBlocksHookDummyModContainer.MOD_NAME;
import static io.github.cadiboo.entityplayersppushoutofblockshook.mod.EntityPlayerSPPushOutOfBlocksHookDummyModContainer.MOD_VERSION;

@Name(MOD_NAME)
@MCVersion("1.12.2")
@TransformerExclusions({"io.github.cadiboo.entityplayersppushoutofblockshook.core."})
/* How early your core mod is called - Use > 1000 to work with srg names */
//??? needs higher than 1001??? 0xBADC0DE works
//@SortingIndex(value = 1001)
@SortingIndex(value = 0xBAD_C0DE)
//put in _VM_ arguments -Dfml.coreMods.load=io.github.cadiboo.entityplayersppushoutofblockshook.core.EntityPlayerSPPushOutOfBlocksHookLoadingPlugin
public final class EntityPlayerSPPushOutOfBlocksHookLoadingPlugin implements IFMLLoadingPlugin {

	public static final String CORE_MARKER = MOD_ID;

	private static final Logger LOGGER = LogManager.getLogger(MOD_NAME + " Core Plugin");
	public static File MOD_LOCATION = null;

	public static ObfuscationHelper.ObfuscationLevel OBFUSCATION_LEVEL = ObfuscationHelper.ObfuscationLevel.OBFUSCATED;

	public EntityPlayerSPPushOutOfBlocksHookLoadingPlugin() {
		LOGGER.debug("Initialising " + this.getClass().getSimpleName() + " version: " + MOD_VERSION);
		Launch.blackboard.put(CORE_MARKER, MOD_VERSION);
	}

	@Override
	public String[] getASMTransformerClass() {
		detectOtherCoremods();

		return new String[]{EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformerVanillaForge.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return EntityPlayerSPPushOutOfBlocksHookDummyModContainer.class.getName();
	}

	@Override
	@Nullable
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(final Map<String, Object> data) {
		final boolean runtimeDeobfuscationEnabled = (boolean) data.get("runtimeDeobfuscationEnabled");
		final boolean developerEnvironment = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

		final boolean debugEverything = getArgsBoolean("debugEverything");
		EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer.DEBUG_EVERYTHING = debugEverything;

		EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer.DEBUG_DUMP_BYTECODE = getArgsBoolean("dumpBytecode") | debugEverything;
		EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer.DEBUG_DUMP_BYTECODE_DIR = data.get("mcLocation") + "/" + MOD_ID + "/dumps/";

		EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer.DEBUG_CLASSES = getArgsBoolean("debugClasses") | debugEverything;
		EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer.DEBUG_FIELDS = getArgsBoolean("debugFields") | debugEverything;
		EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer.DEBUG_TYPES = getArgsBoolean("debugTypes") | debugEverything;
		EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer.DEBUG_STACKS = getArgsBoolean("debugStacks") | debugEverything;
		EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer.DEBUG_METHODS = getArgsBoolean("debugMethods") | debugEverything;
		EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer.DEBUG_INSTRUCTIONS = getArgsBoolean("debugInstructions") | debugEverything;

		MOD_LOCATION = (File) data.get("coremodLocation");

		if (runtimeDeobfuscationEnabled) {
			OBFUSCATION_LEVEL = ObfuscationHelper.ObfuscationLevel.SRG;
		} else if (developerEnvironment) {
			OBFUSCATION_LEVEL = ObfuscationHelper.ObfuscationLevel.DEOBFUSCATED;
		} else {
			OBFUSCATION_LEVEL = ObfuscationHelper.ObfuscationLevel.OBFUSCATED;
		}

		LOGGER.info("Pre-loading event classes...");
		EntityPlayerSPPushOutOfBlocksEvent.class.getName();
		LOGGER.info("Successfully Pre-loaded event classes");
	}

	private boolean getArgsBoolean(final String arg) {
		final boolean result = Boolean.valueOf(System.getProperty("D" + arg)) | Boolean.valueOf(System.getProperty(arg));
		LOGGER.debug("Argument " + arg + ": " + result);
		return result;
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	private void detectOtherCoremods() {

	}

}
