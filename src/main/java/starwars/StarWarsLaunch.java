package starwars;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

public class StarWarsLaunch extends GameApplication {

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1280);
        gameSettings.setHeight(720);
        gameSettings.setTitle("Star Wars");
        gameSettings.setVersion("0.0.1");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
