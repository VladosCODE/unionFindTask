package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {

        try {
            /*if (args.length < 1) {
                System.out.println("Пожалуйста, укажите имя файла.");
                return;
            }*/

            //args[0]
            String fileName = "/Users/vladsemenenok/Desktop/lng.txt";

            List<String> lines = readFile(fileName);

            List<Set<String>> groups = groupLines(lines);

            String outputFilePath = "output.txt";
            writeInFile(outputFilePath, groups);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

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

    // Проверка корректности строки: каждый элемент в кавычках и разделен ;
    public static boolean isValidLine(String line) {
        // Регулярное выражение для проверки, что каждый элемент обёрнут в кавычки и разделён точкой с запятой
        String regex = "^\"([^\"]*)\"(;\")([^\"]*\")*$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(line).matches();
    }

    // Метод для группировки строк
    public static List<Set<String>> groupLines(List<String> lines) {
        int n = lines.size();
        UnionFind uf = new UnionFind(n);

        // Мапа для связывания значений с номерами строк
        Map<String, List<Integer>> valueToLinesMap = new HashMap<>();

        for (int i = 0; i < n; i++) {
            String line = lines.get(i);
            // Пропускаем некорректные строки
            if (!isValidLine(line)) {
                continue;
            }

            String[] values = line.split(";");
            for (String value : values) {
                if (!value.isEmpty()) {
                    valueToLinesMap.computeIfAbsent(value, k -> new ArrayList<>()).add(i);
                }
            }
        }

        // Объединяем строки, которые имеют одинаковые значения
        for (List<Integer> indexes : valueToLinesMap.values()) {
            for (int i = 1; i < indexes.size(); i++) {
                uf.union(indexes.get(0), indexes.get(i));
            }
        }

        // Группируем строки по их корневым родителям
        Map<Integer, Set<String>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            groups.computeIfAbsent(root, k -> new HashSet<>()).add(lines.get(i));
        }

        // Сортируем группы по количеству элементов (по убыванию)
        List<Set<String>> resultGroups = new ArrayList<>(groups.values());
        resultGroups.removeIf(group -> group.size() <= 1); // Оставляем только группы с более чем 1 элементом
        resultGroups.sort((a, b) -> Integer.compare(b.size(), a.size())); // Сортировка по размеру группы

        return resultGroups;
    }

    private static void writeInFile(String outputFilePath,List<Set<String>> resultGroups){
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