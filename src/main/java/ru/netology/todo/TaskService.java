package ru.netology.todo;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Task create(TaskRequest request) {
        long id = sequence.incrementAndGet();
        Task task = new Task(id, request.title().trim(), normalize(request.description()), Boolean.TRUE.equals(request.completed()));
        tasks.put(id, task);
        return task;
    }

    public List<Task> findAll() {
        return tasks.values().stream().sorted(Comparator.comparingLong(Task::id)).toList();
    }

    public Task update(long id, TaskRequest request) {
        if (!tasks.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Task task = new Task(id, request.title().trim(), normalize(request.description()), Boolean.TRUE.equals(request.completed()));
        tasks.put(id, task);
        return task;
    }

    public void delete(long id) {
        if (tasks.remove(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
