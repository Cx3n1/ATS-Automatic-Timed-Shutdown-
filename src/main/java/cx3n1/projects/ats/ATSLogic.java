package cx3n1.projects.ats;

import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.time.LocalTime;

public class ATSLogic {

    private static final LocalTime SHUTDOWN_TIME = LocalTime.of(23, 0);

    public static void mainLogic() throws InterruptedException {
        //load current preset
        //if day is appropriate go on next else exit
        //if time is appropriate shutdown after delay else start waiting thread which will wait till time is right or preset is changed
        //call shutdown after delay
        //end


        int secondsLeftTillShutdown = SHUTDOWN_TIME.toSecondOfDay() - LocalTime.now().toSecondOfDay();

        //if less than 10 minutes are left till shutdown then return
        if(secondsLeftTillShutdown <= 600) return;

        Thread.sleep(secondsLeftTillShutdown* 1000L - 600*1000L);
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
