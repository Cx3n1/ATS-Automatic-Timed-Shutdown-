package cx3n1.projects.ats.runnables;

import cx3n1.projects.ats.ATSSettings;
import cx3n1.projects.ats.ATSWatchman;

import java.time.LocalTime;

//TODO: work in progress
public class ProgressBarRunnable implements Runnable{
    @Override
    public void run() {
        long hourOfShutdown = ATSSettings.getLoadedShutdownTime().getHour();
        long minuteOfShutdown = ATSSettings.getLoadedShutdownTime().getMinute();
        long secondOfShutdown = ATSSettings.getLoadedShutdownTime().getSecond();

        long hoursLeft, minutesLeft, secondsLeft;
       /* while (!ATSWatchman.THREADS_SHUTDOWN_COMMAND){
            hoursLeft = hourOfShutdown - LocalTime.now().getHour();
            minutesLeft = minuteOfShutdown - LocalTime.now().getMinute();
            secondsLeft = secondOfShutdown - LocalTime.now().getSecond();

            //TODO: Send this info to gui thread to update progress bar
        }*/
    }
}
