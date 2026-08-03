package pt.ulisboa.tecnico.rnl.dei.dms.exceptions;

public enum ErrorMessage {

	NO_SUCH_PERSON("Não existe nenhuma pessoa com o ID %s", 1001),
	NO_SUCH_COURSE("Não existe nenhum curso com o ID %s", 1002),
	NO_SUCH_UC("Não existe nenhuma UC com o ID %s", 1003),
	PERSON_NAME_NOT_VALID("O nome da pessoa especificado não é válido.", 1004),
	PERSON_ALREADY_EXISTS("Já existe uma pessoa com o ID %s", 1005),
	INVALID_CREDENTIALS("Email ou password inválidos.", 1006),
	INVALID_REGENTE("A pessoa especificada não é um professor regente.", 1007);

	private final String label;
	private final int code;

	ErrorMessage(String label, int code) {
		this.label = label;
		this.code = code;
	}

	public String getLabel() {
		return this.label;
	}

	public int getCode() {
		return this.code;
	}
}
