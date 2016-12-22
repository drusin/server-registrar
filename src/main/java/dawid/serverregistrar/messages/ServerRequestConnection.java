package dawid.serverregistrar.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerRequestConnection {
	private String address;
	private int port;
}
