package geowars;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Main extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        var isRelease = false;

        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("FXGL Geometry Wars");
        settings.setVersion("1.1.0");
        settings.setIntroEnabled(isRelease);
        settings.setMainMenuEnabled(isRelease);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GeoWarsFactory());
        spawn("Player");
        run(()->spawn("Wanderer"), Duration.seconds(1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
