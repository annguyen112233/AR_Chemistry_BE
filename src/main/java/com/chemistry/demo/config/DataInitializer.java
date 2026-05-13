package com.chemistry.demo.config;

import com.chemistry.demo.aspect.NoLogging;
import com.chemistry.demo.config.seeder.DataSeeder;
import com.chemistry.demo.utils.SeederExecutionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@NoLogging
public class DataInitializer implements CommandLineRunner {

        private final List<DataSeeder> seeders;

        @Override
        public void run(String... args) {
                SeederExecutionContext.enter();
                try {
                        seeders.stream()
                                        .sorted(Comparator.comparingInt(DataSeeder::getOrder))
                                        .forEach(DataSeeder::seed);
                } finally {
                        SeederExecutionContext.exit();
                }
        }
}
