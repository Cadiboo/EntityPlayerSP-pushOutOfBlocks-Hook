package io.github.cadiboo.entityplayersppushoutofblockshook.core.util;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;

import static io.github.cadiboo.entityplayersppushoutofblockshook.core.EntityPlayerSPPushOutOfBlocksHookLoadingPlugin.OBFUSCATION_LEVEL;
import static io.github.cadiboo.entityplayersppushoutofblockshook.core.util.ObfuscationHelper.ObfuscationClass.ENTITY_PLAYER_SP;
import static org.objectweb.asm.Type.BOOLEAN_TYPE;
import static org.objectweb.asm.Type.DOUBLE_TYPE;

public class ObfuscationHelper {

	public enum ObfuscationLevel {
		DEOBFUSCATED,
		SRG,
		OBFUSCATED;

	}

	public enum ObfuscationClass {
		ENTITY_PLAYER_SP("net/minecraft/client/entity/EntityPlayerSP", "net/minecraft/client/entity/EntityPlayerSP", "bub"),

		;

		private final String deobfuscatedName;
		private final String srgName;
		private final String obfuscatedName;

		ObfuscationClass(String deobfuscatedName, String srgName, String obfuscatedName) {
			this.deobfuscatedName = deobfuscatedName;
			this.srgName = srgName;
			this.obfuscatedName = obfuscatedName;

		}

		/**
		 * gets the internal name for the ObfuscationClass based on the current environment
		 *
		 * @return the correct internal name for the current environment
		 */
		public String getInternalName() {
			switch (OBFUSCATION_LEVEL) {
				case DEOBFUSCATED:
					return this.deobfuscatedName;
				default: //1.12.2
				case SRG:
					return this.srgName;
				// default: //1.13
				case OBFUSCATED:
					return this.obfuscatedName;
			}

		}

		/**
		 * gets the name for the ObfuscationClass based on the current environment
		 *
		 * @return the correct name for the current environment
		 */
		public String getClassName() {
			return Type.getObjectType(this.getInternalName()).getClassName();

		}
	}

	public enum ObfuscationField {        // instance fields

		;

		private final ObfuscationClass owner;
		private final String deobfuscatedName;
		private final String srgName;
		private final String obfuscatedName;
		private final Object type;

		ObfuscationField(ObfuscationClass owner, String deobfuscatedName, String srgName, String obfuscatedName, Object type) {
			this.owner = owner;
			this.deobfuscatedName = deobfuscatedName;
			this.srgName = srgName;
			this.obfuscatedName = obfuscatedName;
			this.type = type;

		}

		public ObfuscationClass getOwner() {
			return owner;

		}

		/**
		 * gets the name based on the current environment
		 *
		 * @return the correct name for the current environment
		 */
		public String getName() {
			switch (OBFUSCATION_LEVEL) {
				case DEOBFUSCATED:
					return this.deobfuscatedName;
				default: //1.12.2
				case SRG:
					return this.srgName;
				// default: //1.13
				case OBFUSCATED:
					return this.obfuscatedName;
			}

		}

		public String getDescriptor() {
			final Type type;
			if (this.type instanceof ObfuscationClass) {
				type = Type.getObjectType(((ObfuscationClass) this.type).getInternalName());
			} else if (this.type instanceof Type) {
				type = (Type) this.type;
			} else {
				throw new RuntimeException("Illegal Field Type!");
			}

			return type.getDescriptor();

		}

		public boolean matches(FieldInsnNode field) {
			if (!field.owner.equals(this.getOwner().getInternalName())) {
				return false;
			}

			if (!field.name.equals(this.getName())) {
				return false;
			}

			if (!field.desc.equals(this.getDescriptor())) {
				return false;
			}

			return true;

		}

	}

	public enum ObfuscationMethod {
		PUSH_OUT_OF_BLOCKS(ENTITY_PLAYER_SP, "pushOutOfBlocks", "func_145771_j", "i", BOOLEAN_TYPE, new Object[]{
				DOUBLE_TYPE, DOUBLE_TYPE, DOUBLE_TYPE
		}, false),

		;

		private final ObfuscationClass owner;
		private final String deobfuscatedName;
		private final String srgName;
		private final String obfuscatedName;
		private final Object returnType;
		private final Object[] params;
		private final boolean isInterface;

		ObfuscationMethod(ObfuscationClass owner, String deobfuscatedName, String srgName, String obfuscatedName, Object returnType, Object[] params, boolean isInterface) {
			this.owner = owner;
			this.deobfuscatedName = deobfuscatedName;
			this.srgName = srgName;
			this.obfuscatedName = obfuscatedName;
			this.returnType = returnType;
			this.params = params;
			this.isInterface = isInterface;

		}

		public ObfuscationClass getOwner() {
			return owner;

		}

		/**
		 * gets the name based on the current environment
		 *
		 * @return the correct name for the current environment
		 */
		public String getName() {
			switch (OBFUSCATION_LEVEL) {
				case DEOBFUSCATED:
					return this.deobfuscatedName;
				default: //1.12.2
				case SRG:
					return this.srgName;
				// default: //1.13
				case OBFUSCATED:
					return this.obfuscatedName;
			}

		}

		public String getDescriptor() {
			final Type returnType;
			if (this.returnType instanceof ObfuscationClass) {
				returnType = Type.getObjectType(((ObfuscationClass) this.returnType).getInternalName());
			} else if (this.returnType instanceof Type) {
				returnType = (Type) this.returnType;
			} else {
				throw new RuntimeException("Illegal Return Type!");
			}

			final ArrayList<Type> params = new ArrayList<>();

			for (Object paramObject : this.params) {
				final Type param;
				if (paramObject instanceof ObfuscationClass) {
					param = Type.getObjectType(((ObfuscationClass) paramObject).getInternalName());
				} else if (paramObject instanceof Type) {
					param = (Type) paramObject;
				} else {
					throw new RuntimeException("Illegal Parameter!");
				}

				params.add(param);

			}

			return Type.getMethodDescriptor(returnType, params.toArray(new Type[0]));

		}

		public Type getType() {
			return Type.getMethodType(this.getDescriptor());

		}

		public boolean isInterface() {
			return isInterface;

		}

		public boolean matches(MethodInsnNode method) {
			if (!method.owner.equals(this.getOwner().getInternalName())) {
				return false;
			}

			if (!method.name.equals(this.getName())) {
				return false;
			}

			if (!method.desc.equals(this.getDescriptor())) {
				return false;
			}

			if (!method.itf == this.isInterface()) {
				return false;
			}

			return true;

		}

	}

}
