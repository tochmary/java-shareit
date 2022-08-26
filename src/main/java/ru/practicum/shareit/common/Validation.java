package ru.practicum.shareit.common;

import ru.practicum.shareit.common.exception.BadRequestException;

public class Validation {
    public static void checkRequestParam(String param, Integer value) {
        switch (param) {
            case "from":
                if (value < 0) {
                    throw new BadRequestException("Параметр " + param + " должен быть больше или равно 0!");
                }
                break;
            case "size":
                if (value <= 0) {
                    throw new BadRequestException("Параметр " + param + " должен быть больше 0!");
                }
                break;
        }
    }
}
