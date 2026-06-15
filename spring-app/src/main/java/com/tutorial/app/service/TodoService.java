package com.tutorial.app.service;

import com.tutorial.app.domain.Todo;
import com.tutorial.app.domain.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public Todo findById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found: " + id));
    }

    @Transactional
    public Todo create(Todo todo) {
        return todoRepository.save(todo);
    }

    @Transactional
    public Todo update(Long id, boolean done) {
        Todo todo = findById(id);
        todo.setDone(done);
        return todo;
    }

    @Transactional
    public void delete(Long id) {
        todoRepository.deleteById(id);
    }
}
