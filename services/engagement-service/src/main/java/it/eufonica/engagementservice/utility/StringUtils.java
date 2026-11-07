package it.eufonica.engagementservice.utility;

public class StringUtils {
    /**
     * Takes a string in snake_case and returns a string in camelCase
     *
     * @param input the string in snake_case
     *
     * @return the string rewritten in camelCase
     */
    public static String toCamelCase(String input) {
        String[] parts = input.split("_");
        StringBuilder result = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                result.append(Character.toUpperCase(parts[i].charAt(0)));
                result.append(parts[i].substring(1));
            }
        }

        return result.toString();
    }

}
