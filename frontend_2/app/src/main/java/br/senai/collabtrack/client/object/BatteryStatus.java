package br.senai.collabtrack.client.object;

/**
 * Created by ezs on 07/08/2017.
 */

public class BatteryStatus {

    public int level;
    public boolean isCharging;

    public BatteryStatus(){

    }

    public BatteryStatus(final int level, final boolean isCharging){
        this.level = level;
        this.isCharging = isCharging;
    }

}
