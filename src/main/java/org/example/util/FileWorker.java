package org.example.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility класс для работы с файлом
 */
public class FileWorker {

    private FileWorker(){}
    /**
     * Метод для чтения файла
     *
     * @param pathFile Путь файла
     * @return прочитанные строки файла
     */
    public static List<String> readFile(String pathFile) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(pathFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка  чтения файла");
        }
        return lines;
    }

    /**
     * Метод для записи в файле групп
     *
     * @param outputFilePath - путь к выходному файлу
     * @param resultGroups - объединенные группы
     */
    public static void writeInFile(String outputFilePath,List<Set<String>> resultGroups){
        // Записываем в файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write(resultGroups.size() + "\n"); // Число групп с более чем 1 элементом
            for (int i = 0; i < resultGroups.size(); i++) {
                writer.write("Группа " + (i + 1) + "\n");
                for (String groupLine : resultGroups.get(i)) {
                    writer.write(groupLine + "\n");
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка записи в файл");
        }
    }
}
