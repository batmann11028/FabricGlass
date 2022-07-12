package mine.block.glass.client.render.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class WorldRendererProxy extends WorldRenderer {

    public static HashSet<WorldRendererProxy> renderGlobalProxies = new HashSet<>();

    public static void release(WorldRendererProxy proxy)
    {
        proxy.released = true;
        proxy.setWorld(null);
    }

    public static WorldRendererProxy requestProxy() {
        WorldRendererProxy proxy = null;
        for(WorldRendererProxy pooledProxy : renderGlobalProxies)
        {
            if(pooledProxy.released)
            {
                proxy = pooledProxy;
                proxy.released = false;
                break;
            }
        }
        if(proxy == null)
        {
            proxy = new WorldRendererProxy(MinecraftClient.getInstance());
            renderGlobalProxies.add(proxy);
        }
        proxy.setWorld(MinecraftClient.getInstance().worldRenderer.world);
        return proxy;
    }

    public boolean renderSky = true;
    public boolean released = false;

    public WorldRendererProxy(MinecraftClient client) {
        super(client, client.getEntityRenderDispatcher(), client.getBlockEntityRenderDispatcher(), client.getBufferBuilders());
    }

    @Override
    public void renderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double d, double e, double f) {
        if(renderSky) {
            super.renderClouds(matrices, projectionMatrix, tickDelta, d, e, f);
        }
    }

    @Override
    public void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable) {
        if(renderSky) {
            super.renderSky(matrices, projectionMatrix, tickDelta, camera, bl, runnable);
        }
    }

    @Override
    public void playSong(@Nullable SoundEvent song, BlockPos songPosition) {}

    @Override
    public void processWorldEvent(int eventId, BlockPos pos, int data) {
        Random random = this.world.random;

        switch(eventId) {
            case 2000:
                Direction direction = Direction.byId(data);
                float i = direction.getOffsetX();
                float j = direction.getOffsetY();
                float k = direction.getOffsetZ();
                double d = (double)pos.getX() + (double)i * 0.6 + 0.5;
                double e = (double)pos.getY() + (double)j * 0.6 + 0.5;
                double f = (double)pos.getZ() + (double)k * 0.6 + 0.5;

                for(int l = 0; l < 10; ++l) {
                    double g = random.nextDouble() * 0.2 + 0.01;
                    double h = d + (double)i * 0.01 + (random.nextDouble() - 0.5) * (double)k * 0.5;
                    double m = e + (double)j * 0.01 + (random.nextDouble() - 0.5) * (double)j * 0.5;
                    double n = f + (double)k * 0.01 + (random.nextDouble() - 0.5) * (double)i * 0.5;
                    double o = (double)i * g + random.nextGaussian() * 0.01;
                    double p = (double)j * g + random.nextGaussian() * 0.01;
                    double q = (double)k * g + random.nextGaussian() * 0.01;
                    this.addParticle(ParticleTypes.SMOKE, h, m, n, o, p, q);
                }

                return;
            case 2001:
                BlockState blockState = Block.getStateFromRawId(data);
                if (!blockState.isAir()) {
                    BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
                    this.world.playSound(pos, blockSoundGroup.getBreakSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F, false);
                }

                this.world.addBlockBreakParticles(pos, blockState);
                break;
            case 2002:
            case 2007:
                Vec3d vec3d = Vec3d.ofBottomCenter(pos);

                for(i = 0; i < 8; ++i) {
                    this.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), vec3d.x, vec3d.y, vec3d.z, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }

                float u = (float)(data >> 16 & 255) / 255.0F;
                float v = (float)(data >> 8 & 255) / 255.0F;
                float w = (float)(data >> 0 & 255) / 255.0F;
                ParticleEffect particleEffect = eventId == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

                for(int x = 0; x < 100; ++x) {
                    e = random.nextDouble() * 4.0;
                    f = random.nextDouble() * Math.PI * 2.0;
                    double y = Math.cos(f) * e;
                    double z = 0.01 + random.nextDouble() * 0.5;
                    double aa = Math.sin(f) * e;
                    Particle particle = this.spawnParticle(particleEffect, particleEffect.getType().shouldAlwaysSpawn(), vec3d.x + y * 0.1, vec3d.y + 0.3, vec3d.z + aa * 0.1, y, z, aa);
                    if (particle != null) {
                        float ab = 0.75F + random.nextFloat() * 0.25F;
                        particle.setColor(u * ab, v * ab, w * ab);
                        particle.move((float)e);
                    }
                }

                this.world.playSound(pos, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 2003:
                double r = (double)pos.getX() + 0.5;
                double s = pos.getY();
                d = (double)pos.getZ() + 0.5;

                for(int t = 0; t < 8; ++t) {
                    this.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), r, s, d, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }

                for(e = 0.0; e < 6.283185307179586; e += 0.15707963267948966) {
                    this.addParticle(ParticleTypes.PORTAL, r + Math.cos(e) * 5.0, s - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -5.0, 0.0, Math.sin(e) * -5.0);
                    this.addParticle(ParticleTypes.PORTAL, r + Math.cos(e) * 5.0, s - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -7.0, 0.0, Math.sin(e) * -7.0);
                }

                return;
            case 2004:
                for(i = 0; i < 20; ++i) {
                    s = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
                    d = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
                    e = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
                    this.world.addParticle(ParticleTypes.SMOKE, s, d, e, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.FLAME, s, d, e, 0.0, 0.0, 0.0);
                }

                return;
            case 2005:
                BoneMealItem.createParticles(this.world, pos, data);
                break;
            case 2006:
                for(int jk = 0; jk < 200; ++jk) {
                    w = random.nextFloat() * 4.0F;
                    float ac = random.nextFloat() * 6.2831855F;
                    double ak = (double)(MathHelper.cos(ac) * w);
                    double al = 0.01 + random.nextDouble() * 0.5;
                    double af = (double)(MathHelper.sin(ac) * w);
                    Particle particle2 = this.spawnParticle(ParticleTypes.DRAGON_BREATH, false, (double)pos.getX() + ak * 0.1, (double)pos.getY() + 0.3, (double)pos.getZ() + af * 0.1, ak, al, af);
                    if (particle2 != null) {
                        particle2.move(w);
                    }
                }

                if (data == 1) {
                    this.world.playSound(pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                }
                break;
            case 3000:
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.world.playSound(pos, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F, false);
                break;
            case 3001:
        }
    }
}
