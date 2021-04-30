package org.suai.universityapp.utils;

/**
 * Класс, содержащий методы обработки текста сообщений
 */
public class StringFormatter {

    /**
     * Метод форматирования строки с сообщением
     * @param input исходный текст
     * @return отформатированное сообщение
     */
    public static String format(CharSequence input){

        String text = input.toString();
        int beginCounter = 0;

        while ((text.charAt(beginCounter) == ' ' ||  text.charAt(beginCounter) == '\n' )) {

            if (beginCounter + 1 == text.length())
                return "";

            beginCounter++;
        }

        text = text.substring(beginCounter, text.length());
        char[] tmp = text.toCharArray();

        StringBuilder result = new StringBuilder();

        int counter = 0;

        for(int i = 0; i < text.length(); i++){

            if (tmp[i] != ' ' && tmp[i] != '\n') {

                if (counter == 1){
                    result.append(tmp[i-1]);
                } else if (counter > 3){
                    result.append(tmp[i-1]);
                }

                result.append(tmp[i]);
                counter = 0;
            } else {
                counter++;
            }
        }
        return result.toString();
    }
}