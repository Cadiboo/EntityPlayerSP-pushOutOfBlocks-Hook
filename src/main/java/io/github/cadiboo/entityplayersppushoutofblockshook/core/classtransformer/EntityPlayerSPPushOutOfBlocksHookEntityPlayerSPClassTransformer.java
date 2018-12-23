package io.github.cadiboo.entityplayersppushoutofblockshook.core.classtransformer;

import com.google.common.base.Preconditions;
import io.github.cadiboo.entityplayersppushoutofblockshook.core.util.IStacks;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.cadiboo.entityplayersppushoutofblockshook.core.util.ObfuscationHelper.ObfuscationClass.RENDER_CHUNK;
import static io.github.cadiboo.entityplayersppushoutofblockshook.core.util.ObfuscationHelper.ObfuscationMethod.RENDER_CHUNK_REBUILD_CHUNK;
import static io.github.cadiboo.entityplayersppushoutofblockshook.core.util.ObfuscationHelper.ObfuscationClass;

/**
 * @author Cadiboo
 * @see <a href="http://www.egtry.com/java/bytecode/asm/tree_transform">http://www.egtry.com/java/bytecode/asm/tree_transform</a>
 */
// useful links:
// https://text-compare.com
// http://www.minecraftforge.net/forum/topic/32600-1710-strange-error-with-custom-event-amp-event-handler/?do=findComment&comment=172480
public abstract class EntityPlayerSPPushOutOfBlocksHookEntityPlayerSPClassTransformer implements IClassTransformer, Opcodes, IStacks {

	private static final int CLASS_WRITER_FLAGS = ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
	// skip class reader reading frames if the class writer is going to compute them for us (if it is you should get a warning that this being 0 is dead code)
	private static final int CLASS_READER_FLAGS = (CLASS_WRITER_FLAGS & ClassWriter.COMPUTE_FRAMES) == ClassWriter.COMPUTE_FRAMES ? ClassReader.SKIP_FRAMES : 0;

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Logger LOGGER_MINIFED = LogManager.getLogger("EPSPPOOBH ClassTransformer");

	public static boolean DEBUG_EVERYTHING = false;

	public static boolean DEBUG_DUMP_BYTECODE = false;
	public static String DEBUG_DUMP_BYTECODE_DIR = null;

	public static boolean DEBUG_CLASSES = false;
	public static boolean DEBUG_FIELDS = false;
	public static boolean DEBUG_TYPES = false;
	public static boolean DEBUG_STACKS = false;
	public static boolean DEBUG_METHODS = false;
	public static boolean DEBUG_INSTRUCTIONS = false;

	private static final boolean INJECT_RebuildChunkPreEvent = true;
	private static final boolean INJECT_RebuildChunkBlockRenderInLayerEvent = true;
	private static final boolean INJECT_RebuildChunkBlockRenderInTypeEvent = true;
	private static final boolean INJECT_RebuildChunkBlockEvent = true;
	private static final boolean INJECT_RebuildChunkPostEvent = true;
	private static final Printer PRINTER = new Textifier();
	private static final TraceMethodVisitor TRACE_METHOD_VISITOR = new TraceMethodVisitor(PRINTER);
	static {
		if (DEBUG_STACKS) {
			for (final Field field : IStacks.class.getFields()) {
				Object value;
				try {
					value = field.get(IStacks.class);

					LOGGER.info(field.getName() + ": " + value);

				} catch (IllegalArgumentException | IllegalAccessException e) {
					LOGGER_MINIFED.error("Error logging stacks!", e);
				}
			}
		}

	}

	public static String insnToString(final AbstractInsnNode insn) {
		insn.accept(TRACE_METHOD_VISITOR);
		final StringWriter sw = new StringWriter();
		PRINTER.print(new PrintWriter(sw));
		PRINTER.getText().clear();
		return sw.toString().trim();
	}

