package dawid.serverregistrar.messages;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servers {
	private Collection<ServerData> servers;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ServerData {
		private String address;
		private String name;
		private int port;
		private int connectionId;
	}
}
