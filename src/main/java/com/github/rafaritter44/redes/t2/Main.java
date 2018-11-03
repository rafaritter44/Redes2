package com.github.rafaritter44.redes.t2;

import com.github.rafaritter44.redes.t2.client.Client;
import com.github.rafaritter44.redes.t2.exception.InvalidConfigurationException;
import com.github.rafaritter44.redes.t2.model.Configuration;
import com.github.rafaritter44.redes.t2.server.Server;

/*
 * Classe principal
 */
public class Main {

	/*
	 * Método principal do programa, que o executa com a configuração
	 * contida no arquivo cujo caminho é informado por parâmetro,
	 * parando o programa e imprimindo a exceção caso seja uma
	 * configuração inválida; ou rodando o cliente e o servidor
	 * paralelamente caso a configuração seja válida
	 */
	public static void main(String args[]) {
		Configuration config;
		try {
			config = new Configuration(args[0]);
		} catch(InvalidConfigurationException exception) {
			exception.printStackTrace();
			return;
		}
		new Thread(Server.getInstance().configure(config)).start();
		new Thread(Client.getInstance()).start();
	}

}
