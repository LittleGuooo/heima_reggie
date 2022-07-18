package com.xu.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


@SpringBootTest
@Slf4j
class CommonControllerTest {

    @Test
    public void FileTest() throws IOException {
        File file = new File("a.txt");
        Writer writer = new FileWriter(file);
        writer.write("abc");
        writer.close();
    }
}