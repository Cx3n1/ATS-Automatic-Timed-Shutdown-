package cx3n1.projects.ats.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;

@NoArgsConstructor (access = AccessLevel.PRIVATE)
public class DuplicateCheck {

    private static ServerSocket LOCK_SOCKET;

    /**
     * Establishes lock which can be checked by the program at launch to determine
     * if other instance of the program is already running.
     *
     * should be launched at the start of the program and with same lock number to be effective.
     *
     * @param lock - number between 0 and 65535 inclusive, corresponds to unique port of each program.
     * @exception SecurityException - if a security manager exists and its checkListen method doesn't allow the operation.
     * @exception IllegalArgumentException - if the lock parameter is outside the specified range of valid port values, which is between 0 and 65535, inclusive.
     * @return true - if lock is established successfully, false - if lock already exists.
     */
    public static boolean launchLock(int lock){
        try{
            LOCK_SOCKET = new ServerSocket(lock);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void launchLockWithExceptions(int lock) throws Exception {
        launchLockWithExceptions(lock, "Program is already running!");
    }

    public static void launchLockWithExceptions(int lock, String exceptionMessage) throws Exception {
        try{
            LOCK_SOCKET = new ServerSocket(lock);
        } catch (IOException e) {
            throw new Exception(exceptionMessage);
        }
    }

}
