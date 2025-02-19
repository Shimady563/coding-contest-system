package com.shimady.contest.compiler;

import com.shimady.contest.compiler.service.CompilerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/")
    public void run() {
        String code = """
                #include <iostream>
                int main() {
                    int x;
                    int y;
                    std::cin >> x;
                    std::cin >> y;
                    std::cout << (x + y) << std::endl;
                    return 0;
                }
                """;
        CompilerService.compileAndRun(code);
    }
}
