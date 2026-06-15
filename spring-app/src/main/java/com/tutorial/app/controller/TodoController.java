package com.tutorial.app.controller;

import com.tutorial.app.domain.Todo;
import com.tutorial.app.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // 전체 조회
    @GetMapping
    public List<Todo> getAll() {
        return todoService.findAll();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public Todo getOne(@PathVariable Long id) {
        return todoService.findById(id);
    }

    // 생성
    @PostMapping
    public ResponseEntity<Todo> create(@Valid @RequestBody Todo todo) {
        Todo saved = todoService.create(todo);
        return ResponseEntity.ok(saved);
    }

    // 완료 처리
    @PatchMapping("/{id}/done")
    public Todo complete(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        boolean done = Boolean.TRUE.equals(body.get("done"));
        return todoService.update(id, done);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
