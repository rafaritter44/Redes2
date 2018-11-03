package com.github.rafaritter44.redes.t2.model;

import java.util.Optional;

import com.github.rafaritter44.redes.t2.constant.Constants;
import com.github.rafaritter44.redes.t2.exception.InvalidPacketException;

/*
 * Classe que representa um pacote
 */
public class Packet {
	
	private String content;
	private Optional<String> errorControl;
	private Optional<String> sourceNickname;
	private Optional<String> destinationNickname;
	private Optional<String> dataType;
	private Optional<String> message;
	
	/*
	 * Construtor, que chama o método que valida o conteúdo passado por parâmetro
	 */
	public Packet(String content) throws InvalidPacketException {
		validatePacket(content);
	}
	
	/*
	 * Método que valida o conteúdo do pacote, passado por parâmetro;
	 * Caso o conteúdo esteja no formato correto, chama o método que inicializa o pacote;
	 * Caso contrário, lança uma exceção informando qual foi o problema que ocorreu
	 */
	private void validatePacket(String content) throws InvalidPacketException {
		if(content == null)
			throw new InvalidPacketException("The content of the packet cannot be null");
		if(!validPacket(content))
			throw new InvalidPacketException("This packet is invalid:\n" + content + "\nCorrect format:\n" + Constants.DATA_PACKET_FORMAT);
		this.content = content;
		if(isToken())
			initializeToken();
		else
			initializeDataPacket();
	}
	
	/*
	 * Método que inicializa um pacote de dados com o conteúdo atribuído ao atributo "content"
	 */
	private void initializeDataPacket() {
		String[] fields = fields(content);
		errorControl = Optional.ofNullable(fields[0]);
		sourceNickname = Optional.ofNullable(fields[1]);
		destinationNickname = Optional.ofNullable(fields[2]);
		dataType = Optional.ofNullable(fields[3]);
		message = Optional.ofNullable(fields[4]);
	}
	
	/*
	 * Método que inicializa um pacote do tipo "token", o qual não possui nenhum dos
	 * campos que um pacote de dados possuiria (inicializa esses atributos como "vazios")
	 */
	private void initializeToken() {
		errorControl = Optional.empty();
		sourceNickname = Optional.empty();
		destinationNickname = Optional.empty();
		dataType = Optional.empty();
		message = Optional.empty();
	}
	
	/*
	 * Método que retorna o início de um pacote de dados – "2345;"
	 */
	private String dataPacketBeginning() {
		return Constants.DATA_PACKET_ID + ";";
	}
	
	/*
	 * Método que recebe todo o conteúdo de um pacote de dados e
	 * retorna um vetor com os campos que ele possui
	 */
	private String[] fields(String content) {
		return content.substring(dataPacketBeginning().length()).split(":");
	}
	
	/*
	 * Método que verifica se um pacote é válido (token ou pacote de dados com formato válido)
	 */
	private boolean validPacket(String content) {
		if(content == null)
			return false;
		if(isToken(content))
			return true;
		if(!content.startsWith(dataPacketBeginning()) || !validFields(fields(content)))
			return false;
		return true;
	}
	
	/*
	 * Método que verifica se os campos informados por parâmetro estão no formato válido
	 */
	private boolean validFields(String[] fields) {
		return fields.length == 5 && validErrorControl(fields[0]) &&
				!Constants.BROADCAST_ID.equals(fields[1]) && validDataType(fields[3]);
	}
	
	/*
	 * Método que verifica se o campo de controle de erro informado é válido
	 * ("naocopiado", "erro" ou "OK")
	 */
	private boolean validErrorControl(String errorControl) {
		return Constants.NOT_COPIED.equals(errorControl) ||
				Constants.ERROR.equals(errorControl) ||
				Constants.OK.equals(errorControl);
	}
	
	/*
	 * Método que verifica se o tipo de dado informado é válido
	 * ("A" para arquivo, ou "M" para mensagem)
	 */
	private boolean validDataType(String dataType) {
		return Constants.FILE_ID.equals(dataType) ||
				Constants.MESSAGE_ID.equals(dataType);
	}
	
	/*
	 * Método que verifica se o conteúdo informado equivale ao do token – "1234"
	 */
	private boolean isToken(String content) {
		return Constants.TOKEN.equals(content);
	}
	
	/*
	 * Método que verifica se este pacote é um token (compara "1234" com atributo "content")
	 */
	public boolean isToken() {
		return Constants.TOKEN.equals(content);
	}
	
	/*
	 * Getters
	 */
	public String getContent() { return content; }
	public Optional<String> getErrorControl() { return errorControl; }
	public Optional<String> getSourceNickname() { return sourceNickname; }
	public Optional<String> getDestinationNickname() { return destinationNickname; }
	public Optional<String> getDataType() { return dataType; }
	public Optional<String> getMessage() { return message; }
	
	/*
	 * Método que retorna a parte do pacote equivalente ao input do cliente (aquilo que
	 * ele deve informar pelo teclado para enviar um pacote – destino, tipo de dado e
	 * mensagem), excluindo a parte inicial e padrão do pacote (controle de erro e origem)
	 */
	public String getClientInput() {
		return destinationNickname.get() + ":" + dataType.get() + ":" + message.get();
	}
	
	/*
	 * Método que atualiza e retorna esta instância de pacote com o controle de erro "OK"
	 */
	public Packet readMessage() {
		errorControl = Optional.ofNullable(Constants.OK);
		content = content.replaceFirst(Constants.NOT_COPIED, Constants.OK);
		return this;
	}
	
	/*
	 * Método que atualiza e retorna esta instância de pacote com o controle de erro "erro"
	 */
	public Packet introduceError() {
		errorControl = Optional.ofNullable(Constants.ERROR);
		content = content.replaceFirst(Constants.NOT_COPIED, Constants.ERROR);
		return this;
	}
	
	/*
	 * Método que atualiza e retorna esta instância de pacote com o controle de erro "naocopiado"
	 */
	public Packet renew() {
		errorControl = Optional.ofNullable(Constants.NOT_COPIED);
		content = content.replaceFirst(Constants.ERROR, Constants.NOT_COPIED);
		return this;
	}
	
	/*
	 * Método estático que retorna um pacote do tipo token
	 */
	public static Packet generateToken() {
		return new Packet(Constants.TOKEN);
	}
	
}
