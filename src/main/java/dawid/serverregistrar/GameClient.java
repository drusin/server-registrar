package dawid.serverregistrar;

import java.io.IOException;

import lombok.SneakyThrows;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import dawid.serverregistrar.messages.GetServerList;
import dawid.serverregistrar.messages.RequestConnection;
import dawid.serverregistrar.messages.Servers;
import dawid.serverregistrar.messages.Servers.ServerData;
import dawid.serverregistrar.utils.RegisterClasses;

public class GameClient {

	@SneakyThrows
	public GameClient() {
		Client registrarClient = new Client();
		RegisterClasses.registerClientClasses(clazz -> registrarClient.getKryo().register(clazz));
		registrarClient.start();
		registrarClient.connect(100, ServerRegistrar.REGISTRAR_ADRESS, ServerRegistrar.REGISTRAR_PORT_CLIENT);
		registrarClient.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof Servers) {
					Servers servers = (Servers) object;
					ServerData serverData = servers.getServers().stream().findFirst().get();
					registrarClient.sendTCP(new RequestConnection(serverData.getAddress(), serverData.getPort()));
					connectTo(serverData);
				}
			}
		});
		registrarClient.sendTCP(new GetServerList());
	}

	private void connectTo(ServerData serverData) {
		Client gameClient = new Client();
		gameClient.start();
		while (!gameClient.isConnected()) {
			try {
				gameClient.connect(100, serverData.getAddress(), serverData.getPort());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SneakyThrows
	public static void main(String[] args) {
		new GameClient();
		while(true) {
			Thread.sleep(100);
		}
	}
}
