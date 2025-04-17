package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAllUsers() {
        return users.values();
    }

    public boolean checkUser(long id) {
        if (users.containsKey(id)) {return true;}
        return false;
    }

    public User createUser(User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        } else if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        // формируем дополнительные данные
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        } else if (users.values().stream().anyMatch(u -> u.getEmail().equals(newUser.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (!(newUser.getUsername() == null) && !(newUser.getUsername().isBlank()) && !(newUser.getPassword() == null)
                    && !(newUser.getPassword().isBlank()) && !(newUser.getEmail() == null)
                    && !(newUser.getEmail().isBlank())) {
                oldUser.setUsername(newUser.getUsername());

                //if (!(newUser.getPassword() == null) && !(newUser.getPassword().isBlank())) {
                oldUser.setPassword(newUser.getPassword());
                //}
                //if (!(newUser.getEmail() == null) && !(newUser.getEmail().isBlank())) {
                oldUser.setEmail(newUser.getEmail());
                //}
            }
            return oldUser;
        }
        throw new NotFoundException("Пост с id = " + newUser.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
