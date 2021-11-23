package com.villaton.schematicsorter.utility;

import com.villaton.schematicsorter.SchematicSorter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;

public class CPTimer {

    private static LinkedList<Timer> all_timers;

    private static class Timer implements Runnable {

        private final BukkitTask task;
        private int time;
        private final Player player;
        private final String command;

        public Timer(Player player, String command) {
            //Clearing values
            this.player = player;
            this.command = command;
            this.time = 0;
            this.task = Bukkit.getScheduler().runTaskTimer(SchematicSorter.getInstance(), this,0, 20);
        }

        @Override
        public void run() {

            //Bukkit.getLogger().info("Tick Tack");
            time++;
            if (time >= 3) {
                this.player.chat(command);
                this.task.cancel();
                remove_timer(this);
            }
        }
    }

    public static void create_timer_management() {
        all_timers = new LinkedList<>();
    }

    public static void add_timer(Player player, String command) {
        if (!(all_timers == null)) {
            all_timers.add(new Timer(player, command));
        }
    }

    private static void remove_timer(Timer timer) {
        if (!(all_timers == null) && all_timers.size() > 0) {
            all_timers.remove(timer);
        }
    }

}
