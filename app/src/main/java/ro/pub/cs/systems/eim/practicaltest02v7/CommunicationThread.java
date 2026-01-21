package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;
    private static boolean timeNotSurpassed = true;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "Communication Thread - Socket is null!");
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "Communication Thread - Buffered Reader / Print Writer are null!");
                return;
            }

            String timer_action = bufferedReader.readLine();
            if (timer_action == null || timer_action.isEmpty()) {
                Log.e(Constants.TAG, "Communication Thread - Error receiving parameters from client (timer action)!");
                return;
            }

            Log.i(Constants.TAG, "Communication Thread - Received: " + timer_action);

            String[] timeComponent = timer_action.split(",");

            String clientIP = socket.getInetAddress().toString();

            HashMap<String, TimerInformation> data = serverThread.getData();

            switch(timeComponent[0]) {
                case "set":
                    timeNotSurpassed = true;
                    String hour = timeComponent[1];
                    String minute = timeComponent[2];

                    serverThread.setData(clientIP, new TimerInformation(hour, minute));

                    Log.i(Constants.TAG, "Communication Thread - Set: " + hour + " " + minute);
                    break;
                case "poll":
                    if (data.containsKey(clientIP)) {
                        if (timeNotSurpassed) {
                            TimerInformation timerInformation = data.get(clientIP);

                            // Local timer information
                            String timerHour = timerInformation.getHour();
                            String timerMinute = timerInformation.getMinute();

                            // Server timer information
                            String utcTime = getTime();
                            String utcHour = utcTime.split(" ")[2].split(":")[0];
                            String utcMinute = utcTime.split(" ")[2].split(":")[1];

                            Log.i(Constants.TAG, "Current time from server is " + utcHour + ":" + utcMinute);

                            // Local timer hour and minute
                            int timerHourInt = Integer.parseInt(timerHour);
                            int timerMinuteInt = Integer.parseInt(timerMinute);

                            // Server timer hour and minute
                            int utcHourInt = Integer.parseInt(utcHour);
                            int utcMinuteInt = Integer.parseInt(utcMinute);

                            // Verify if the local timer is past the server timer
                            if ((utcHourInt == timerHourInt && utcMinuteInt > timerMinuteInt) || utcHourInt > timerHourInt) {
                                timeNotSurpassed = false;
                                printWriter.println("active\n");
                                printWriter.flush();
                            } else {
                                printWriter.println("inactive\n");
                                printWriter.flush();
                            }
                        } else {
                            printWriter.println("active\n");
                            printWriter.flush();
                        }
                    } else {
                        Log.i(Constants.TAG, "Communication Thread - None");
                        printWriter.println("none\n");
                        printWriter.flush();
                    }
                    break;
                case "reset":
                    timeNotSurpassed = true;
                    data.remove(clientIP);
                    break;
                default:
                    Log.e(Constants.TAG, "Communication Thread - Invalid action!");
                    break;
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "Communication Thread - An exception has occurred: " + ioException.getMessage());
            ioException.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "Communication Thread - An exception has occurred: " + ioException.getMessage());
                    ioException.printStackTrace();
                }
            }
        }

    }

    private String getTime() {
        String utcTime = null;

        try {
            Socket socket = new Socket("time-a-g.nist.gov", 13);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            bufferedReader.readLine();
            utcTime = bufferedReader.readLine();
            Log.i(Constants.TAG, "utcTime: " + utcTime);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, ioException.getMessage());
            ioException.printStackTrace();
        }

        return utcTime;
    }
}
