package com.chemistry.demo.config.seeder;

import com.chemistry.demo.aspect.NoLogging;

@NoLogging
public interface DataSeeder {
    void seed();

    int getOrder();
}
