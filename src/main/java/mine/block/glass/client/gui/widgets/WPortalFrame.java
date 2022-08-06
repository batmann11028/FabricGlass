package mine.block.glass.client.gui.widgets;

import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.CHelper;
import qouteall.imm_ptl.core.ClientWorldLoader;
import qouteall.imm_ptl.core.render.GuiPortalRendering;
import qouteall.imm_ptl.core.render.MyRenderHelper;
import qouteall.imm_ptl.core.render.context_management.WorldRenderInfo;
import qouteall.q_misc_util.my_util.DQuaternion;

import javax.annotation.Nullable;

public class WPortalFrame extends WWidget {
    private final RegistryKey<World> viewingDimension;
    public Vec3d viewingPosition;
    private final MinecraftClient client;

    private Framebuffer framebuffer;


    public WPortalFrame(RegistryKey<World> viewingDimension, Vec3d viewingPosition) {
        this.viewingDimension = viewingDimension;
        this.viewingPosition = viewingPosition.add(0, 1, 0);
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if(viewingPosition == null) return;
        if(framebuffer == null) {
            this.framebuffer = new SimpleFramebuffer(getWidth(), getHeight(), true, true);
        }

        matrices.push();

        double t1 = CHelper.getSmoothCycles(503);
        double t2 = CHelper.getSmoothCycles(197);

        // Determine the camera transformation
        Matrix4f cameraTransformation = new Matrix4f();
        cameraTransformation.loadIdentity();

        // Determine the camera position
        Vec3d cameraPosition = this.viewingPosition;

        // Create the world render info
        WorldRenderInfo worldRenderInfo = new WorldRenderInfo(
                ClientWorldLoader.getWorld(viewingDimension),// the world that it renders
                cameraPosition,// the camera position
                cameraTransformation,// the camera transformation
                true,// does not apply this transformation to the existing player camera
                null,
                client.options.getClampedViewDistance()// render distance
        );

        // Ask it to render the world into the framebuffer the next frame
        GuiPortalRendering.submitNextFrameRendering(worldRenderInfo, framebuffer);

        // Draw the framebuffer
        MyRenderHelper.drawFramebuffer(
                framebuffer,
                true,
                false,
                x,
                x + this.getWidth(),
                y + this.getHeight(),
                y
        );
    }
}
