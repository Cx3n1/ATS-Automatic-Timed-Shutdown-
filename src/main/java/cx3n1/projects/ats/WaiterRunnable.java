package cx3n1.projects.ats;

import java.io.IOException;
import java.time.LocalTime;

public class WaiterRunnable implements Runnable {
    @Override
    public void run() {
        if (LocalTime.now().isBefore(ATSWatchman.LOADED_PRESET.getTimeOfShutdown())) {
            waitTillShutdownTime();
            executeShutdownAfterWarning();
        }
    }

    private void waitTillShutdownTime() {
        try {
            long millisecondsBetweenNowAndShutdownTime = (ATSWatchman.LOADED_PRESET.getTimeOfShutdown().toSecondOfDay() - LocalTime.now().toSecondOfDay()) * 1000L;
            System.out.println(millisecondsBetweenNowAndShutdownTime - ATSWatchman.LOADED_PRESET.getWarningTime() * 1000L - 1000);
            Thread.sleep(millisecondsBetweenNowAndShutdownTime - ATSWatchman.LOADED_PRESET.getWarningTime()* 1000L - 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeShutdownAfterWarning() {
        try {
            ATSLogic.shutDownAfterGivenMinutes(ATSWatchman.LOADED_PRESET.getWarningTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
