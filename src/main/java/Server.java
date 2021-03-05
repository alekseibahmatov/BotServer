import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ArrayList<MuleThread> muleThreads = new ArrayList<>();
    private ArrayList<WorkerThread> workerThreads = new ArrayList<>();

    public Server() {

        Socket clientSocket = null;

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(7878);
            System.out.println("Server started");
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    BufferedWriter dos = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    String request = reader.readLine();

                    String splittedRequested[] = request.split(";");

                    if (splittedRequested[0].equals("Con")) {
                        if (splittedRequested[1].equals("0")) {
                            WorkerThread t = new WorkerThread(clientSocket, splittedRequested[2], workerThreads, muleThreads, this);

                            new Thread(t).start();

                            workerThreads.add(t);

                            dos.write("Connected");
                            dos.newLine();
                            dos.flush();
                        }

                        else if (splittedRequested[1].equals("1")) {
                          System.out.println(request);
                            MuleThread t = new MuleThread(clientSocket, splittedRequested[2], splittedRequested[3], this);

                            t.start();

                            muleThreads.add(t);

                            dos.write("Connected");
                          dos.newLine();
                          dos.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeMule(MuleThread muleThread) {
        muleThreads.remove(muleThread);
    }

    public void removeWorker(WorkerThread workerThread) {
        workerThreads.remove(workerThread);
    }
}
