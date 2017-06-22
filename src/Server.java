import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	ServerSocket serverSocket;
	
	public Server(int port){
		try {
			// 특정 port 로 소켓을 열어서 서버를 생성
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		System.out.println("Server is running...");
		try {
			// 서버의 메인thread는 클라이언트와의 연결만 담당하고,
			// 실제 Task 는 서버thread에서 처리되기 때문에 동시에 많은 요청을 처리할 수 있게된다.
			while(true){
				Socket client = serverSocket.accept(); // 연결이 완성될때 까지 이줄에서 멈춘다
				processClient(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processClient(Socket client){
		// 태스크를 서브 thread 에서 실행해서 서버소켓이 계속 동작할 수 있도록 해준다.
		new Thread(){
			public void run(){
				
				InputStream is = null;
				OutputStream os = null;
				try { 
					// 연결된 Socket 에서 요청을 받는 Stream을 열어서 통신준비를 한다.
					is = client.getInputStream();
					os = client.getOutputStream();
							
					// 1. 요청처리
					BufferedReader br = new BufferedReader(new InputStreamReader(is)); 
					String line = "";

					// 데이터가 없을때까지 한줄씩 읽어서 출력한다
					while(!(line= br.readLine()).startsWith("Accept-Language")){
						System.out.println(line);
					}

					// 2. 응답처리
					String message = "Response Completed!!!";
					// 헤더
					os.write("HTTP/1.0 200 OK \r\n".getBytes());
					os.write("Content-Type: text/html \r\n".getBytes());
					os.write(("Content-Length: " + message.getBytes().length + " \r\n").getBytes());
					// 헤더와 바디의 구분줄
					os.write("\r\n".getBytes());
					// 실제 바디 메시지
					os.write(message.getBytes());
					os.flush();
					
					System.out.println("response done!");
					
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					try {
						os.close();
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}

				// 3. 요청처리가 완료되면 연결 소켓을 해제한다.
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
