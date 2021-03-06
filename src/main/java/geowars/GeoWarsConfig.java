package geowars;

import static com.almasb.fxgl.dsl.FXGLForKtKt.geti;

public final class GeoWarsConfig {

    private double redEnemyChance = 0.25;
    private int redEnemyHealth = geti("multiplier");
    private int redEnemyMoveSpeed = 300;
    private int wandererMaxMoveSpeed = 150;
    private int bouncerMoveSpeed = 500;
    private int seekerMaxMoveSpeed= 200;
    private int runnerMoveSpeed= 350;

    private int playerSpeed = 350;

    private int enemyHealth = 3;

    public double getRedEnemyChance() {
        return redEnemyChance;
    }

    public int getRedEnemyHealth() {
        return redEnemyHealth;
    }

    public int getRedEnemyMoveSpeed() {
        return redEnemyMoveSpeed;
    }

    public int getSeekerMaxMoveSpeed() { return seekerMaxMoveSpeed; }

    public int getWandererMaxMoveSpeed() {
        return wandererMaxMoveSpeed;
    }

    public int getRunnerMoveSpeed() { return runnerMoveSpeed; }

    public int getBouncerMoveSpeed() { return bouncerMoveSpeed; }

    public int getPlayerSpeed() {
        return playerSpeed;
    }

    public int getEnemyHealth() {
        return enemyHealth;
    }
}
