package com.github.rafaritter44.redes.t2.model;

import static java.util.stream.Collectors.toList;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import com.github.rafaritter44.redes.t2.constant.Constants;
import com.github.rafaritter44.redes.t2.exception.InvalidConfigurationException;

/*
 * Classe que representa a configuração do programa
 */
public class Configuration {
	
	private String nextIP;
	private int nextPort;
	private String nickname;
	private long sleepDuration;
	private boolean tokenGenerator;
	private InetAddressValidator validator;
	
	/*
	 * Construtor, que inicializa o validador e chama o método que valida a configuração fornecida
	 */
	public Configuration(String path) throws InvalidConfigurationException {
		validator = InetAddressValidator.getInstance();
		validateConfiguration(path);
	}
	
	/*
	 * Método que valida a configuração contida em arquivo, cujo caminho é passado por parâmetro;
	 * Caso haja algum erro no arquivo informado ou caso o formato da configuração seja inválido,
	 * é lançada uma exceção informando o ocorrido;
	 * Caso contrário (configuração correta), é chamado o método que inicializa a configuração
	 */
	private void validateConfiguration(String path) throws InvalidConfigurationException {
		List<String> lines;
		try(Stream<String> stream = Files.lines(Paths.get(path))) {
			lines = stream.collect(toList());
		} catch(Exception exception) {
			exception.printStackTrace();
			throw new InvalidConfigurationException(exception);
		}
		if(!validFormat(lines))
			throw new InvalidConfigurationException("Invalid format! Should be:\n" + Constants.CONFIGURATION_FORMAT);
		initializeConfiguration(lines);
	}
	
	/*
	 * Método que inicializa a configuração com as linhas de configuração passadas por parâmetro
	 */
	private void initializeConfiguration(List<String> lines) {
		String[] address = lines.get(0).split(":");
		nextIP = address[0];
		nextPort = Integer.parseInt(address[1]);
		nickname = lines.get(1);
		sleepDuration = Long.parseLong(lines.get(2));
		tokenGenerator = Boolean.parseBoolean(lines.get(3));
	}
	
	/*
	 * Método que verifica se as linhas de configuaração passadas por parâmetro são válidas
	 */
	private boolean validFormat(List<String> lines) {
		if(lines.size() != Constants.CONFIGURATION_LINES)
			return false;
		String[] address = lines.get(0).split(":");
		return address.length == 2 && validator.isValid(address[0]) && StringUtils.isNumeric(address[1]) &&
				!Constants.BROADCAST_ID.equals(lines.get(1)) && StringUtils.isNumeric(lines.get(2)) && isBoolean(lines.get(3));
	}
	
	/*
	 * Método que verifica se a String passada por parâmetro contém valor booleano ("true" ou "false")
	 */
	private boolean isBoolean(String value) {
		return "true".equals(value) || "false".equals(value);
	}
	
	/*
	 * Getters
	 */
	public String getNextIP() { return nextIP; }
	public int getNextPort() { return nextPort; }
	public String getNickname() { return nickname; }
	public long getSleepDuration() { return sleepDuration; }
	public boolean isTokenGenerator() { return tokenGenerator; }
	
}
