package com.chemistry.demo.config;

import com.chemistry.demo.config.seeder.DataSeeder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final List<DataSeeder> seeders;

        @Override
        public void run(String... args) {
                seeders.stream()
                                .sorted(Comparator.comparingInt(DataSeeder::getOrder))
                                .forEach(DataSeeder::seed);
        }
}