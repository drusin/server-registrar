package dawid.serverregistrar.utils;

import java.util.ArrayList;
import java.util.function.Consumer;

import lombok.experimental.UtilityClass;

import dawid.serverregistrar.messages.GetServerList;
import dawid.serverregistrar.messages.RegisterMessage;
import dawid.serverregistrar.messages.Servers;

@UtilityClass
public class RegisterClasses {

	public static void registerServerClasses (Consumer<Class> registrar) {
		registrar.accept(RegisterMessage.class);
	}

	public static void registerClientClasses (Consumer<Class> registrar) {
		registrar.accept(GetServerList.class);
		registrar.accept(Servers.class);
		registrar.accept(Servers.ServerData.class);
		registrar.accept(ArrayList.class);
		registrar.accept(String.class);
	}
}
