package com.ratesp.mod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class RatRenderer {

    private static final int COLOR = RatESP.COLOR;
    private static final int RADAR_SIZE = 120;
    private static final int RADAR_RANGE = 64; // blocks shown on radar

    public static void render(DrawContext context, float tickDelta, MinecraftClient client) {
        List<Entity> rats = getRats(client);
        if (rats.isEmpty()) return;

        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        int cx = sw / 2;
        int cy = sh / 2;

        for (Entity rat : rats) {
            Vec3d pos = rat.getLerpedPos(tickDelta);

            // Project 3D position to 2D screen
            int[] screen = worldToScreen(pos, client, sw, sh);

            if (RatESP.showTracers && screen != null) {
                drawLine(context, cx, cy, screen[0], screen[1], COLOR);
            }

            if (RatESP.showBox && screen != null) {
                drawBox(context, rat, client, tickDelta, sw, sh);
            }

            if (RatESP.showName) {
                String label = "§6Rat §7[" + (int) client.player.distanceTo(rat) + "m]";
                if (screen != null) {
                    context.drawCenteredTextWithShadow(
                        client.textRenderer,
                        label,
                        screen[0],
                        screen[1] - 10,
                        COLOR
                    );
                }
            }
        }

        if (RatESP.showRadar) {
            drawRadar(context, client, rats, tickDelta, sw, sh);
        }
    }

    private static List<Entity> getRats(MinecraftClient client) {
        List<Entity> list = new ArrayList<>();
        for (Entity e : client.world.getEntities()) {
            if (RatESP.isRat(e)) list.add(e);
        }
        return list;
    }

    // Simple line drawing using filled rectangles along the path
    private static void drawLine(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) return;
        float xStep = dx / (float) steps;
        float yStep = dy / (float) steps;
        for (int i = 0; i <= steps; i += 2) {
            int px = (int) (x1 + xStep * i);
            int py = (int) (y1 + yStep * i);
            ctx.fill(px, py, px + 1, py + 1, color);
        }
    }

    private static void drawBox(DrawContext ctx, Entity entity, MinecraftClient client, float tickDelta, int sw, int sh) {
        Box box = entity.getBoundingBox();
        Vec3d[] corners = {
            new Vec3d(box.minX, box.minY, box.minZ),
            new Vec3d(box.maxX, box.minY, box.minZ),
            new Vec3d(box.maxX, box.minY, box.maxZ),
            new Vec3d(box.minX, box.minY, box.maxZ),
            new Vec3d(box.minX, box.maxY, box.minZ),
            new Vec3d(box.maxX, box.maxY, box.minZ),
            new Vec3d(box.maxX, box.maxY, box.maxZ),
            new Vec3d(box.minX, box.maxY, box.maxZ),
        };

        int[][] pts = new int[8][];
        for (int i = 0; i < 8; i++) {
            pts[i] = worldToScreen(corners[i], client, sw, sh);
            if (pts[i] == null) return;
        }

        // Bottom face
        drawLine(ctx, pts[0][0], pts[0][1], pts[1][0], pts[1][1], COLOR);
        drawLine(ctx, pts[1][0], pts[1][1], pts[2][0], pts[2][1], COLOR);
        drawLine(ctx, pts[2][0], pts[2][1], pts[3][0], pts[3][1], COLOR);
        drawLine(ctx, pts[3][0], pts[3][1], pts[0][0], pts[0][1], COLOR);
        // Top face
        drawLine(ctx, pts[4][0], pts[4][1], pts[5][0], pts[5][1], COLOR);
        drawLine(ctx, pts[5][0], pts[5][1], pts[6][0], pts[6][1], COLOR);
        drawLine(ctx, pts[6][0], pts[6][1], pts[7][0], pts[7][1], COLOR);
        drawLine(ctx, pts[7][0], pts[7][1], pts[4][0], pts[4][1], COLOR);
        // Vertical edges
        drawLine(ctx, pts[0][0], pts[0][1], pts[4][0], pts[4][1], COLOR);
        drawLine(ctx, pts[1][0], pts[1][1], pts[5][0], pts[5][1], COLOR);
        drawLine(ctx, pts[2][0], pts[2][1], pts[6][0], pts[6][1], COLOR);
        drawLine(ctx, pts[3][0], pts[3][1], pts[7][0], pts[7][1], COLOR);
    }

    private static void drawRadar(DrawContext ctx, MinecraftClient client, List<Entity> rats, float tickDelta, int sw, int sh) {
        int rx = sw - RADAR_SIZE - 10;
        int ry = 10;
        int rs = RADAR_SIZE;

        // Background
        ctx.fill(rx, ry, rx + rs, ry + rs, 0xAA000000);
        // Border
        drawLineRect(ctx, rx, ry, rx + rs, ry + rs, 0xFFFFBF00);

        // Label
        ctx.drawCenteredTextWithShadow(client.textRenderer, "§6Rat Radar", rx + rs / 2, ry + 2, COLOR);

        float yaw = client.player.getYaw();
        Vec3d playerPos = client.player.getPos();

        for (Entity rat : rats) {
            Vec3d rPos = rat.getLerpedPos(tickDelta);
            double dx = rPos.x - playerPos.x;
            double dz = rPos.z - playerPos.z;

            // Rotate by player yaw
            double rad = Math.toRadians(yaw);
            double rotX = dx * Math.cos(rad) - dz * Math.sin(rad);
            double rotZ = dx * Math.sin(rad) + dz * Math.cos(rad);

            // Scale to radar
            float scale = (rs / 2f) / RADAR_RANGE;
            int dotX = rx + rs / 2 + (int) (rotX * scale);
            int dotZ = ry + rs / 2 + (int) (rotZ * scale);

            // Clamp to radar bounds
            dotX = Math.max(rx + 2, Math.min(rx + rs - 4, dotX));
            dotZ = Math.max(ry + 2, Math.min(ry + rs - 4, dotZ));

            ctx.fill(dotX, dotZ, dotX + 4, dotZ + 4, COLOR);
        }

        // Player dot in center
        ctx.fill(rx + rs / 2 - 2, ry + rs / 2 - 2, rx + rs / 2 + 2, ry + rs / 2 + 2, 0xFFFFFFFF);
    }

    private static void drawLineRect(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        ctx.fill(x1, y1, x2, y1 + 1, color);
        ctx.fill(x1, y2 - 1, x2, y2, color);
        ctx.fill(x1, y1, x1 + 1, y2, color);
        ctx.fill(x2 - 1, y1, x2, y2, color);
    }

    public static int[] worldToScreen(Vec3d worldPos, MinecraftClient client, int sw, int sh) {
        Camera camera = client.gameRenderer.getCamera();
        Matrix4f proj = new Matrix4f(client.gameRenderer.getBasicProjectionMatrix(client.options.getFov().getValue()));
        Matrix4f view = new Matrix4f();

        Vec3d camPos = camera.getPos();
        float rx = camera.getPitch();
        float ry = camera.getYaw();

        view.identity();
        view.rotateX((float) Math.toRadians(rx));
        view.rotateY((float) Math.toRadians(ry + 180));
        view.translate(
            (float) -(worldPos.x - camPos.x),
            (float) -(worldPos.y - camPos.y),
            (float) -(worldPos.z - camPos.z)
        );

        Vector4f clipCoords = new Vector4f(0, 0, 0, 1);
        view.transform(clipCoords);
        proj.transform(clipCoords);

        if (clipCoords.w <= 0) return null; // Behind camera

        float ndcX = clipCoords.x / clipCoords.w;
        float ndcY = clipCoords.y / clipCoords.w;

        int screenX = (int) ((ndcX + 1f) / 2f * sw);
        int screenY = (int) ((1f - ndcY) / 2f * sh);

        return new int[]{screenX, screenY};
    }
}
