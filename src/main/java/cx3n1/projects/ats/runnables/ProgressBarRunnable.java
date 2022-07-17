package cx3n1.projects.ats.runnables;

import cx3n1.projects.ats.ATSWatchman;

import java.time.LocalTime;

public class ProgressBarRunnable implements Runnable{


    @Override
    public void run() {
        long hourOfShutdown = ATSWatchman.LOADED_PRESET.getTimeOfShutdown().getHour();
        long minuteOfShutdown = ATSWatchman.LOADED_PRESET.getTimeOfShutdown().getMinute();
        long secondOfShutdown = ATSWatchman.LOADED_PRESET.getTimeOfShutdown().getSecond();

        long hoursLeft, minutesLeft, secondsLeft;
        while (!ATSWatchman.THREADS_SHUTDOWN_COMMAND){
            hoursLeft = hourOfShutdown - LocalTime.now().getHour();
            minutesLeft = minuteOfShutdown - LocalTime.now().getMinute();
            secondsLeft = secondOfShutdown - LocalTime.now().getSecond();

            //TODO: Send this info to gui thread to update progress bar
        }
    }
}
