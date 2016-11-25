package dawid.serverregistrar;

import lombok.SneakyThrows;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import dawid.serverregistrar.messages.GetServerList;
import dawid.serverregistrar.messages.Servers;
import dawid.serverregistrar.messages.Servers.ServerData;
import dawid.serverregistrar.utils.RegisterClasses;

public class GameClient {

	@SneakyThrows
	public GameClient() {
		Client registrarClient = new Client();
		RegisterClasses.registerClientClasses(clazz -> registrarClient.getKryo().register(clazz));
		registrarClient.start();
		registrarClient.connect(100, "localhost", ServerRegistrar.REGISTRAR_PORT_CLIENT);
		registrarClient.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof Servers) {
					Servers servers = (Servers) object;
					System.out.println(servers);
					connectTo(servers.getServers().stream().findFirst().get());
				}
			}
		});
		registrarClient.sendTCP(new GetServerList());
	}

	@SneakyThrows
	private void connectTo(ServerData serverData) {
		Client gameClient = new Client();
		gameClient.start();
		gameClient.connect(100, serverData.getAddress(), serverData.getPort());
	}

	@SneakyThrows
	public static void main(String[] args) {
		new GameClient();
		while(true) {
			Thread.sleep(100);
		}
	}
}
