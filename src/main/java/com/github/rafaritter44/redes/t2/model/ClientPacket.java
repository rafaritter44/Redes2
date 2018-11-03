package com.github.rafaritter44.redes.t2.model;

import com.github.rafaritter44.redes.t2.constant.Constants;
import com.github.rafaritter44.redes.t2.exception.InvalidPacketException;
import com.github.rafaritter44.redes.t2.server.Server;

/*
 * Classe que representa a entrada do cliente ao enviar um pacote
 */
public class ClientPacket {
	
	private String input;
	
	/*
	 * Construtor, que chama o método validador com a entrada do cliente informada
	 */
	public ClientPacket(String input) throws InvalidPacketException {
		validatePacket(input);
	}
	
	/*
	 * Método que retorna a entrada do cliente
	 */
	public String getInput() { return input; }
	
	/*
	 * Método que adiciona o início padrão de um pacote de dados
	 * ("2345;naocopiado:<apelido_da_origem>:") à entrada do cliente
	 * e retorna todo esse conteúdo do pacote pronto
	 */
	public String getContent() {
		return Constants.DATA_PACKET_ID + ";" + Constants.NOT_COPIED + ":" +
				Server.getInstance().getConfig().getNickname() + ":" + input;
	}
	
	/*
	 * Método que valida a entrada do cliente e inicializa o pacote caso seja válida;
	 * Caso contrário, lança uma exceção informando o problema que ocorreu
	 */
	private void validatePacket(String input) throws InvalidPacketException {
		if(!validFields(input.split(":")))
			throw new InvalidPacketException("This packet is invalid:\n" + input + "\nCorrect format:\n" + Constants.CLIENT_PACKET_FORMAT);
		this.input = input;
	}
	
	/*
	 * Método que verifica se os campos informados estão de acordo com o formato esperado
	 */
	private boolean validFields(String[] fields) {
		return fields.length == 3 && validDataType(fields[1]);
	}
	
	/*
	 * Método que verifica se o tipo de dado informado é válido
	 * ("A" para arquivo, ou "M" para mensagem)
	 */
	private boolean validDataType(String dataType) {
		return Constants.FILE_ID.equals(dataType) ||
				Constants.MESSAGE_ID.equals(dataType);
	}

}
