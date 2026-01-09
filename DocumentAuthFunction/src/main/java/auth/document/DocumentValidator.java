package auth.document;

public final class DocumentValidator {

    private DocumentValidator() {}

    public static String validateAndClean(String document) {
        if (document == null || document.isBlank()) {
            throw new IllegalArgumentException("Documento é obrigatório");
        }

        String cleaned = document.replaceAll("\\D", "");

        if (cleaned.length() == 11 && isCpfValid(cleaned)) {
            return cleaned;
        }

        if (cleaned.length() == 14 && isCnpjValid(cleaned)) {
            return cleaned;
        }

        throw new IllegalArgumentException("Documento inválido");
    }

    private static boolean isCpfValid(String cpf) {
        if (cpf.chars().distinct().count() == 1) return false;

        int d1 = calculateDigit(cpf, new int[]{10,9,8,7,6,5,4,3,2});
        int d2 = calculateDigit(cpf, new int[]{11,10,9,8,7,6,5,4,3,2});

        return cpf.charAt(9) - '0' == d1 && cpf.charAt(10) - '0' == d2;
    }

    private static boolean isCnpjValid(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) return false;

        int d1 = calculateDigit(cnpj, new int[]{5,4,3,2,9,8,7,6,5,4,3,2});
        int d2 = calculateDigit(cnpj, new int[]{6,5,4,3,2,9,8,7,6,5,4,3,2});

        return cnpj.charAt(12) - '0' == d1 && cnpj.charAt(13) - '0' == d2;
    }

    private static int calculateDigit(String number, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += (number.charAt(i) - '0') * weights[i];
        }
        int digit = 11 - (sum % 11);
        return digit >= 10 ? 0 : digit;
    }
}
