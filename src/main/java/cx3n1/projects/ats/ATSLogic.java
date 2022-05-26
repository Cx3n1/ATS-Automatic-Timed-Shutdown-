package cx3n1.projects.ats;

import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalUnit;

public class ATSLogic {

    public static void mainLogic() throws Exception {
        Preset currentPreset = ATSWatchman.LOADED_PRESET;

        if(!currentPreset.checkIfTodayIsSelectedDay(LocalDate.now().getDayOfWeek()))
            return;

        if(LocalTime.now().plusMinutes(currentPreset.getWarningTime()).isBefore(currentPreset.getTimeOfShutdown())){
            ATSWatchman.WAITER_THREAD = new Thread(new WaiterRunnable());
            ATSWatchman.WAITER_THREAD.start();
        } else {
            shutDownAfterGivenMinutes(currentPreset.getWarningTime());
        }
    }

    public static void shutDownAfterGivenMinutes(int minutes) throws IOException {
        String shutdownCommand = null;

        if(SystemUtils.IS_OS_AIX)
            shutdownCommand = "shutdown -Fh " + minutes;
        else if(SystemUtils.IS_OS_FREE_BSD || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC|| SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_NET_BSD || SystemUtils.IS_OS_OPEN_BSD || SystemUtils.IS_OS_UNIX)
            shutdownCommand = "shutdown -h " + minutes;
        else if(SystemUtils.IS_OS_HP_UX)
            shutdownCommand = "shutdown -hy " + minutes;
        else if(SystemUtils.IS_OS_IRIX)
            shutdownCommand = "shutdown -y -g " + minutes;
        else if(SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS)
            shutdownCommand = "shutdown -y -i5 -g" + minutes;
        else if(SystemUtils.IS_OS_WINDOWS)
            shutdownCommand = "shutdown.exe /s /t " + minutes*60; //Windows takes seconds as delay
        else
            return;

        Runtime.getRuntime().exec(shutdownCommand);
    }
}
