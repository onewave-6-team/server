package org.example.explog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class FolderController {

    @GetMapping("/folders")
    public void getFolders() {
        return;
    }
}