	public static String fieldToString(final FieldNode field) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		field.accept(new TraceClassVisitor(pw));
		PRINTER.print(pw);
		PRINTER.getText().clear();
		return sw.toString().trim();
	}

	@Override
	public byte[] transform(final String unTransformedName, final String transformedName, final byte[] basicClass) {

		if (DEBUG_CLASSES) {
			if ((unTransformedName.startsWith("b") || unTransformedName.startsWith("net.minecraft.client.renderer.chunk.")) || (transformedName.startsWith("b") || transformedName.startsWith("net.minecraft.client.renderer.chunk."))) {
				LOGGER.info("unTransformedName: " + unTransformedName + ", transformedName: " + transformedName + ", unTransformedName equals: " + unTransformedName.equals(RENDER_CHUNK.getClassName()) + ", transformedName equals: " + transformedName.equals(ObfuscationClass.RENDER_CHUNK.getClassName()));
			}
		}

		if (!transformedName.equals(RENDER_CHUNK.getClassName())) {
			return basicClass;
		}

		if (DEBUG_DUMP_BYTECODE) {
			try {
				Preconditions.checkNotNull(DEBUG_DUMP_BYTECODE_DIR, "debug dump bytecode dir before");
				final Path pathToFile = Paths.get(DEBUG_DUMP_BYTECODE_DIR + "before_hook.txt");
				pathToFile.toFile().getParentFile().mkdirs();
				final PrintWriter filePrinter = new PrintWriter(pathToFile.toFile());
				final ClassReader reader = new ClassReader(basicClass);
				final TraceClassVisitor tracingVisitor = new TraceClassVisitor(filePrinter);
				reader.accept(tracingVisitor, 0);

				final Path pathToClass = Paths.get(DEBUG_DUMP_BYTECODE_DIR + "before_hook.class");
				pathToClass.toFile().getParentFile().mkdirs();
				final FileOutputStream fileOutputStream = new FileOutputStream(pathToClass.toFile());
				fileOutputStream.write(basicClass);
				fileOutputStream.close();
			} catch (final Exception e) {
				LOGGER_MINIFED.error("Failed to dump bytecode of classes before injecting hook!", e);
			}
		}

		LOGGER.info("Preparing to inject hook into \"" + transformedName + "\" (RenderChunk)");

		// Build classNode & get instruction list
		final ClassNode classNode = new ClassNode();
		final ClassReader cr = new ClassReader(basicClass);
		cr.accept(classNode, CLASS_READER_FLAGS);

		if (DEBUG_FIELDS) {
			for (final FieldNode field : classNode.fields) {
				LOGGER.info(fieldToString(field));
			}
		}

		if (DEBUG_TYPES) {
			LOGGER.info("RebuildChunk type: " + RENDER_CHUNK_REBUILD_CHUNK.getType());
			LOGGER.info("RebuildChunk descriptor: " + RENDER_CHUNK_REBUILD_CHUNK.getDescriptor());
		}

		for (final MethodNode method : classNode.methods) {            //TODO RENDER_CHUNK_REBUILD_CHUNK.matches()

			if (!method.desc.equals(RENDER_CHUNK_REBUILD_CHUNK.getDescriptor())) {
				if (DEBUG_METHODS) {
					LOGGER.info("Method with name \"" + method.name + "\" and description \"" + method.desc + "\" did not match");
				}
				continue;
			}

			if (DEBUG_METHODS) {
				LOGGER.info("Method with name \"" + method.name + "\" and description \"" + method.desc + "\" matched!");
			}

			// make sure not to overwrite resortTransparency (it has the same description but it's name is "a" while rebuildChunk's name is "b")
			if (method.name.equals("a") || method.name.equals("func_178570_a") || method.name.equals("resortTransparency")) {
				if (DEBUG_METHODS) {
					LOGGER.info("Method with name \"" + method.name + "\" and description \"" + method.desc + "\" was rejected");
				}
				continue;
			}

			if (DEBUG_METHODS) {
				LOGGER.info("Method with name \"" + method.name + "\" and description \"" + method.desc + "\" matched and passed");
			}

			this.injectHooks(method.instructions);

		}

		// write classNode
		try {
			final ClassWriter out = new ClassWriter(CLASS_WRITER_FLAGS);

			// make the ClassWriter visit all the code in classNode
			classNode.accept(out);

			LOGGER.info("Injected hook successfully!");

			if (DEBUG_DUMP_BYTECODE) {
				try {
					Preconditions.checkNotNull(DEBUG_DUMP_BYTECODE_DIR, "debug dump bytecode dir after");
					final byte[] bytes = out.toByteArray();

					final Path pathToFile = Paths.get(DEBUG_DUMP_BYTECODE_DIR + "after_hook.txt");
					pathToFile.toFile().getParentFile().mkdirs();
					final PrintWriter filePrinter = new PrintWriter(pathToFile.toFile());
					final ClassReader reader = new ClassReader(bytes);
					final TraceClassVisitor tracingVisitor = new TraceClassVisitor(filePrinter);
					reader.accept(tracingVisitor, 0);

					final Path pathToClass = Paths.get(DEBUG_DUMP_BYTECODE_DIR + "after_hook.class");
					pathToClass.toFile().getParentFile().mkdirs();
					final FileOutputStream fileOutputStream = new FileOutputStream(pathToClass.toFile());
					fileOutputStream.write(bytes);
					fileOutputStream.close();
				} catch (final Exception e) {
					LOGGER_MINIFED.error("Failed to dump bytecode of classes after injecting hook!", e);
				}
			}

			return out.toByteArray();
		} catch (final Exception e) {
			LOGGER_MINIFED.error("FAILED to inject hook!!! Discarding changes.", e);
			LOGGER.warn("Any mods that depend on the hook provided by this mod will not work");
			return basicClass;
		}

	}

	public void injectHooks(InsnList instructions) {
		if (INJECT_RebuildChunkPreEvent) {
			LOGGER.info("injecting Hook...");
			if (DEBUG_INSTRUCTIONS) {
				for (int i = 0; i < instructions.size(); i++) {
					LOGGER_MINIFED.info(insnToString(instructions.get(i)));
				}
			}
			if (this.injectHook(instructions)) {
				LOGGER.info("injected Hook");
			} else {
				LOGGER.error("failed to inject Hook");
			}
			if (DEBUG_INSTRUCTIONS) {
				for (int i = 0; i < instructions.size(); i++) {
					LOGGER_MINIFED.info(insnToString(instructions.get(i)));
				}
			}
		}

	}

	public abstract boolean injectHook(InsnList instructions);

}
