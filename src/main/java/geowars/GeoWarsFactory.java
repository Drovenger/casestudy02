package geowars;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

import static com.almasb.fxgl.dsl.FXGL.*;
import static geowars.GeoWarsType.PLAYER;

public class GeoWarsFactory implements EntityFactory {
    @Spawns("player")
    public Entity spawnPlayer(SpawnData data) {
        return entityBuilder()
                .type(PLAYER)
                .at(getAppWidth() / 2, getAppHeight() / 2)
                .viewWithBBox("Player.png")
                .build();
    }
}
