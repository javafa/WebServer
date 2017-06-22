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
			// Ư�� port �� ������ ��� ������ ����
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		System.out.println("Server is running...");
		try {
			// ������ ����thread�� Ŭ���̾�Ʈ���� ���Ḹ ����ϰ�,
			// ���� Task �� ����thread���� ó���Ǳ� ������ ���ÿ� ���� ��û�� ó���� �� �ְԵȴ�.
			while(true){
				Socket client = serverSocket.accept(); // ������ �ϼ��ɶ� ���� ���ٿ��� �����
				processClient(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processClient(Socket client){
		// �½�ũ�� ���� thread ���� �����ؼ� ���������� ��� ������ �� �ֵ��� ���ش�.
		new Thread(){
			public void run(){
				
				InputStream is = null;
				OutputStream os = null;
				try { 
					// ����� Socket ���� ��û�� �޴� Stream�� ��� ����غ� �Ѵ�.
					is = client.getInputStream();
					os = client.getOutputStream();
							
					// 1. ��ûó��
					BufferedReader br = new BufferedReader(new InputStreamReader(is)); 
					String line = "";

					// �����Ͱ� ���������� ���پ� �о ����Ѵ�
					while(!(line= br.readLine()).startsWith("Accept-Language")){
						System.out.println(line);
					}

					// 2. ����ó��
					String message = "Response Completed!!!";
					// ���
					os.write("HTTP/1.0 200 OK \r\n".getBytes());
					os.write("Content-Type: text/html \r\n".getBytes());
					os.write(("Content-Length: " + message.getBytes().length + " \r\n").getBytes());
					// ����� �ٵ��� ������
					os.write("\r\n".getBytes());
					// ���� �ٵ� �޽���
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

				// 3. ��ûó���� �Ϸ�Ǹ� ���� ������ �����Ѵ�.
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
