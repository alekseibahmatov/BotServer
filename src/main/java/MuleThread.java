import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


// Con;1;Ivan;Available

public class MuleThread extends Thread {
    private String nickname, status, workerNickname;

    private BufferedReader dis;
    public DataOutputStream dos;
    private Socket s;

    private Server server;

    private volatile boolean run = true;

    public MuleThread(Socket s, String nickname, String status, Server server) {
        try {
            this.s = s;
            this.dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.dos = new DataOutputStream(s.getOutputStream());
            this.nickname = nickname;
            this.status = status;
            this.server = server;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String request, response;
        System.out.println("Thread initialized");
        try {
            while (run) {
                if((request = dis.readLine()) != null) {
                    String splittedRequest[] = request.split(";");

                    switch (splittedRequest[0]) {
                        case "UpdateStatus":
                            System.out.println(nickname + " is updating its status to " + splittedRequest[1]);
                            status = splittedRequest[1];
                            break;
                        case "exit":
                            close();
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getStatus() {
        return status;
    }

    private void close() {
        run = false;
        System.out.println("Mule exit");
        server.removeMule(this);
    }

//    private void writeToMule(String muleNickname, String workerNickname) throws IOException {
//        for (MuleThread mT : mTs) {
//            if(nickname.equals(muleNickname)) {
//                String response = String.format("Info;%s;%s;%s", muleNickname, workerNickname, WORLDS[0]);
//                dos.writeUTF(response);
//            }
//        }
//    }
}
