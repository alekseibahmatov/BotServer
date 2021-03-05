import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

// Con;0;Abraham

public class WorkerThread implements Runnable {

    private String nickname, status;

    private BufferedReader dis;
    private BufferedWriter dos;
    private Socket s = null;

    private ArrayList<WorkerThread> wTs;
    private ArrayList<MuleThread> mTs;

    private Server server;

    private volatile boolean run = true;

    public WorkerThread(Socket s, String nickname, ArrayList<WorkerThread> workerThreads, ArrayList<MuleThread> muleThreads, Server server) {
        try {
            this.s = s;
            this.dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.dos = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            this.nickname = nickname;
            this.wTs = workerThreads;
            this.mTs = muleThreads;
            this.server = server;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        String request, response;

        System.out.println(nickname + " thread initialized");

        try {
            while (run) {
                if((request = dis.readLine()) != null) {
                    String splittedRequest[] = request.split(";");

                    switch (splittedRequest[0]) {
                        case "RequestMules":
                            System.out.println(nickname + " is requesting mules");
                            ArrayList<String> nicknames = new ArrayList<>();
                            for (MuleThread mule : mTs) {
                                if(mule.getStatus().equals("Available")) {
                                    nicknames.add(mule.getNickname());
                                }
                            }

                            String availableMules = String.join(";", nicknames);

                            if(nicknames.size() != 0) {
                                System.out.println("Mules available: " + availableMules);
                                dos.write(availableMules);
                                dos.newLine();
                                dos.flush();
                            }
                            else {
                                System.out.println("No mules available right now");
                                dos.write("NMA");
                              dos.newLine();
                              dos.flush();
                            }
                            break;
                        case "ReserveMule":
                            System.out.println(nickname + " is reserving " + splittedRequest[1]);
                            if(writeToMule(splittedRequest[1], splittedRequest[2], splittedRequest[3])) {
                              dos.write("Received");
                              dos.newLine();
                              dos.flush();
                            }
                            break;
                        case "exit":
                           close();
                           break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Jopa");
            e.printStackTrace();
        } finally {
            try {
                dos.close();
                dis.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean writeToMule(String muleNickname, String world, String place) throws IOException {
        for (MuleThread mT : mTs) {
            if(muleNickname.equals(mT.getNickname())) {
                String response = String.format("Info;%s;%s;%s", nickname, world, place);
                mT.dos.write(response);
                mT.dos.newLine();
                mT.dos.flush();
                return true;
            }
        }

        return false;
    }

    private void close() {
        run = false;
        System.out.println("Exiting");
        server.removeWorker(this);
    }

    public void setmTs(ArrayList<MuleThread> mTs) {
        this.mTs = mTs;
    }
}
