package dawid.serverregistrar;

import java.io.IOException;

import lombok.Data;
import lombok.SneakyThrows;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import dawid.serverregistrar.messages.RegisterMessage;
import dawid.serverregistrar.messages.RequestConnection;
import dawid.serverregistrar.utils.RegisterClasses;

@Data
public class GameServer {
	private final int port;
	private final String name;

	@SneakyThrows
	public GameServer(int port, String name) {
		this.port = port;
		this.name = name;

		Server server = new Server();
		server.bind(port);
		server.start();
		server.addListener(new Listener(){
			@Override
			public void received(Connection connection, Object object) {
				System.out.println(name + " recieved " + object + " from " + connection + "!");
			}
		});

		Client client = new Client();
		RegisterClasses.registerServerClasses(clazz -> client.getKryo().register(clazz));
		client.start();
		client.connect(100, ServerRegistrar.REGISTRAR_ADRESS, ServerRegistrar.REGISTRAR_PORT_SERVER);
		client.sendTCP(new RegisterMessage(port, name));
		client.addListener(new Listener() {
			@SneakyThrows
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof RequestConnection) {
					RequestConnection request = (RequestConnection) object;
					Client tmpClient = new Client();
					tmpClient.start();
					try {
						tmpClient.connect(100, request.getAdress(), request.getPort());
					}
					catch (IOException e) {
						//expected
					}
					finally {
						tmpClient.stop();
						tmpClient.dispose();
					}
				}
			}
		});
	}

	public static void main(String[] args) {
		new GameServer(7000, "first");
	}
}
