package dawid.serverregistrar;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import dawid.serverregistrar.messages.ClientRequestConnection;
import dawid.serverregistrar.messages.GetServerList;
import dawid.serverregistrar.messages.RegisterMessage;
import dawid.serverregistrar.messages.ServerRequestConnection;
import dawid.serverregistrar.messages.Servers;
import dawid.serverregistrar.messages.Servers.ServerData;
import dawid.serverregistrar.utils.RegisterClasses;

public class ServerRegistrar {

	public static final int REGISTRAR_PORT_SERVER = 9090;
	public static final int REGISTRAR_PORT_CLIENT = 9091;

	public static final String REGISTRAR_ADRESS = "35.156.141.181";
//	public static final String REGISTRAR_ADRESS = "localhost";

	private final Map<Integer, ServerData> registeredServers = new HashMap<>();
	private Server registrarServer;
	private Server clientServer;

	public ServerRegistrar() throws IOException {
		createRegistrarServer();
		createClientServer();
	}

	private void createRegistrarServer() throws IOException {
		registrarServer = new Server();
		registrarServer.bind(REGISTRAR_PORT_SERVER);
		RegisterClasses.registerServerClasses(clazz -> registrarServer.getKryo().register(clazz));
		registrarServer.start();
		registrarServer.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof RegisterMessage) {
					RegisterMessage message = (RegisterMessage) object;
					System.out.println(connection + ": " + message);
					registeredServers.put(connection.getID(), new ServerData(connection.getRemoteAddressTCP().getAddress().getHostAddress(), message.getName(), message.getPort(), connection.getID()));
				}
			}

			@Override
			public void disconnected(Connection connection) {
				registeredServers.remove(connection.getID());
			}
		});
	}

	private void createClientServer() throws IOException {
		clientServer = new Server();
		clientServer.bind(REGISTRAR_PORT_CLIENT);
		RegisterClasses.registerClientClasses(clazz -> clientServer.getKryo().register(clazz));
		clientServer.start();
		clientServer.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof GetServerList) {
					connection.sendTCP(new Servers(registeredServers.values().stream().collect(Collectors.toList())));
				}
				if (object instanceof ClientRequestConnection) {
					ClientRequestConnection request = (ClientRequestConnection) object;
					registrarServer.sendToTCP(request.getConnectionId(), new ServerRequestConnection(connection.getRemoteAddressTCP().getAddress().getHostAddress(), connection.getRemoteAddressTCP().getPort()));
				}
			}
		});
	}

	public static void main(String[] args) throws IOException {
		new ServerRegistrar();
	}
}
