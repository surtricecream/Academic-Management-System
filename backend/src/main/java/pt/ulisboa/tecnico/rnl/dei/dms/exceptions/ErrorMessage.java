package pt.ulisboa.tecnico.rnl.dei.dms.exceptions;

public enum ErrorMessage {

	NO_SUCH_PERSON("Não existe nenhuma pessoa com o ID %s", 1001),
	NO_SUCH_COURSE("Não existe nenhum curso com o ID %s", 1002),
	NO_SUCH_UC("Não existe nenhuma UC com o ID %s", 1003),
	NO_SUCH_MEMBERSHIP("A pessoa com ID %s não é membro desta UC.", 1010),
	NO_SUCH_TEST("Não existe nenhum teste com o ID %s", 1011),
	NO_SUCH_PROJECT("Não existe nenhum projeto com o ID %s", 1012),
	NO_SUCH_GROUP("Não exites nenhum grupo com o ID %s", 1016),
	NO_STUDENTS_IN_UC("Não existe nenhuma pessoa inscrita nesta UC", 1018),
	PERSON_NAME_NOT_VALID("O nome da pessoa especificado não é válido.", 1004),
	PERSON_ALREADY_EXISTS("Já existe uma pessoa com o ID %s", 1005),
	INVALID_CREDENTIALS("Email ou password inválidos.", 1006),
	INVALID_REGENTE("A pessoa especificada não é um professor regente.", 1007),
	INVALID_GROUP_SIZE("Tamanho de grupo inválido", 1013),
	NOT_AUTHORIZED("Não está autorizado a executar esta operação.", 1008),
	MEMBERSHIP_ALREADY_EXISTS("A pessoa %s já é membro desta UC.", 1009),
	GROUP_NOT_ALLOWED("Este projeto não permite grupos", 1014),
	GROUP_SIZE_EXCEEDED("O grupo excede o tamanho máximo permitido.", 1015),
	GROUPS_ALREADY_EXIST("Os grupos para este projeto já estão feitos", 1019),
	MEMBER_NOT_STUDENT("A pessoa com ID %s não é estudante desta UC.", 1017);

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
