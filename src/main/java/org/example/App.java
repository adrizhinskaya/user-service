package org.example;

import org.example.model.User;
import org.example.service.UserService;

import java.util.Scanner;

public class App {
    static UserService userService = new UserService();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Введите номер операции\n1 - create\n2 - read\n3 - update\n4 - delete\n5 - exit");
            int command = scanner.nextInt();

            switch (command) {
                case 1:
                    createUser(scanner);
                    break;
                case 2:
                    readUser(scanner);
                    break;
                case 3:
                    updateUser(scanner);
                    break;
                case 4:
                    deleteUser(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Неизвестная команда");
            }
        }
    }

    private static void createUser(Scanner scanner) {
        User user = new User();
        System.out.print("Введите имя: ");
        user.setName(scanner.next());
        System.out.print("Введите email: ");
        user.setEmail(scanner.next());
        System.out.print("Введите возраст: ");
        user.setAge(scanner.nextInt());
        userService.add(user);
        System.out.println("Пользователь создан");
    }

    private static void readUser(Scanner scanner) {
        System.out.print("Введите ID пользователя: ");
        Integer id = scanner.nextInt();
        User user = userService.getById(id);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("Пользователь не найден");
        }
    }

    private static void updateUser(Scanner scanner) {
        User user = new User();
        System.out.print("Введите ID пользователя для обновления: ");
        user.setId(scanner.nextInt());
        System.out.print("Введите новое имя: ");
        user.setName(scanner.next());
        System.out.print("Введите новый email: ");
        user.setEmail(scanner.next());
        System.out.print("Введите новый возраст: ");
        user.setAge(scanner.nextInt());
        try {
            userService.update(user);
            System.out.println("Пользователь обновлен");
        } catch (RuntimeException e) {
            System.out.println("Пользователь не найден");
        }
    }

    private static void deleteUser(Scanner scanner) {
        System.out.print("Введите ID пользователя для удаления: ");
        Integer id = scanner.nextInt();
        userService.deleteById(id);
        System.out.println("Пользователь удален");
    }
}