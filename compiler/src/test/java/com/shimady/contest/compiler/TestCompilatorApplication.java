package com.shimady.contest.compiler;

import org.springframework.boot.SpringApplication;

public class TestCompilatorApplication {

	public static void main(String[] args) {
		SpringApplication.from(CompilerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
