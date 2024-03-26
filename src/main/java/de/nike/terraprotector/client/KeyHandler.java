package de.nike.terraprotector.client;

import com.mojang.blaze3d.platform.InputConstants;
import de.nike.terraprotector.TerraProtector;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Registry;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import javax.net.ssl.KeyManager;
import java.util.function.Supplier;

public class KeyHandler {

    public static KeyMapping hudConfig;

    public static void init() {
        hudConfig = new KeyMapping("key.terraprotector.hud_config", new CustomContext(KeyConflictContext.IN_GAME, () -> hudConfig), InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, TerraProtector.MODID);
    }

    private static class CustomContext implements IKeyConflictContext {
        private KeyConflictContext context;
        private Supplier<KeyMapping> binding;
        public CustomContext(KeyConflictContext context, Supplier<KeyMapping> binding) {
            this.context = context;
            this.binding = binding;
        }
        @Override
        public boolean isActive() {
            return context.isActive();
        }
        @Override
        public boolean conflicts(IKeyConflictContext other) {
            if (!(other instanceof CustomContext)) {
                return other == context;
            }

            if (((CustomContext) other).context != context) {
                return false;
            }

            KeyMapping otherBind = ((CustomContext) other).binding.get();
            return otherBind.getKey().getValue() == binding.get().getKey().getValue() && otherBind.getKeyModifier() == binding.get().getKeyModifier();
        }
    }


}
