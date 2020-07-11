package geowars.component.enemy;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class WandererComponent extends Component {

    private int screenWidth, screenHeight;

    private double angleAdjustRate = FXGLMath.random(0, 0.15);

    private Vec2 velocity = new Vec2();
    private double directionAngle = FXGLMath.toDegrees(FXGLMath.random(-1, 1) * FXGLMath.PI2);

    private int moveSpeed;
    private int rotationSpeed = FXGLMath.random(-100, 100);

    private float tx = FXGLMath.random(1000, 10000);

    private Texture texture;
    private Texture saturatedTexture;

    private LocalTimer timer;

    private Entity wanderer;

    public WandererComponent(int moveSpeed, Texture texture, Texture overlay) {
        screenWidth = FXGL.getAppWidth();
        screenHeight = FXGL.getAppHeight();
        this.moveSpeed = moveSpeed;
        this.texture = texture;
        saturatedTexture = overlay.toColor(Color.YELLOW);
    }

    @Override
    public void onAdded() {
        wanderer = entity;
        timer = FXGL.newLocalTimer();
    }

    @Override
    public void onUpdate(double tpf) {
        adjustAngle(tpf);
        move(tpf);

        entity.setRotation(entity.getRotation() * 0.9 + directionAngle * 0.1);


        rotate(tpf);

        tx += tpf;

        checkScreenBounds();

        if (timer.elapsed(Duration.seconds(1))) {
            swapTextures();
            timer.capture();
        }
    }

    private void adjustAngle(double tpf) {
        if (FXGLMath.randomBoolean(angleAdjustRate)) {
            // never rotate further than 15 degrees
            directionAngle += Math.min(FXGLMath.toDegrees((FXGLMath.noise1D(tx) - 0.5)), 15);
        }
    }

    private void move(double tpf) {
        Vec2 directionVector = Vec2.fromAngle(directionAngle).mulLocal(moveSpeed);

        velocity.addLocal(directionVector).mulLocal((float) tpf);

        wanderer.translate(new Point2D(velocity.x, velocity.y));
    }

    private void checkScreenBounds() {
        if (wanderer.getX() < 0
                || wanderer.getY() < 0
                || wanderer.getRightX() >= screenWidth
                || wanderer.getBottomY() >= screenHeight) {

            Point2D newDirectionVector = new Point2D(screenWidth / 2, screenHeight / 2)
                    .subtract(wanderer.getCenter());

            double angle = Math.toDegrees(Math.atan(newDirectionVector.getY() / newDirectionVector.getX()));
            directionAngle = newDirectionVector.getX() > 0 ? angle : 180 + angle;
        }
    }

    private void swapTextures() {
        if (saturatedTexture.getScene() == null) {
            entity.getViewComponent().addChild(saturatedTexture);
        } else {
            entity.getViewComponent().removeChild(saturatedTexture);
        }
    }

    private void rotate(double tpf) {
        wanderer.rotateBy(rotationSpeed * tpf);
    }
}
