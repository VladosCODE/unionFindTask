package org.example;

import org.example.entities.UnionFind;
import org.example.util.FileWorker;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        try {
            if (args.length < 1) {
                log.info("Пожалуйста, укажите имя файла.");
                return;
            }

            String fileName = args[0];

            List<String> lines = FileWorker.readFile(fileName);

            List<Set<String>> groups = groupLines(lines);

            String outputFilePath = "output.txt";
            FileWorker.writeInFile(outputFilePath, groups);
        }
        catch (IllegalArgumentException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Проверка некорректности строки
     *
     * @param line Строка
     * @return Флаг соответствия паттерну
     */
    public static boolean isValidLine(String line) {
        // Регулярное выражение для проверки, что каждый элемент обёрнут в кавычки и разделён точкой с запятой
        String regex = "^\"([^\"]*)\"(;\")([^\"]*\")*$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(line).matches();
    }

    /**
     * Метод для группировки строк
     *
     * @param lines - все строки, прочитанные из файла
     * @return структура сгруппированных строк
     */
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

}