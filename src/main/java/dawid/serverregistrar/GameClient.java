package dawid.serverregistrar;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import dawid.serverregistrar.messages.ClientRequestConnection;
import dawid.serverregistrar.messages.GetServerList;
import dawid.serverregistrar.messages.Servers;
import dawid.serverregistrar.messages.Servers.ServerData;
import dawid.serverregistrar.utils.RegisterClasses;

public class GameClient {

	public GameClient() throws Exception{
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
					registrarClient.sendTCP(new ClientRequestConnection(serverData.getConnectionId()));
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

	public static void main(String[] args) throws Exception{
		new GameClient();
		while(true) {
			Thread.sleep(100);
		}
	}
}
