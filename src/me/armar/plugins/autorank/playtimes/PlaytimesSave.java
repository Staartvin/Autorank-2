package me.armar.plugins.autorank.playtimes;

public class PlaytimesSave implements Runnable {
    
    private Playtimes playtimes;

    public PlaytimesSave(Playtimes playtimes) {
	this.playtimes = playtimes;
    }

    @Override
    public void run() {
	playtimes.save();
    }

}
