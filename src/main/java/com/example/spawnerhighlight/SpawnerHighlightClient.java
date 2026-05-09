package com.example.spawnerhighlight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class SpawnerHighlightClient implements ClientModInitializer {

    private static final int PARTICLE_INTERVAL = 10;
    private static final DustParticleEffect RED_DUST =
            new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f), 1.3f);
    private static KeyBinding menuKey;
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        SpawnerHighlightConfig.getInstance();
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spawnerhighlight.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.spawnerhighlight"
        ));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommandManager.literal("activate_finder")
                    .executes(context -> {
                        SpawnerHighlightConfig cfg = SpawnerHighlightConfig.getInstance();
                        cfg.enabled = !cfg.enabled;
                        cfg.save();
                        String status = cfg.enabled ? "§aON" : "§cOFF";
                        context.getSource().sendFeedback(
                            Text.literal("§6[SpawnerHighlight] §rHighlight is now " + status)
                        );
                        return 1;
                    })
            );
        });
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;
        while (menuKey.wasPressed()) {
            client.setScreen(new SpawnerHighlightScreen(client.currentScreen));
        }
        if (client.isPaused()) return;
        if (!SpawnerHighlightConfig.getInstance().enabled) return;
        tickCounter++;
        if (tickCounter % PARTICLE_INTERVAL != 0) return;
        ClientWorld world = client.world;
        int renderDistance = client.options.getViewDistance().getValue();
        ChunkPos playerChunk = client.player.getChunkPos();
        int pcx = playerChunk.x;
        int pcz = playerChunk.z;
        for (int cx = pcx - renderDistance; cx <= pcx + renderDistance; cx++) {
            for (int cz = pcz - renderDistance; cz <= pcz + renderDistance; cz++) {
                if (!world.isChunkLoaded(cx, cz)) continue;
                WorldChunk chunk = world.getChunk(cx, cz);
                chunk.getBlockEntities().forEach((pos, blockEntity) -> {
                    if (!(blockEntity instanceof MobSpawnerBlockEntity)) return;
                    if (!world.getBlockState(pos).isOf(Blocks.SPAWNER)) return;
                    spawnRedRing(world, pos);
                });
            }
        }
    }

    private void spawnRedRing(ClientWorld world, BlockPos pos) {
        double bx = pos.getX();
        double by = pos.getY();
        double bz = pos.getZ();
        double cx = bx + 0.5;
        double cz = bz + 0.5;
        int ringSteps = 20;
        for (int i = 0; i < ringSteps; i++) {
            double angle = (2 * Math.PI / ringSteps) * i;
            double ox = Math.cos(angle) * 0.62;
            double oz = Math.sin(angle) * 0.62;
            world.addParticle(RED_DUST, cx + ox, by + 0.03, cz + oz, 0, 0, 0);
            world.addParticle(RED_DUST, cx + ox, by + 0.97, cz + oz, 0, 0, 0);
        }
        double[] xs = {bx + 0.03, bx + 0.97, bx + 0.03, bx + 0.97};
        double[] zs = {bz + 0.03, bz + 0.03, bz + 0.97, bz + 0.97};
        for (int corner = 0; corner < 4; corner++) {
            for (double y = 0.1; y <= 0.9; y += 0.18) {
                world.addParticle(RED_DUST, xs[corner], by + y, zs[corner], 0, 0, 0);
            }
        }
    }
}
